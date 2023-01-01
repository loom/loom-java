package test.loom.type;

import static org.assertj.core.api.Assertions.assertThat;

import autoparams.AutoSource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import loom.eventsourcing.StreamCommand;
import loom.eventsourcing.StreamEvent;
import loom.type.ClassNameTypeStrategy;
import loom.type.EventSourcingTypeStrategy;
import org.junit.jupiter.params.ParameterizedTest;
import test.loom.CreateUser;
import test.loom.UserCreated;

class EventSourcingTypeStrategy_specs {

    @ParameterizedTest
    @AutoSource
    void tryFormatType_correctly_formats_type_of_stream_command(
        ClassNameTypeStrategy payloadStrategy,
        StreamCommand<CreateUser> command
    ) {
        EventSourcingTypeStrategy sut =
            new EventSourcingTypeStrategy(payloadStrategy);

        Optional<String> actual = sut.tryFormatType(command);

        assertThat(actual).contains("stream-command:test.loom.CreateUser");
    }

    @ParameterizedTest
    @AutoSource
    void tryFormatType_correctly_formats_type_of_stream_event(
        ClassNameTypeStrategy payloadStrategy,
        StreamEvent<UserCreated> event
    ) {
        EventSourcingTypeStrategy sut =
            new EventSourcingTypeStrategy(payloadStrategy);

        Optional<String> actual = sut.tryFormatType(event);

        assertThat(actual).contains("stream-event:test.loom.UserCreated");
    }

    @ParameterizedTest
    @AutoSource
    void tryResolveType_returns_empty_optional_for_invalid_formatted_type(
        ClassNameTypeStrategy payloadStrategy,
        String formattedType
    ) {
        EventSourcingTypeStrategy sut =
            new EventSourcingTypeStrategy(payloadStrategy);

        Optional<Type> actual = sut.tryResolveType(formattedType);

        assertThat(actual).isEmpty();
    }

    @ParameterizedTest
    @AutoSource
    void tryResolveType_returns_correct_type_for_formatted_stream_command_type(
        ClassNameTypeStrategy payloadStrategy
    ) {
        // Arrange
        EventSourcingTypeStrategy sut =
            new EventSourcingTypeStrategy(payloadStrategy);
        String formattedType = "stream-command:test.loom.CreateUser";

        // Act
        Optional<Type> actual = sut.tryResolveType(formattedType);

        // Assert
        assertThat(actual).hasValueSatisfying(value -> {
            assertThat(value).isInstanceOf(ParameterizedType.class);

            ParameterizedType parameterizedType = (ParameterizedType) value;

            assertThat(parameterizedType.getRawType())
                    .isEqualTo(StreamCommand.class);

            assertThat(parameterizedType.getActualTypeArguments())
                    .containsExactly(CreateUser.class);
        });
    }

    @ParameterizedTest
    @AutoSource
    void tryResolveType_returns_correct_type_for_formatted_stream_event_type(
        ClassNameTypeStrategy payloadStrategy
    ) {
        // Arrange
        EventSourcingTypeStrategy sut =
            new EventSourcingTypeStrategy(payloadStrategy);
        String formattedType = "stream-event:test.loom.UserCreated";

        // Act
        Optional<Type> actual = sut.tryResolveType(formattedType);

        // Assert
        assertThat(actual).hasValueSatisfying(value -> {
            assertThat(value).isInstanceOf(ParameterizedType.class);

            ParameterizedType parameterizedType = (ParameterizedType) value;

            assertThat(parameterizedType.getRawType())
                    .isEqualTo(StreamEvent.class);

            assertThat(parameterizedType.getActualTypeArguments())
                    .containsExactly(UserCreated.class);
        });
    }
}
