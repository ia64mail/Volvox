package volvox.messenger.server;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Flow;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketServer extends AllDirectives {
    private static Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    private static final String wsPathConfig = "ws.path";

    private final ActorSystem actorSystem;

    public WebSocketServer(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    public Route createRoute() {
        final var config = ConfigFactory.load();
        final String routePath = config.getString(wsPathConfig);

        return route(
                path(routePath, () -> handleWebSocketMessages(greeter())),
                //TODO To be removed!
                path("ping", () ->
                        get(this::healthCheck))

        );
    }

    //TODO To be removed!
    private Route healthCheck() {
        return complete("pong!");
    }

    /**
     * A handler that treats incoming messages as a name,
     * and responds with a greeting to that name
     */
    private Flow<Message, Message, NotUsed> greeter() {
        var flowManager = actorSystem.actorOf(Props.create(PubSubActor.class));

        //response
        Source<Message, NotUsed> source =
                Source.<Outgoing>actorRef(5, OverflowStrategy.fail())
                        .map((outgoing) -> (Message) TextMessage.create(outgoing.getMessageText()))
                        .<NotUsed>mapMaterializedValue(destinationRef -> {
                            flowManager.tell(new OutgoingDestination(destinationRef), ActorRef.noSender());
                            return NotUsed.getInstance();
                        });
        // request
        Sink<Message, NotUsed> sink =
                Flow.<Message>create()
                        .map((msg) -> new Incoming(msg.asTextMessage().getStrictText()))
                        .to(Sink.actorRef(flowManager, PoisonPill.getInstance()));

        Flow<Message, Message, NotUsed> flow = Flow.fromSinkAndSource(sink, source);

        logger.info("Flow has been created! Managing actor: " + flowManager);

        return flow;

    }
}
