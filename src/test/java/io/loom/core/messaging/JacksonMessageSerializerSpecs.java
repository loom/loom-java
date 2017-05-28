package io.loom.core.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class JacksonMessageSerializerSpecs {
    @Test
    public void sut_implements_MessageSerializer() {
        Assert.assertTrue(
                "JacksonMessageSerializer does not implement MessageSerializer.",
                implementsInterface(JacksonMessageSerializer.class, MessageSerializer.class));
    }

    private boolean implementsInterface(Class<?> typeUnderTest, Class<?> interfaceType) {
        return containsType(typeUnderTest.getInterfaces(), interfaceType);
    }

    private boolean containsType(Class<?>[] source, Class<?> type) {
        for (Class<?> t : source) {
            if (t.equals(type)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void sut_serializes_immutable_message_having_json_creator_correctly() {
        // Arrange
        ImmutableMessageWithJsonCreator message = new ImmutableMessageWithJsonCreator(1024, "foo");
        JacksonMessageSerializer sut = new JacksonMessageSerializer();

        // Act
        String value = sut.serialize(message);
        System.out.println("The serialized value is '" + value + "'.");
        Object actual = sut.deserialize(value);

        // Assert
        Assert.assertNotNull("The actual value is null.", actual);
        Assert.assertTrue(
                "The actual value is not an instance of ImmutableMessageWithJsonCreator.",
                actual instanceof ImmutableMessageWithJsonCreator);
        ImmutableMessageWithJsonCreator actualMessage = (ImmutableMessageWithJsonCreator)actual;
        Assert.assertEquals(message.getIntProperty(), actualMessage.getIntProperty());
        Assert.assertEquals(message.getStringProperty(), actualMessage.getStringProperty());
    }

    @Test
    public void serialize_has_guard_clause_for_null_message() {
        // Arrange
        JacksonMessageSerializer sut = new JacksonMessageSerializer();

        // Act
        IllegalArgumentException expected = null;
        try {
            sut.serialize(null);
        } catch (IllegalArgumentException e) {
            expected = e;
        }

        // Assert
        Assert.assertNotNull(
                "serialize() did not throw IllegalArgumentException.",
                expected != null);
        assert expected != null;
        Assert.assertTrue(
                "The error message should contain the name of the parameter 'message'.",
                expected.getMessage().contains("'message'"));
    }

    @Test
    public void deserialize_has_guard_clause_for_null_value() {
        // Arrange
        JacksonMessageSerializer sut = new JacksonMessageSerializer();

        // Act
        IllegalArgumentException expected = null;
        try {
            sut.deserialize(null);
        } catch (IllegalArgumentException e) {
            expected = e;
        }

        // Assert
        Assert.assertTrue(
                "deserialize() did not throw IllegalArgumentException.",
                expected != null);
        Assert.assertTrue(
                "The error message should contain the name of the parameter 'value'.",
                expected.getMessage().contains("'value'"));
    }

    @Test
    public void sut_serializes_mutable_message_not_having_json_creator_correctly() {
        // Arrange
        JacksonMessageSerializer sut = new JacksonMessageSerializer();
        MutableMessageWithoutJsonCreator message = new MutableMessageWithoutJsonCreator();
        message.setIntProperty(1024);
        message.setStringProperty("foo");

        // Act
        String value = sut.serialize(message);
        System.out.println("The serialized value is '" + value + "'.");
        Object actual = sut.deserialize(value);

        // Assert
        Assert.assertNotNull("The actual value is null.", actual);
        Assert.assertTrue(
                "The actual value is not an instance of ImmutableMessageWithJsonCreator.",
                actual instanceof MutableMessageWithoutJsonCreator);
        MutableMessageWithoutJsonCreator actualMessage = (MutableMessageWithoutJsonCreator)actual;
        Assert.assertEquals(message.getIntProperty(), actualMessage.getIntProperty());
        Assert.assertEquals(message.getStringProperty(), actualMessage.getStringProperty());
    }

    @Test
    public void serialize_throws_RuntimeException_for_JsonProcessingException() {
        // Arrange
        EmptyObject invalidMessage = new EmptyObject();
        JacksonMessageSerializer sut = new JacksonMessageSerializer();

        // Act
        RuntimeException expected = null;
        try {
            sut.serialize(invalidMessage);
        } catch (RuntimeException e) {
            expected = e;
        }

        // Assert
        Assert.assertTrue(
                "serialize() did not throw RuntimeException.",
                expected != null);
        Assert.assertTrue(
                "The cause was not set correctly.",
                expected.getCause() instanceof JsonProcessingException);
    }

    @Test
    public void deserialize_throws_RuntimeException_for_invalid_json() {
        // Arrange
        String value = "This is not a valid json document.";
        JacksonMessageSerializer sut = new JacksonMessageSerializer();

        // Act
        RuntimeException expected = null;
        try {
            sut.deserialize(value);
        } catch (RuntimeException e) {
            expected = e;
        }

        // Assert
        Assert.assertTrue(
                "deserialize() did not throw RuntimeException.",
                expected != null);
        Assert.assertTrue(
                "The cause was not set correctly.",
                expected.getCause() instanceof IOException);
    }

    @Test
    public void sut_serializes_final_immutable_message_having_json_creator_correctly() {
        // Arrange
        FinalImmutableMessageWithJsonCreator message =
                new FinalImmutableMessageWithJsonCreator(1024, "foo");
        JacksonMessageSerializer sut = new JacksonMessageSerializer();

        // Act
        String value = sut.serialize(message);
        System.out.println("The serialized value is '" + value + "'.");
        Object actual = sut.deserialize(value);

        // Assert
        Assert.assertNotNull("The actual value is null.", actual);
        Assert.assertTrue(
                "The actual value is not an instance of FinalImmutableMessageWithJsonCreator.",
                actual instanceof FinalImmutableMessageWithJsonCreator);
        FinalImmutableMessageWithJsonCreator actualMessage =
                (FinalImmutableMessageWithJsonCreator)actual;
        Assert.assertEquals(message.getIntProperty(), actualMessage.getIntProperty());
        Assert.assertEquals(message.getStringProperty(), actualMessage.getStringProperty());
    }
}
