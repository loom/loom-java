package test.loom.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import autoparams.AutoSource;
import java.lang.reflect.Type;
import java.util.Optional;
import loom.json.TypeResolver;
import org.junit.jupiter.params.ParameterizedTest;

class TypeResolver_specs {

    @ParameterizedTest
    @AutoSource
    public void resolveType_throws_if_result_is_empty(String formattedType) {
        TypeResolver sut = x -> Optional.empty();

        assertThatThrownBy(() -> sut.resolveType(formattedType))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Unable to resolve type for: " + formattedType);
    }

    @ParameterizedTest
    @AutoSource
    public void resolveType_unwrap_value_if_result_is_some(
        Class<?> type,
        String formattedType
    ) {
        TypeResolver sut = x -> Optional.of(type);
        Type actual = sut.resolveType(formattedType);
        assertThat(actual).isEqualTo(type);
    }
}
