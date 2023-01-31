package test.loom.messaging.azure;

import static org.assertj.core.api.Assertions.assertThat;

import autoparams.AutoSource;
import autoparams.customization.Customization;
import autoparams.mockito.MockitoCustomizer;
import com.azure.messaging.eventhubs.EventData;
import loom.json.JacksonJsonStrategy;
import loom.messaging.Message;
import loom.messaging.azure.EventConverter;
import loom.type.TypeFormatter;
import loom.type.TypeResolver;
import loom.type.TypeStrategy;
import org.junit.jupiter.params.ParameterizedTest;

public class EventConverter_specs {

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    public void convertToEvent_correctly_serializes_message_data(
        JacksonJsonStrategy jsonStrategy,
        Message message
    ) {
        // Arrange
        TypeFormatter formatter = TypeFormatter.forTypeName();
        TypeResolver resolver = TypeResolver.forClassName();
        TypeStrategy typeStrategy = TypeStrategy.create(formatter, resolver);
        EventConverter sut = new EventConverter(jsonStrategy, typeStrategy);

        // Act
        EventData actual = sut.convertToEvent(message);

        // Assert
        String expected = jsonStrategy.serialize(message.getData());
        assertThat(actual.getBodyAsString()).isEqualTo(expected);
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    public void convertToEvent_correctly_sets_Type_property(
        JacksonJsonStrategy jsonStrategy,
        Message message
    ) {
        // Arrange
        TypeFormatter formatter = TypeFormatter.forTypeName();
        TypeResolver resolver = TypeResolver.forClassName();
        TypeStrategy typeStrategy = TypeStrategy.create(formatter, resolver);
        EventConverter sut = new EventConverter(jsonStrategy, typeStrategy);

        // Act
        EventData actual = sut.convertToEvent(message);

        // Assert
        assertThat(actual.getProperties().get("Type")).isNotNull();

        String formatted = typeStrategy.formatTypeOf(message.getData());
        assertThat(actual.getProperties().get("Type")).isEqualTo(formatted);
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    public void convertToEvent_correctly_sets_ProcessId_property(
        JacksonJsonStrategy jsonStrategy,
        Message message
    ) {
        // Arrange
        TypeFormatter formatter = TypeFormatter.forTypeName();
        TypeResolver resolver = TypeResolver.forClassName();
        TypeStrategy typeStrategy = TypeStrategy.create(formatter, resolver);
        EventConverter sut = new EventConverter(jsonStrategy, typeStrategy);

        // Act
        EventData actual = sut.convertToEvent(message);

        // Assert
        assertThat(actual.getProperties().get("ProcessId")).isNotNull();
        assertThat(actual.getProperties().get("ProcessId")).isEqualTo(message.getProcessId());
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    public void convertToEvent_correctly_sets_PredecessorId_property(
        JacksonJsonStrategy jsonStrategy,
        Message message
    ) {
        // Arrange
        TypeFormatter formatter = TypeFormatter.forTypeName();
        TypeResolver resolver = TypeResolver.forClassName();
        TypeStrategy typeStrategy = TypeStrategy.create(formatter, resolver);
        EventConverter sut = new EventConverter(jsonStrategy, typeStrategy);

        // Act
        EventData actual = sut.convertToEvent(message);

        // Assert
        assertThat(actual.getProperties().get("PredecessorId")).isNotNull();
    }
}
