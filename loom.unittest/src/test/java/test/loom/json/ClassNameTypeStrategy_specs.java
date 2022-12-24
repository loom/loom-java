package test.loom.json;

import static org.assertj.core.api.Assertions.assertThat;

import autoparams.AutoSource;
import java.lang.reflect.Type;
import java.util.Optional;
import loom.json.ClassNameTypeStrategy;
import org.junit.jupiter.params.ParameterizedTest;

class ClassNameTypeStrategy_specs {

    @ParameterizedTest
    @AutoSource
    void tryFormatType_returns_class_name(
        ClassNameTypeStrategy sut,
        String value
    ) {
        Optional<String> actual = sut.tryFormatType(value);
        assertThat(actual).containsSame("java.lang.String");
    }

    @ParameterizedTest
    @AutoSource
    void tryResolveType_returns_correct_class(ClassNameTypeStrategy sut) {
        String formattedType = "loom.json.ClassNameTypeStrategy";
        Optional<Type> actual = sut.tryResolveType(formattedType);
        assertThat(actual).containsSame(ClassNameTypeStrategy.class);
    }

    @ParameterizedTest
    @AutoSource
    void tryResolveType_returns_empty_for_invalid_type(
        ClassNameTypeStrategy sut,
        String formattedType
    ) {
        Optional<Type> actual = sut.tryResolveType(formattedType);
        assertThat(actual).isEmpty();
    }
}
