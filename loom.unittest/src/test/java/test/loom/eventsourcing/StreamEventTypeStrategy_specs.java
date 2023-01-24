package test.loom.eventsourcing;

import static org.assertj.core.api.Assertions.assertThat;

import autoparams.AutoSource;
import autoparams.customization.Customization;
import autoparams.mockito.MockitoCustomizer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.lang.reflect.Type;
import java.util.Optional;
import loom.eventsourcing.StreamEvent;
import loom.eventsourcing.StreamEventTypeStrategy;
import loom.type.TypeFormatter;
import loom.type.TypeResolver;
import loom.type.TypeStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import test.loom.User;
import test.loom.UserCreated;

class StreamEventTypeStrategy_specs {

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void sut_implements_TypeStrategy(StreamEventTypeStrategy sut) {
        assertThat(sut).isInstanceOf(TypeStrategy.class);
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void tryFormatType_returns_empty(TypeResolver resolver) {
        StreamEventTypeStrategy sut = new StreamEventTypeStrategy(
            () -> TypeStrategy.create(TypeFormatter.forTypeName(), resolver));

        Optional<String> actual = sut.tryFormatType(User.class);

        assertThat(actual).isEmpty();
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void tryFormatTypeOf_correctly_formats_type_of_stream_event(
        TypeResolver resolver,
        StreamEvent<UserCreated> value
    ) {
        StreamEventTypeStrategy sut = new StreamEventTypeStrategy(
            () -> TypeStrategy.create(TypeFormatter.forTypeName(), resolver));

        Optional<String> actual = sut.tryFormatTypeOf(value);

        assertThat(actual).contains("streamevent:test.loom.UserCreated");
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void tryFormatTypeOf_correctly_sets_prefix(
        TypeResolver resolver,
        String prefix,
        StreamEvent<UserCreated> value
    ) {
        StreamEventTypeStrategy sut = new StreamEventTypeStrategy(
            () -> TypeStrategy.create(TypeFormatter.forTypeName(), resolver),
            prefix);

        String actual = sut.formatTypeOf(value);

        assertThat(actual).startsWith(prefix);
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void tryFormatTypeOf_returns_empty_for_non_stream_event_value(
        TypeResolver resolver,
        UserCreated value
    ) {
        StreamEventTypeStrategy sut = new StreamEventTypeStrategy(
            () -> TypeStrategy.create(TypeFormatter.forTypeName(), resolver));

        Optional<String> actual = sut.tryFormatTypeOf(value);

        assertThat(actual).isEmpty();
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void tryResolveType_returns_empty_optional_for_invalid_formatted_type(
        TypeStrategy payloadStrategy,
        String formattedType
    ) {
        StreamEventTypeStrategy sut =
            new StreamEventTypeStrategy(() -> payloadStrategy);

        Optional<Type> actual = sut.tryResolveType(formattedType);

        assertThat(actual).isEmpty();
    }

    @Test
    void tryResolveType_returns_type_for_formatted_stream_event_type() {
        // Arrange
        StreamEventTypeStrategy sut = new StreamEventTypeStrategy(
            () -> TypeStrategy.create(
                TypeFormatter.forTypeName(),
                TypeResolver.forClassName()
            )
        );

        String formattedType = "streamevent:test.loom.UserCreated";

        // Act
        Optional<Type> actual = sut.tryResolveType(formattedType);

        // Assert
        assertThat(actual).isNotEmpty();
    }

    @ParameterizedTest
    @AutoSource
    void tryResolveType_returns_correct_stream_event_type(
        StreamEvent<UserCreated> event,
        ObjectMapper mapper
    ) throws JsonProcessingException {
        // Arrange
        StreamEventTypeStrategy sut = new StreamEventTypeStrategy(
            () -> TypeStrategy.create(
                TypeFormatter.forTypeName(),
                TypeResolver.forClassName()
            )
        );
        mapper.registerModule(new JavaTimeModule());

        // Act
        Type type = sut.resolveType("streamevent:test.loom.UserCreated");

        // Assert
        String json = mapper.writeValueAsString(event);
        JavaType javaType = TypeFactory.defaultInstance().constructType(type);
        Object value = mapper.readValue(json, javaType);
        assertThat(value).usingRecursiveComparison().isEqualTo(event);
    }
}
