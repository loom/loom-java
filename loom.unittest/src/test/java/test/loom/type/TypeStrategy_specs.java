package test.loom.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import autoparams.AutoSource;
import autoparams.customization.Customization;
import autoparams.mockito.MockitoCustomizer;
import java.lang.reflect.Type;
import java.util.Optional;
import loom.type.TypeFormatter;
import loom.type.TypeResolver;
import loom.type.TypeStrategy;
import org.junit.jupiter.params.ParameterizedTest;

class TypeStrategy_specs {

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void create_creates_instance(
        TypeFormatter formatter,
        TypeResolver resolver
    ) {
        TypeStrategy actual = TypeStrategy.create(formatter, resolver);
        assertThat(actual).isNotNull();
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void create_creates_instance_that_relays_to_formatter(
        TypeFormatter formatter,
        TypeResolver resolver,
        Type query,
        Optional<String> answer
    ) {
        TypeStrategy sut = TypeStrategy.create(formatter, resolver);
        when(formatter.tryFormatType(query)).thenReturn(answer);

        Optional<String> actual = sut.tryFormatType(query);

        assertThat(actual).isSameAs(answer);
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void create_creates_instance_that_relays_to_resolver(
        TypeFormatter formatter,
        TypeResolver resolver,
        String query,
        Optional<Type> answer
    ) {
        TypeStrategy sut = TypeStrategy.create(formatter, resolver);
        when(resolver.tryResolveType(query)).thenReturn(answer);

        Optional<Type> actual = sut.tryResolveType(query);

        assertThat(actual).isSameAs(answer);
    }
}
