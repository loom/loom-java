package test.loom.eventsourcing;

import static org.assertj.core.api.Assertions.assertThat;

import autoparams.AutoSource;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.params.ParameterizedTest;
import test.loom.PasswordHashChanged;
import test.loom.User;
import test.loom.UserCreated;

class EventReader_specs {

    @ParameterizedTest
    @AutoSource
    void default_queryEvents_returns_all_events_from_the_first_one(
        InMemoryEventStore sut,
        String streamId,
        String username,
        String hash1,
        String hash2
    ) {
        Iterable<Object> events = Arrays.asList(
            Arrays.array(
                new UserCreated(username),
                new PasswordHashChanged(hash1),
                new PasswordHashChanged(hash2)));

        sut.collectEvents(User.class, streamId, events);

        Iterable<Object> actual = sut.queryEvents(User.class, streamId);
        assertThat(actual)
            .usingRecursiveFieldByFieldElementComparator()
            .isEqualTo(events);
    }
}
