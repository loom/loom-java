package test.loom.eventsourcing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import autoparams.AutoSource;
import autoparams.customization.Customization;
import autoparams.mockito.MockitoCustomizer;
import java.util.Arrays;
import loom.eventsourcing.EventStore;
import loom.eventsourcing.StreamCommand;
import org.junit.jupiter.params.ParameterizedTest;
import test.loom.ChangePassword;
import test.loom.CreateUser;
import test.loom.PasswordHashChanged;
import test.loom.RaiseUnknownEvent;
import test.loom.UnknownEvent;
import test.loom.User;
import test.loom.UserCreated;
import test.loom.messaging.MessageBuilder;

class Headspring_specs {

    @ParameterizedTest
    @AutoSource
    void canHandle_returns_true_with_valid_data(
        InMemoryEventStore eventStore,
        MessageBuilder<StreamCommand<CreateUser>> message
    ) {
        UserHeadspring sut = new UserHeadspring(eventStore);
        boolean actual = sut.canHandle(message.build());
        assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @AutoSource
    void canHandle_returns_false_with_non_stream_command(
        InMemoryEventStore eventStore,
        MessageBuilder<String> message
    ) {
        UserHeadspring sut = new UserHeadspring(eventStore);
        boolean actual = sut.canHandle(message.build());
        assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @AutoSource
    void canHandle_returns_false_with_unknown_command_payload(
        InMemoryEventStore eventStore,
        MessageBuilder<StreamCommand<String>> message
    ) {
        UserHeadspring sut = new UserHeadspring(eventStore);
        boolean actual = sut.canHandle(message.build());
        assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @AutoSource
    void handle_collects_raised_event(
        InMemoryEventStore eventStore,
        MessageBuilder<StreamCommand<CreateUser>> message
    ) {
        UserHeadspring sut = new UserHeadspring(eventStore);
        String streamId = message.getData().getStreamId();
        CreateUser command = message.getData().getPayload();

        sut.handle(message.build());

        Iterable<Object> actual = eventStore.queryEvents(User.class, streamId);
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(Arrays.asList(
                new UserCreated(command.getUsername()),
                new PasswordHashChanged(command.getPassword())
            ));
    }

    @ParameterizedTest
    @AutoSource
    void handle_correctly_sets_version(
        InMemoryEventStore eventStore,
        UserCreated userCreated,
        PasswordHashChanged passwordHashChanged,
        MessageBuilder<StreamCommand<ChangePassword>> message
    ) {
        // Arrange
        eventStore.collectEvents(
            User.class,
            message.getData().getStreamId(),
            Arrays.asList(userCreated, passwordHashChanged));

        UserHeadspring sut = new UserHeadspring(eventStore);

        // Act/Assert
        assertThatNoException().isThrownBy(() -> sut.handle(message.build()));
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void handle_correctly_sets_context_data(
        EventStore eventStore,
        UserCreated userCreated,
        PasswordHashChanged passwordHashChanged,
        MessageBuilder<StreamCommand<ChangePassword>> message
    ) {
        UserHeadspring sut = new UserHeadspring(eventStore);

        sut.handle(message.build());

        verify(eventStore).collectEvents(
            any(),
            eq(message.getProcessId()),
            eq(message.getInitiator()),
            any(),
            any(),
            any(long.class),
            any());
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void handle_correctly_sets_predecessor_id(
        EventStore eventStore,
        UserCreated userCreated,
        PasswordHashChanged passwordHashChanged,
        MessageBuilder<StreamCommand<ChangePassword>> message
    ) {
        UserHeadspring sut = new UserHeadspring(eventStore);

        sut.handle(message.build());

        verify(eventStore).collectEvents(
            any(),
            any(),
            any(),
            eq(message.getId()),
            any(),
            any(long.class),
            any());
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void handle_fails_if_command_payload_is_unknonwn(
        UserHeadspring sut,
        MessageBuilder<StreamCommand<String>> message
    ) {
        assertThatException()
            .isThrownBy(() -> sut.handle(message.build()))
            .withMessageContaining("Unsupported command payload type")
            .withMessageContaining(String.class.getName());
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void handle_fails_if_unknown_event_produced(
        UserHeadspring sut,
        MessageBuilder<StreamCommand<RaiseUnknownEvent>> message
    ) {
        assertThatException()
            .isThrownBy(() -> sut.handle(message.build()))
            .withMessageContaining("Event that cannot be handled was produced")
            .withMessageContaining(UnknownEvent.class.getName());
    }
}
