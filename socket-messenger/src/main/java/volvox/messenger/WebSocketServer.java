package volvox.messenger;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.*;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.japi.JavaPartialFunction;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.management.AkkaManagement;
import akka.management.cluster.bootstrap.ClusterBootstrap;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import volvox.common.utils.ActorNameUtils;

import java.util.concurrent.CompletionStage;


public class WebSocketServer extends AllDirectives {
    private static Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    public static void main(String[] args) {
        var utils = new ActorNameUtils();
        final ActorSystem system = ActorSystem.create(utils.toLowerCase("volvox"));

        //See https://github.com/akka/akka-management/issues/282
        if (system.settings().config().getStringList("akka.cluster.seed-nodes").size() == 0) {
            // Akka Management hosts the HTTP routes used by bootstrap
            AkkaManagement.get(system).start();
            // Starting the bootstrap process needs to be done explicitly
            ClusterBootstrap.get(system).start();
        }

        try {
            logger.info("Akka stared successfully ...");

            final String hostName = system.settings().config().getString("akka.remote.artery.canonical.hostname");
            final int port = 8080;

            final Http http = Http.get(system);
            Materializer materializer = ActorMaterializer.create(system);

            //In order to access all directives we need an instance where the routes are define.
            WebSocketServer app = new WebSocketServer();

            final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
            final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                    ConnectHttp.toHost(hostName, port), materializer);

//            binding
//                    .thenCompose(ServerBinding::unbind) // trigger unbinding from the port
//                    .thenAccept(unbound -> { /*NOOP!*/});

            logger.info("Listening HTTP on " + hostName + ":" + port);

        } catch (Exception ioe) {
            logger.error(ioe.toString());
            system.terminate();
        }
    }

    private Route createRoute() {
        return route(
                path("hello", () ->
                        get(() -> complete("<h1>Say hello to akka-http</h1>"))),
                path("ws", () ->
                        handleWebSocketMessages(greeter()))
        );
    }

    /**
     * A handler that treats incoming messages as a name,
     * and responds with a greeting to that name
     */
    public static Flow<Message, Message, NotUsed> greeter() {
        return
                Flow.<Message>create()
                        .collect(new JavaPartialFunction<Message, Message>() {
                            @Override
                            public Message apply(Message msg, boolean isCheck) throws Exception {
                                if (isCheck) {
                                    if (msg.isText()) {
                                        return null;
                                    } else {
                                        throw noMatch();
                                    }
                                } else {
                                    return handleTextMessage(msg.asTextMessage());
                                }
                            }
                        });
    }

    public static TextMessage handleTextMessage(TextMessage msg) {
        if (msg.isStrict()) // optimization that directly creates a simple response...
        {
            return TextMessage.create(msg.getStrictText());
        } else // ... this would suffice to handle all text messages in a streaming fashion
        {
            return TextMessage.create(Source.single("").concat(msg.getStreamedText()));
        }
    }
}
