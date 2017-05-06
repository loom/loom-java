package io.loom.core.aggregate;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by mhyeon.lee on 2017. 5. 3..
 */
public interface Repository<AggregateT extends AggregateRoot> {
    void save(AggregateT root);

    Optional<AggregateT> load(UUID id);
}
