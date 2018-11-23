package volvox.messenger.server;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import volvox.messenger.server.dto.StubDto;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

public class PubSubActor extends AbstractActor {
    private final LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);

    private ActorRef handlerActor;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(WebSocketServer.OnConnected.class, this::OnConnected)
                .match(WebSocketServer.Incoming.class, this::onReceive)
                .match(WebSocketServer.OnDisconnected.class, this::OnDisconnected)
                .build();
    }

    /**
     * On connect with new client handler.
     * <p>
     * Initialize connection here.
     *
     * @param signal
     */
    private void OnConnected(WebSocketServer.OnConnected signal) {
        handlerActor = signal.getHandlerRef();

        logger.info("[OnConnected] {}", signal);

        //TODO review: initiate new ping-pong interaction
        var payload = toJson(mapper, new WebSocketMessage(UUID.randomUUID(), new StubDto(Instant.now())));
        handlerActor.tell(new WebSocketServer.Outgoing(payload), ActorRef.noSender());
    }

    /**
     * On receive new incoming message handler.
     * <p>
     * Every incoming message has text payload that could be de-serialized into WebSocketMessage
     * with custom AbstractDto inside.
     *
     * @param incoming incoming socket data with text payload
     */
    private void onReceive(WebSocketServer.Incoming incoming) {
        logger.info("[Incoming] {}", incoming);

        //parse payload into socket message
        WebSocketMessage webSocketMessage = fromJson(incoming.getPayload(), mapper);

        //TODO review: extract DTO from socket message and perform required business logic here
        StubDto stubDto = (StubDto) webSocketMessage.getDto();
        stubDto.incrementHopCount();

        //TODO review: reply with the same DTO object
        if (handlerActor != null) {
            var payload = toJson(mapper, new WebSocketMessage(webSocketMessage.getSessionId(), stubDto));
            handlerActor.tell(new WebSocketServer.Outgoing(payload), ActorRef.noSender());
        }
    }

    /**
     * On disconnect with new client handler.
     * <p>
     * Dispose connection resources here.
     *
     * @param signal
     */
    private void OnDisconnected(WebSocketServer.OnDisconnected signal) {
        logger.info("[OnDisconnected] {}", signal);
    }

    private WebSocketMessage fromJson(String payload, ObjectMapper mapper) {
        WebSocketMessage dto = null;
        try {
            dto = mapper.readValue(payload, WebSocketMessage.class);
        } catch (IOException e) {
            logger.error("Can't read JSON value! {}", e.getMessage());
        }
        return dto;
    }

    private String toJson(ObjectMapper mapper, WebSocketMessage dto) {
        String payload = null;
        try {
            payload = mapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            logger.error("Can't write JSON value! {}", e.getMessage());
        }
        return payload;
    }
}
