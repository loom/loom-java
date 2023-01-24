package test.loom.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import autoparams.AutoSource;
import autoparams.customization.Customization;
import autoparams.mockito.MockitoCustomizer;
import java.lang.reflect.Type;
import java.util.Optional;
import loom.type.TypeFormatter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import test.loom.User;

class TypeFormatter_specs {

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void formatType_correctly_unwrap_some_value(Type type, String some) {
        TypeFormatter sut = x -> Optional.ofNullable(x == type ? some : null);
        String actual = sut.formatType(type);
        assertThat(actual).isEqualTo(some);
    }

    @Test
    void formatType_throws_RuntimeException_for_none() {
        TypeFormatter sut = x -> Optional.empty();
        assertThatThrownBy(() -> sut.formatType(User.class))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Unable to format type: test.loom.User");
    }

    @ParameterizedTest
    @AutoSource
    void tryFormatTypeOf_delegates_with_class_of_value(String some) {
        TypeFormatter sut = x -> {
            return Optional.ofNullable(x == User.class ? some : null);
        };
        Optional<String> actual = sut.tryFormatTypeOf(User.seedFactory());
        assertThat(actual).hasValue(some);
    }

    @ParameterizedTest
    @AutoSource
    void formatTypeOf_correctly_unwrap_some_value(String some) {
        TypeFormatter sut = x -> {
            return Optional.ofNullable(x == User.class ? some : null);
        };
        String actual = sut.formatTypeOf(User.seedFactory());
        assertThat(actual).isEqualTo(some);
    }

    @Test
    void formatTypeOf_throws_RuntimeException_for_none() {
        TypeFormatter sut = x -> Optional.empty();
        assertThatThrownBy(() -> sut.formatTypeOf(User.seedFactory()))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Unable to format type: test.loom.User");
    }

    @Test
    void forTypeName_returns_type_name() {
        TypeFormatter formatter = TypeFormatter.forTypeName();
        String actual = formatter.formatType(User.class);
        assertThat(actual).isEqualTo("test.loom.User");
    }
}
