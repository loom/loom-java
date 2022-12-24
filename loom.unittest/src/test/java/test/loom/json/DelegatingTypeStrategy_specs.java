package test.loom.json;

import static org.assertj.core.api.Assertions.assertThat;

import autoparams.AutoSource;
import java.lang.reflect.Type;
import java.util.Optional;
import loom.json.DelegatingTypeStrategy;
import org.junit.jupiter.params.ParameterizedTest;

class DelegatingTypeStrategy_specs {

    @ParameterizedTest
    @AutoSource
    void tryFormatType_correctly_relays(
        Object value,
        String correctAnswer,
        String wrongAnswer
    ) {
        DelegatingTypeStrategy sut = new DelegatingTypeStrategy(
            x -> Optional.of(x == value ? correctAnswer : wrongAnswer),
            x -> Optional.empty());

        Optional<String> actual = sut.tryFormatType(value);

        assertThat(actual).containsSame(correctAnswer);
    }

    @ParameterizedTest
    @AutoSource
    void tryResolveType_correctly_relays(String formattedType) {
        Optional<Type> answer = Optional.of(UserCreated.class);
        DelegatingTypeStrategy sut = new DelegatingTypeStrategy(
            x -> Optional.of(x.getClass().getName()),
            x -> x.equals(formattedType) ? answer : Optional.empty());

        Optional<Type> actual = sut.tryResolveType(formattedType);

        assertThat(actual).isSameAs(answer);
    }
}
