package volvox.messenger.server;

import volvox.messenger.server.dto.AbstractDto;

import java.util.UUID;

public class WebSocketMessage {
    private UUID sessionId;
    private UUID messageId;
    private AbstractDto dto;

    public WebSocketMessage(UUID sessionId, AbstractDto dtoPayload) {
        this.sessionId = sessionId;
        this.messageId = UUID.randomUUID();
        this.dto = dtoPayload;
    }

    /**
     * Serialization constructor.
     */
    public WebSocketMessage() {
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public AbstractDto getDto() {
        return dto;
    }

    public void setDto(AbstractDto dto) {
        this.dto = dto;
    }
}
