package loom;

import static org.assertj.core.api.Assertions.assertThat;

import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ContractObject_specs {

    @ParameterizedTest
    @ValueSource(classes = {
        StreamEvent.class,
        StreamCommand.class
    })
    void constructor_is_decorated_with_ConstructorProperties_annotation(
        Class<?> type
    ) {
        Constructor<?> constructor = type.getConstructors()[0];
        Annotation[] actual = constructor.getAnnotations();
        assertThat(actual).anyMatch(x -> x instanceof ConstructorProperties);
    }
}
