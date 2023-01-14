package test.loom.eventsourcing;

import loom.eventsourcing.CommandExecutor;
import org.assertj.core.util.Arrays;
import test.loom.RaiseUnknownEvent;
import test.loom.UnknownEvent;
import test.loom.User;

public class RaiseUnknownEventCommandExecutor
    implements CommandExecutor<User, RaiseUnknownEvent> {

    @Override
    public Iterable<Object> produceEvents(
        User state,
        RaiseUnknownEvent command
    ) {
        return Arrays.asList(new Object[] { new UnknownEvent() });
    }
}
