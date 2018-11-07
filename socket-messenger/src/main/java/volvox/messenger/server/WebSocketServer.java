package volvox.messenger.server;

import akka.NotUsed;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.stream.javadsl.Flow;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.javadsl.Source;
import com.typesafe.config.ConfigFactory;

import java.util.Collections;

public class WebSocketServer extends AllDirectives {
    private static final String wsPathConfig = "ws.path";

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
    private static Flow<Message, Message, NotUsed> greeter() {
        return
                Flow.of(Message.class).mapConcat(msg -> {
                    if (msg instanceof TextMessage) {
                        final TextMessage tm = (TextMessage) msg;
                        final TextMessage ret = TextMessage.create(tm.getStreamedText());
                        return Collections.singletonList(ret);
                    } else {
                        throw new IllegalArgumentException("Unsupported message type!");
                    }
                });
    }
}
