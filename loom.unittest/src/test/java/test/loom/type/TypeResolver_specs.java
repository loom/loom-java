package test.loom.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import autoparams.AutoSource;
import java.lang.reflect.Type;
import java.util.Optional;
import loom.type.TypeResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import test.loom.User;

class TypeResolver_specs {

    @ParameterizedTest
    @AutoSource
    void resolveType_throws_if_result_is_empty(String formattedType) {
        TypeResolver sut = x -> Optional.empty();

        assertThatThrownBy(() -> sut.resolveType(formattedType))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Unable to resolve type for: " + formattedType);
    }

    @ParameterizedTest
    @AutoSource
    void resolveType_unwrap_value_if_result_is_some(
        Class<?> type,
        String formattedType
    ) {
        TypeResolver sut = x -> Optional.of(type);
        Type actual = sut.resolveType(formattedType);
        assertThat(actual).isEqualTo(type);
    }

    @Test
    void forClassName_returns_correct_class() {
        TypeResolver resolver = TypeResolver.forClassName();
        Type actual = resolver.resolveType("test.loom.User");
        assertThat(actual).isSameAs(User.class);
    }

    @ParameterizedTest
    @AutoSource
    void forClassName_returns_empty_for_unknown_type(String formattedType) {
        TypeResolver resolver = TypeResolver.forClassName();
        Optional<Type> actual = resolver.tryResolveType(formattedType);
        assertThat(actual).isEmpty();
    }
}
