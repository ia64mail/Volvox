package volvox.messenger.server.dto;

import java.time.Instant;

/**
 * Test DTO message
 */
public class StubDto extends AbstractDto {
    private long createdAt;
    private long hopCount;

    public StubDto(Instant createdAt) {
        this.createdAt = createdAt.toEpochMilli();
    }

    /**
     * Serialization constructor.
     */
    public StubDto() {
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getHopCount() {
        return hopCount;
    }

    public void setHopCount(long hopCount) {
        this.hopCount = hopCount;
    }

    public void incrementHopCount() {
        setHopCount(getHopCount() + 1);
    }
}
