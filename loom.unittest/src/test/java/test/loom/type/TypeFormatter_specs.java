package test.loom.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import autoparams.AutoSource;
import java.util.Optional;
import loom.type.TypeFormatter;
import org.junit.jupiter.params.ParameterizedTest;
import test.loom.UserCreated;

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
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Unable to format type: test.loom.UserCreated");
    }
}
