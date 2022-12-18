package loom;

import static org.assertj.core.api.Assertions.assertThat;

import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

class StreamEvent_specs {

    @Test
    void constructor_is_decorated_with_ConstructorProperties_annotation() {
        Constructor<?> constructor = StreamEvent.class.getConstructors()[0];
        Annotation[] actual = constructor.getAnnotations();
        assertThat(actual).anyMatch(x -> x instanceof ConstructorProperties);
    }
}
