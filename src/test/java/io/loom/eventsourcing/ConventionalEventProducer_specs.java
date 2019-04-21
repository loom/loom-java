package io.loom.eventsourcing;

import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ConventionalEventProducer_specs {
    private static final Random random = new Random();

    static class State {
        private final int value;

        State(int value) {
            this.value = value;
        }

        int getValue() {
            return value;
        }
    }

    private static class UnknownCommand {
    }

    private static class DecreaseValueTwice {
    }

    public static class ValueChanged {
        private final int value;

        ValueChanged(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static class StateEventProducer extends ConventionalEventProducer<State> {
        StateEventProducer() {
            super(State.class);
        }

        private Iterable<Object> produceEvents(State state, DecreaseValueTwice command) {
            int seed = state.getValue();
            return Arrays.asList(
                    new ValueChanged(seed - 1),
                    new ValueChanged(seed - 2));
        }
    }

    @Test
    public void given_unknown_command_then_produceEvents_throws_exception() {
        // Arrange
        final StateEventProducer sut = new StateEventProducer();
        final State state = new State(random.nextInt());
        final UnknownCommand command = new UnknownCommand();

        // Act
        ThrowableAssert.ThrowingCallable action = () -> sut.produceEvents(state, command);

        // Assert
        assertThatThrownBy(action);
    }

    @Test
    public void given_known_command_then_produceEvents_returns_events_correctly() {
        // Arrange
        final StateEventProducer sut = new StateEventProducer();
        final State state = new State(random.nextInt());
        final Object command = new DecreaseValueTwice();

        // Act
        final Iterable<Object> actual = sut.produceEvents(state, command);

        // Assert
        assertThat(actual).usingFieldByFieldElementComparator().containsExactly(
                new ValueChanged(state.getValue() - 1),
                new ValueChanged(state.getValue() - 2));
    }
}
