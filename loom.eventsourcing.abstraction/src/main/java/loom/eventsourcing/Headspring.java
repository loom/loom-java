package loom.eventsourcing;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import loom.messaging.Message;
import loom.messaging.MessageHandler;

public abstract class Headspring<S>
    extends Rehydrator<S>
    implements MessageHandler {

    private final EventStore eventStore;
    private final Map<Class<?>, CommandExecutor<S, Object>> commandExecutors;

    protected Headspring(
        EventStore eventStore,
        Function<String, S> seedFactory,
        Iterable<CommandExecutor<S, ?>> commandExecutors,
        Iterable<EventHandler<S, ?>> eventHandlers
    ) {
        super(eventStore, seedFactory, eventHandlers);

        this.eventStore = eventStore;
        this.commandExecutors = toDictionary(commandExecutors);
    }

    private static <E> Stream<E> stream(Iterable<E> source) {
        return StreamSupport.stream(source.spliterator(), false);
    }

    @SuppressWarnings("unchecked")
    private static <S> Map<Class<?>, CommandExecutor<S, Object>> toDictionary(
        Iterable<CommandExecutor<S, ?>> commandExecutor
    ) {
        return stream(commandExecutor)
            .map(e -> (CommandExecutor<S, Object>) e)
            .collect(toMap(CommandExecutor::getCommandType, h -> h));
    }

    @Override
    public boolean canHandle(Message message) {
        if (message.getData() instanceof StreamCommand == false) {
            return false;
        }

        StreamCommand<?> command = (StreamCommand<?>) (message.getData());
        Object payload = command.getPayload();
        return commandExecutors.containsKey(payload.getClass());
    }

    @Override
    public void handle(Message message) {
        StreamCommand<?> command = (StreamCommand<?>) (message.getData());
        Snapshot<S> snapshot = rehydrateState(command.getStreamId());
        String predecessorId = message.getId();
        eventStore.collectEvents(
            getStateType(),
            message.getProcessId(),
            message.getInitiator(),
            predecessorId,
            command.getStreamId(),
            snapshot.getVersion() + 1,
            produceEvents(snapshot.getState(), command));
    }

    private Iterable<Object> produceEvents(S state, StreamCommand<?> command) {
        Object payload = command.getPayload();
        CommandExecutor<S, Object> executor = getExecutor(payload);
        List<Object> events = new ArrayList<>();
        executor.produceEvents(state, payload).forEach(events::add);
        events.forEach(this::verifyEvent);
        return events;
    }

    private void verifyEvent(Object event) {
        for (Class<?> eventType : getEventTypes()) {
            if (eventType.isInstance(event)) {
                return;
            }
        }
        throw new RuntimeException(
            "Event that cannot be handled was produced."
            + " Event payload type: '"
            + event.getClass().getName()
            + "'.");
    }

    private CommandExecutor<S, Object> getExecutor(Object payload) {
        Class<?> payloadType = payload.getClass();
        if (commandExecutors.containsKey(payloadType) == false) {
            throw new RuntimeException(
                "Unsupported command payload type '"
                + payloadType.getName()
                + "'.");
        }
        return commandExecutors.get(payloadType);
    }
}
