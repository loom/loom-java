package test.loom.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import autoparams.AutoSource;
import java.util.ArrayList;
import java.util.List;
import loom.messaging.CompositeMessageHandler;
import loom.messaging.Message;
import loom.messaging.MessageHandler;
import org.junit.jupiter.params.ParameterizedTest;

public class CompositeMessageHandler_specs {

    @ParameterizedTest
    @AutoSource
    public void can_handle_with_empty_composite_handler(Message message) {
        CompositeMessageHandler sut = new CompositeMessageHandler();
        assertThat(sut.canHandle(message)).isFalse();
    }

    @ParameterizedTest
    @AutoSource
    public void can_handle_with_single_handler_that_can_handle_message(
        Message message
    ) {
        CompositeMessageHandler sut = new CompositeMessageHandler(
            new CanHandleStub(true));
        assertThat(sut.canHandle(message)).isTrue();
    }

    @ParameterizedTest
    @AutoSource
    public void can_handle_with_single_handler_that_cannot_handle_message(
        Message message
    ) {
        CompositeMessageHandler sut = new CompositeMessageHandler(
            new CanHandleStub(false));
        assertThat(sut.canHandle(message)).isFalse();
    }

    @ParameterizedTest
    @AutoSource
    public void can_handle_with_multiple_handlers_that_can_handle_message(
        Message message
    ) {
        CompositeMessageHandler sut = new CompositeMessageHandler(
            new CanHandleStub(true),
            new CanHandleStub(true));
        assertThat(sut.canHandle(message)).isTrue();
    }

    @ParameterizedTest
    @AutoSource
    public void can_handle_with_multiple_handlers_that_cannot_handle_message(
        Message message
    ) {
        CompositeMessageHandler sut = new CompositeMessageHandler(
            new CanHandleStub(false),
            new CanHandleStub(false));
        assertThat(sut.canHandle(message)).isFalse();
    }

    private static class CanHandleStub implements MessageHandler {
        private final boolean canHandle;

        public CanHandleStub(boolean canHandle) {
            this.canHandle = canHandle;
        }

        @Override
        public boolean canHandle(Message message) {
            return canHandle;
        }

        @Override
        public void handle(Message message) {
            // not used in these tests
        }
    }

    @ParameterizedTest
    @AutoSource
    public void handle_should_call_handle_on_all_handlers(
        Message message
    ) {
        MessageHandlerDouble handler1 = new MessageHandlerDouble(true);
        MessageHandlerDouble handler2 = new MessageHandlerDouble(true);
        CompositeMessageHandler sut = new CompositeMessageHandler(
            handler1, handler2);

        sut.handle(message);

        assertThat(handler1.getHandledMessages()).containsExactly(message);
        assertThat(handler2.getHandledMessages()).containsExactly(message);
    }

    @ParameterizedTest
    @AutoSource
    public void handle_should_not_call_handle_on_handler_not_able_to_handle_message(
        Message message
    ) {
        // Given
        MessageHandlerDouble handler1 = new MessageHandlerDouble(false);
        MessageHandlerDouble handler2 = new MessageHandlerDouble(true);
        CompositeMessageHandler compositeHandler = new CompositeMessageHandler(
            handler1, handler2);

        // When
        compositeHandler.handle(message);

        // Then
        assertThat(handler1.getHandledMessages()).isEmpty();
        assertThat(handler2.getHandledMessages()).containsExactly(message);
    }

    @ParameterizedTest
    @AutoSource
    public void handle_should_continue_calling_handle_on_all_handlers_even_if_some_throw_exception(
        Message message
    ) {
        // Arrange
        MessageHandlerDouble handler1 = new MessageHandlerDouble(true) {
            @Override
            public void handle(Message message) {
                super.handle(message);
                throw new RuntimeException("Test exception");
            }
        };
        MessageHandlerDouble handler2 = new MessageHandlerDouble(true);
        CompositeMessageHandler sut = new CompositeMessageHandler(
            handler1,
            handler2);

        // Act
        try {
            sut.handle(message);
        } catch (Exception e) {
            // ignore
        }

        // Assert
        assertThat(handler1.getHandledMessages()).containsExactly(message);
        assertThat(handler2.getHandledMessages()).containsExactly(message);
    }

    @ParameterizedTest
    @AutoSource
    public void handle_should_throw_exception_that_aggregates_exceptions_thrown_by_sub_handlers(
        Message message
    ) {
        // Arrange
        RuntimeException exception1 = new RuntimeException("Test exception 1");
        MessageHandlerDouble handler1 = new MessageHandlerDouble(true) {
            @Override
            public void handle(Message message) {
                super.handle(message);
                throw exception1;
            }
        };
        RuntimeException exception2 = new RuntimeException("Test exception 2");
        MessageHandlerDouble handler2 = new MessageHandlerDouble(true) {
            @Override
            public void handle(Message message) {
                super.handle(message);
                throw exception2;
            }
        };
        CompositeMessageHandler sut = new CompositeMessageHandler(handler1, handler2);

        // Act/Assert
        assertThatThrownBy(() -> sut.handle(message))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Test exception 1")
            .hasMessageContaining("Test exception 2")
            .hasSuppressedException(exception1)
            .hasSuppressedException(exception2);
    }

    public static class MessageHandlerDouble implements MessageHandler {

        private final boolean canHandleResult;
        private final List<Message> handledMessages;

        public MessageHandlerDouble(boolean canHandleResult) {
            this.canHandleResult = canHandleResult;
            this.handledMessages = new ArrayList<>();
        }

        public List<Message> getHandledMessages() {
            return handledMessages;
        }

        @Override
        public boolean canHandle(Message message) {
            return canHandleResult;
        }

        @Override
        public void handle(Message message) {
            handledMessages.add(message);
        }
    }
}
