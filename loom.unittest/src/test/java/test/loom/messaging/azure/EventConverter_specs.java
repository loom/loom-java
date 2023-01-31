package test.loom.messaging.azure;

import static org.assertj.core.api.Assertions.assertThat;

import autoparams.AutoSource;
import autoparams.customization.Customization;
import autoparams.mockito.MockitoCustomizer;
import com.azure.messaging.eventhubs.EventData;
import java.util.Optional;
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
        String expected = message.getPredecessorId();
        assertThat(actual.getProperties().get("PredecessorId")).isEqualTo(expected);
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    public void tryRestoreMessage_returns_empty_if_type_cannot_be_resolved(
        JacksonJsonStrategy jsonStrategy,
        Message message,
        String invalidType
    ) {
        // Arrange
        TypeFormatter formatter = TypeFormatter.forTypeName();
        TypeResolver resolver = TypeResolver.forClassName();
        TypeStrategy typeStrategy = TypeStrategy.create(formatter, resolver);
        EventConverter sut = new EventConverter(jsonStrategy, typeStrategy);
        EventData eventData = sut.convertToEvent(message);
        eventData.getProperties().put("Type", invalidType);

        // Act
        Optional<Message> actual = sut.tryRestoreMessage(eventData);

        // Assert
        assertThat(actual).isEmpty();
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    public void tryRestoreMessage_correctly_restores_message_data(
        JacksonJsonStrategy jsonStrategy,
        Message message
    ) {
        // Arrange
        TypeFormatter formatter = TypeFormatter.forTypeName();
        TypeResolver resolver = TypeResolver.forClassName();
        TypeStrategy typeStrategy = TypeStrategy.create(formatter, resolver);
        EventConverter sut = new EventConverter(jsonStrategy, typeStrategy);
        EventData eventData = sut.convertToEvent(message);

        // Act
        Optional<Message> actual = sut.tryRestoreMessage(eventData);

        // Assert
        assertThat(actual).isNotEmpty();
        actual.ifPresent(x -> {
            assertThat(x).isNotNull();
            assertThat(x.getData())
                .usingRecursiveComparison()
                .isEqualTo(jsonStrategy.deserialize(
                    message.getData().getClass(),
                    eventData.getBodyAsString()));
        });
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    public void tryRestoreMessage_correctly_restores_process_id(
        JacksonJsonStrategy jsonStrategy,
        Message message
    ) {
        // Arrange
        TypeFormatter formatter = TypeFormatter.forTypeName();
        TypeResolver resolver = TypeResolver.forClassName();
        TypeStrategy typeStrategy = TypeStrategy.create(formatter, resolver);
        EventConverter sut = new EventConverter(jsonStrategy, typeStrategy);
        EventData eventData = sut.convertToEvent(message);

        // Act
        Optional<Message> actual = sut.tryRestoreMessage(eventData);

        // Assert
        String expected = (String) eventData.getProperties().get("ProcessId");
        //noinspection OptionalGetWithoutIsPresent
        assertThat(actual.get().getProcessId()).isEqualTo(expected);
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    public void tryRestoreMessage_correctly_restores_predecessor_id(
        JacksonJsonStrategy jsonStrategy,
        Message message
    ) {
        // Arrange
        TypeFormatter formatter = TypeFormatter.forTypeName();
        TypeResolver resolver = TypeResolver.forClassName();
        TypeStrategy typeStrategy = TypeStrategy.create(formatter, resolver);
        EventConverter sut = new EventConverter(jsonStrategy, typeStrategy);
        EventData eventData = sut.convertToEvent(message);

        // Act
        Optional<Message> actual = sut.tryRestoreMessage(eventData);

        // Assert
        String expected = (String) eventData.getProperties().get("PredecessorId");
        //noinspection OptionalGetWithoutIsPresent
        assertThat(actual.get().getPredecessorId()).isEqualTo(expected);
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    public void tryRestoreMessage_returns_empty_if_message_data_is_null(
        JacksonJsonStrategy jsonStrategy,
        TypeStrategy typeStrategy
    ) {
        // Arrange
        EventConverter sut = new EventConverter(jsonStrategy, typeStrategy);

        // Act
        EventData eventData = new EventData();
        Optional<Message> actual = sut.tryRestoreMessage(eventData);

        // Assert
        assertThat(actual).isEmpty();
    }
}
