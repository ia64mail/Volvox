package volvox.messenger.server;

import akka.NotUsed;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.japi.JavaPartialFunction;
import akka.stream.javadsl.Flow;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.javadsl.Source;
import com.typesafe.config.ConfigFactory;

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

    private static TextMessage handleTextMessage(TextMessage msg) {
        if (msg.isStrict()) // optimization that directly creates a simple response...
        {
            return TextMessage.create(msg.getStrictText());
        } else // ... this would suffice to handle all text messages in a streaming fashion
        {
            return TextMessage.create(Source.single("").concat(msg.getStreamedText()));
        }
    }
}
