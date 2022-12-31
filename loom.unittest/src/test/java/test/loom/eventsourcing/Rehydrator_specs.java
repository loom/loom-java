package test.loom.eventsourcing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import autoparams.AutoSource;
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
        InMemoryEventReader eventReader,
        String streamId,
        UserCreated userCreated,
        PasswordHashChanged passwordHashChanged
    ) {
        // Arrange
        eventReader.addEvent(streamId, userCreated);
        eventReader.addEvent(streamId, passwordHashChanged);
        UserRehydrator sut = new UserRehydrator(eventReader);

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
        InMemoryEventReader eventReader,
        String streamId,
        UserCreated userCreated,
        UnknownEvent unknownEvent,
        PasswordHashChanged passwordHashChanged
    ) {
        // Arrange
        eventReader.addEvent(streamId, userCreated);
        eventReader.addEvent(streamId, unknownEvent);
        eventReader.addEvent(streamId, passwordHashChanged);
        UserRehydrator sut = new UserRehydrator(eventReader);

        // Act/Assert
        assertThatThrownBy(() -> sut.rehydrateState(streamId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("No event handler registered for event")
            .hasMessageContaining(unknownEvent.getClass().getName());
    }
}
