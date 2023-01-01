package test.loom.type;

import static org.assertj.core.api.Assertions.assertThat;

import loom.type.ClassNameTypeStrategy;
import loom.type.DelegatingTypeStrategy;
import loom.type.TypeStrategy;
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
