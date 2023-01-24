package test.loom.eventsourcing;

import static org.assertj.core.api.Assertions.assertThat;

import autoparams.AutoSource;
import autoparams.customization.Customization;
import autoparams.mockito.MockitoCustomizer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.lang.reflect.Type;
import java.util.Optional;
import loom.eventsourcing.StreamCommand;
import loom.eventsourcing.StreamCommandTypeStrategy;
import loom.type.TypeFormatter;
import loom.type.TypeResolver;
import loom.type.TypeStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import test.loom.CreateUser;
import test.loom.User;

class StreamCommandTypeStrategy_specs {

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void sut_implements_TypeStrategy(StreamCommandTypeStrategy sut) {
        assertThat(sut).isInstanceOf(TypeStrategy.class);
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void tryFormatType_returns_empty(TypeResolver resolver) {
        StreamCommandTypeStrategy sut = new StreamCommandTypeStrategy(
            () -> TypeStrategy.create(TypeFormatter.forTypeName(), resolver));

        Optional<String> actual = sut.tryFormatType(User.class);

        assertThat(actual).isEmpty();
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void tryFormatTypeOf_correctly_formats_type_of_stream_command(
        TypeResolver resolver,
        StreamCommand<CreateUser> value
    ) {
        StreamCommandTypeStrategy sut = new StreamCommandTypeStrategy(
            () -> TypeStrategy.create(TypeFormatter.forTypeName(), resolver));

        Optional<String> actual = sut.tryFormatTypeOf(value);

        assertThat(actual).contains("streamcommand:test.loom.CreateUser");
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void tryFormatTypeOf_returns_empty_for_non_stream_command_value(
        TypeResolver resolver,
        CreateUser value
    ) {
        StreamCommandTypeStrategy sut = new StreamCommandTypeStrategy(
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
        StreamCommandTypeStrategy sut =
            new StreamCommandTypeStrategy(() -> payloadStrategy);

        Optional<Type> actual = sut.tryResolveType(formattedType);

        assertThat(actual).isEmpty();
    }

    @Test
    void tryResolveType_returns_type_for_formatted_stream_command_type() {
        // Arrange
        StreamCommandTypeStrategy sut = new StreamCommandTypeStrategy(
            () -> TypeStrategy.create(
                TypeFormatter.forTypeName(),
                TypeResolver.forClassName()
            )
        );

        String formattedType = "streamcommand:test.loom.CreateUser";

        // Act
        Optional<Type> actual = sut.tryResolveType(formattedType);

        // Assert
        assertThat(actual).isNotEmpty();
    }

    @ParameterizedTest
    @AutoSource
    void tryResolveType_returns_correct_stream_command_type(
        StreamCommand<CreateUser> command,
        ObjectMapper mapper
    ) throws JsonProcessingException {
        // Arrange
        StreamCommandTypeStrategy sut = new StreamCommandTypeStrategy(
            () -> TypeStrategy.create(
                TypeFormatter.forTypeName(),
                TypeResolver.forClassName()
            )
        );

        // Act
        Type type = sut.resolveType("streamcommand:test.loom.CreateUser");

        // Assert
        String json = mapper.writeValueAsString(command);
        JavaType javaType = TypeFactory.defaultInstance().constructType(type);
        Object value = mapper.readValue(json, javaType);
        assertThat(value).usingRecursiveComparison().isEqualTo(command);
    }
}
