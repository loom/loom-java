package io.loom.core.event;

import io.loom.core.messaging.Message;
import org.junit.Assert;
import org.junit.Test;

public class DomainEventSpecs {
    @Test
    public void sut_extends_Message() {
        Class<?>[] interfaces = DomainEvent.class.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (interfaces[i] == Message.class) {
                return;
            }
        }
        Assert.fail("DomainEvent should extends Message.");
    }
}
