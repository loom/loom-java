package test.loom.eventsourcing;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import autoparams.AutoSource;
import java.util.List;
import loom.eventsourcing.EventHandler;
import loom.eventsourcing.EventReader;
import loom.eventsourcing.Rehydrator;
import loom.eventsourcing.Snapshot;
import org.junit.jupiter.params.ParameterizedTest;
import test.loom.PasswordHashChanged;
import test.loom.UnknownEvent;
import test.loom.User;
import test.loom.UserCreated;

class Rehydrator_specs {

    @ParameterizedTest
    @AutoSource
    void sut_correctly_returns_snapshot(
        InMemoryEventStore eventStore,
        String streamId,
        UserCreated userCreated,
        PasswordHashChanged passwordHashChanged
    ) {
        // Arrange
        List<Object> events = asList(userCreated, passwordHashChanged);
        eventStore.collectEvents(User.class, streamId, events);
        UserHeadspring sut = new UserHeadspring(eventStore);

        // Act
        Snapshot<User> snapshot = sut.rehydrateState(streamId);

        // Assert
        assertThat(snapshot).isNotNull();
        assertThat(snapshot.getStreamId()).isEqualTo(streamId);
        assertThat(snapshot.getVersion()).isEqualTo(2);
        User state = snapshot.getState();
        assertThat(state.getUsername()).isEqualTo(userCreated.getUsername());
        assertThat(state.getPasswordHash()).isEqualTo(passwordHashChanged.getPasswordHash());
    }

    @ParameterizedTest
    @AutoSource
    void sut_throws_correct_exception_for_unknown_event(
        InMemoryEventStore eventStore,
        String streamId,
        UserCreated userCreated,
        UnknownEvent unknownEvent,
        PasswordHashChanged passwordHashChanged
    ) {
        // Arrange
        List<Object> events = asList(
            userCreated,
            unknownEvent,
            passwordHashChanged);
        eventStore.collectEvents(User.class, streamId, events);
        UserHeadspring sut = new UserHeadspring(eventStore);

        // Act/Assert
        assertThatThrownBy(() -> sut.rehydrateState(streamId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("No event handler registered for event")
            .hasMessageContaining(unknownEvent.getClass().getName());
    }

    @ParameterizedTest
    @AutoSource
    void sut_denies_handler_of_generic_type(
        InMemoryEventStore eventStore
    ) {
        assertThatThrownBy(() -> new CorruptRehydrator(eventStore))
            .isInstanceOf(RuntimeException.class)
            .hasMessage(
                "Non-generic class expected for event handler."
                + " Type argument cannot be resolved."
                + " Make sure to specify the type argument"
                + " when declaring the event handler class.");
    }

    @ParameterizedTest
    @AutoSource
    void sut_correctly_applies_stream_id_when_creates_seed(
        InMemoryEventStore eventStore,
        String streamId
    ) {
        UserHeadspring sut = new UserHeadspring(eventStore);
        Snapshot<User> snapshot = sut.rehydrateState(streamId);
        assertThat(snapshot.getState().getId()).isEqualTo(streamId);
    }

    static class CorruptRehydrator extends Rehydrator<User> {

        public CorruptRehydrator(EventReader eventReader) {
            super(
                eventReader,
                User::seedFactory,
                singletonList(new GenericEventHandler<User, UserCreated>()));
        }
    }

    static class GenericEventHandler<S, E> implements EventHandler<S, E> {

        @Override
        public S handleEvent(S state, E event) {
            return state;
        }
    }
}
