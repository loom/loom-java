package io.loom.eventsourcing;

import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import java.util.Collections;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ConventionalEventHandler_specs {
    class State {
        private final String value;

        State(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

    public class Appended {
        private final String value;

        Appended(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

    private class UnknownEvent {
    }

    public class StateEventHandler extends ConventionalEventHandler<State> {
        StateEventHandler() {
            super(State.class);
        }

        private State handleEvent(State state, Appended event) {
            return new State(state.getValue() + event.getValue());
        }
    }

    @Test
    public void given_unknown_event_then_handleEvents_throws_exception() {
        // Arrange
        final StateEventHandler sut = new StateEventHandler();
        final State state = new State(randomUUID().toString());
        final Iterable<Object> events = Collections.singletonList(new UnknownEvent());

        // Act
        ThrowableAssert.ThrowingCallable action = () -> sut.handleEvents(state, events);

        // Assert
        assertThatThrownBy(action);
    }

    @Test
    public void given_known_event_then_handleEvents_invokes_handler_correctly() {
        // Arrange
        final StateEventHandler sut = new StateEventHandler();
        final State state = new State(randomUUID().toString());
        final Appended event = new Appended(randomUUID().toString());
        final Iterable<Object> events = Collections.singletonList(event);

        // Act
        final State actual = sut.handleEvents(state, events);

        // Assert
        assertThat(actual.getValue()).isEqualTo(state.getValue() + event.getValue());
    }
}
