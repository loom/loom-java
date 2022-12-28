package loom.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class Message {
    private final String id;
    private final String processId;
    private final String initiator;
    private final String predecessorId;
    private final Object data;
}
