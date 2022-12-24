package test.loom.json;

import static org.assertj.core.api.Assertions.assertThat;

import loom.json.ClassNameTypeStrategy;
import loom.json.DelegatingTypeStrategy;
import loom.json.TypeStrategy;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TypeStrategy_specs {

    @ParameterizedTest
    @ValueSource(classes = {
        ClassNameTypeStrategy.class,
        DelegatingTypeStrategy.class
    })
    void type_implements_TypeStrategy(Class<?> sut) {
        assertThat(sut.getInterfaces()).contains(TypeStrategy.class);
    }
}
