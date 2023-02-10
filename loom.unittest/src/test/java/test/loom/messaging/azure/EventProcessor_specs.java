package test.loom.messaging.azure;

import static org.assertj.core.api.Assertions.assertThat;

import autoparams.AutoSource;
import autoparams.customization.Customization;
import autoparams.mockito.MockitoCustomizer;
import com.azure.messaging.eventhubs.EventData;
import java.util.Optional;
import java.util.Queue;
import loom.json.JacksonJsonStrategy;
import loom.messaging.Message;
import loom.messaging.MessageHandler;
import loom.messaging.azure.EventConverter;
import loom.messaging.azure.EventProcessor;
import loom.type.TypeFormatter;
import loom.type.TypeResolver;
import loom.type.TypeStrategy;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mockito;

public class EventProcessor_specs {

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    public void sut_processes_event_correctly(
        JacksonJsonStrategy jsonStrategy,
        MessageHandlerSpy spy,
        Message message
    ) {
        // Arrange
        TypeFormatter formatter = TypeFormatter.forTypeName();
        TypeResolver resolver = TypeResolver.forClassName();
        TypeStrategy typeStrategy = TypeStrategy.create(formatter, resolver);
        EventConverter eventConverter = new EventConverter(jsonStrategy, typeStrategy);

        EventData eventData = eventConverter.convertToEvent(message);
        EventProcessor sut = new EventProcessor(eventConverter, spy);

        // Act
        sut.process(eventData);

        // Assert
        Queue<Message> actual = spy.getLogs();
        assertThat(actual).hasSize(1);
        assertThat(actual.element()).usingRecursiveComparison().isEqualTo(message);
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    public void given_unknown_type_then_sut_ignores_message(
        JacksonJsonStrategy jsonStrategy,
        MessageHandlerSpy spy,
        EventData eventData,
        String type
    ) {
        // Arrange
        TypeFormatter formatter = TypeFormatter.forTypeName();
        TypeResolver resolver = TypeResolver.forClassName();
        TypeStrategy typeStrategy = TypeStrategy.create(formatter, resolver);
        EventConverter eventConverter = new EventConverter(jsonStrategy, typeStrategy);

        EventProcessor sut = new EventProcessor(eventConverter, spy);
        eventData.getProperties().put("Type", type);

        // Act
        sut.process(eventData);

        // Assert
        Queue<Message> actual = spy.getLogs();
        assertThat(actual).hasSize(0);
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    public void given_unhandlable_message_then_sut_ignores_it(
        JacksonJsonStrategy jsonStrategy,
        MessageHandler spy,
        Message message
    ) {
        // Arrange
        TypeFormatter formatter = TypeFormatter.forTypeName();
        TypeResolver resolver = TypeResolver.forClassName();
        TypeStrategy typeStrategy = TypeStrategy.create(formatter, resolver);
        EventConverter eventConverter = new EventConverter(jsonStrategy, typeStrategy);
        EventData eventData = eventConverter.convertToEvent(message);
        Mockito.when(spy.canHandle(message)).thenReturn(false);

        EventProcessor sut = new EventProcessor(eventConverter, spy);

        // Act
        sut.process(eventData);

        // Assert
        Mockito.verify(spy, Mockito.never()).handle(message);
    }
}
