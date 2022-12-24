package test.loom.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import autoparams.AutoSource;
import java.util.Optional;
import loom.json.TypeFormatter;
import org.junit.jupiter.params.ParameterizedTest;

class TypeFormatter_specs {

    @ParameterizedTest
    @AutoSource
    void formatType_correctly_unwrap_some_value(Object value, String some) {
        TypeFormatter sut = x -> Optional.ofNullable(x == value ? some : null);
        String actual = sut.formatType(value);
        assertThat(actual).isEqualTo(some);
    }

    @ParameterizedTest
    @AutoSource
    void formatType_throws_RuntimeException_for_none(
        UserCreated value,
        String some
    ) {
        TypeFormatter sut = x -> Optional.ofNullable(x == value ? null : some);
        assertThatThrownBy(() -> sut.formatType(value))
            .hasMessageStartingWith("Cannot format type")
            .hasMessageContaining(UserCreated.class.getName());
    }
}
