package io.loom.core.messaging;

public class MessageWithComplexTypeProperty implements Message {
    private ComplexObject complexField;

    public ComplexObject getComplexProperty() {
        return complexField;
    }

    public void setComplexProperty(ComplexObject complexValue) {
        complexField = complexValue;
    }
}
