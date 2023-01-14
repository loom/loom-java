package test.loom.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import loom.messaging.Message;

@AllArgsConstructor
@Getter
@Builder
public class MessageBuilder<T> {
    private String id;
    private String processId;
    private String initiator;
    private String predecessorId;
    private T data;

    public Message build() {
        return new Message(id, processId, initiator, predecessorId, data);
    }
}
