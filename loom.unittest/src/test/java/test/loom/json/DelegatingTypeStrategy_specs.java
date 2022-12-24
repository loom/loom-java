package test.loom.json;

import static org.assertj.core.api.Assertions.assertThat;

import autoparams.AutoSource;
import java.lang.reflect.Type;
import java.util.Optional;
import loom.json.DelegatingTypeStrategy;
import loom.json.TypeStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

class DelegatingTypeStrategy_specs {

    @Test
    void sut_implements_TypeStrategy() {
        Class<?> sut = DelegatingTypeStrategy.class;
        Class<?>[] interfaces = sut.getInterfaces();
        assertThat(interfaces).contains(TypeStrategy.class);
    }

    @ParameterizedTest
    @AutoSource
    void formatType_correctly_relays(
        Object value,
        String correctAnswer,
        String wrongAnswer
    ) {
        DelegatingTypeStrategy sut = new DelegatingTypeStrategy(
            x -> x == value ? correctAnswer : wrongAnswer,
            x -> Optional.empty());

        String actual = sut.formatType(value);

        assertThat(actual).isEqualTo(correctAnswer);
    }

    @ParameterizedTest
    @AutoSource
    void tryResolveType_correctly_relays(String formattedType) {
        Optional<Type> answer = Optional.of(UserCreated.class);
        DelegatingTypeStrategy sut = new DelegatingTypeStrategy(
            x -> x.getClass().getName(),
            x -> x.equals(formattedType) ? answer : Optional.empty());

        Optional<Type> actual = sut.tryResolveType(formattedType);

        assertThat(actual).isSameAs(answer);
    }
}
