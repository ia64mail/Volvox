package volvox.messenger.server.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base class for any DTO message between server and client.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "$class"
)
public abstract class AbstractDto {
}
