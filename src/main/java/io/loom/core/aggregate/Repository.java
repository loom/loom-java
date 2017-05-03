package io.loom.core.aggregate;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by mhyeon.lee on 2017. 5. 3..
 */
public interface Repository<AGGREGATE extends AggregateRoot> {
    void save(AGGREGATE root);

    Optional<AGGREGATE> load(UUID id);
}
