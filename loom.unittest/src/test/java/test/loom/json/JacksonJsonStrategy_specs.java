package test.loom.json;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.assertj.core.api.Assertions.assertThat;

import autoparams.AutoSource;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.lang.reflect.Type;
import loom.eventsourcing.StreamEvent;
import loom.json.JacksonJsonStrategy;
import loom.json.JsonStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import test.loom.UserCreated;
import test.loom.ValueHolder;

class JacksonJsonStrategy_specs {

    @Test
    void sut_implements_JsonStrategy() {
        Class<?> sut = JacksonJsonStrategy.class;
        Class<?>[] interfaces = sut.getInterfaces();
        assertThat(interfaces).contains(JsonStrategy.class);
    }

    @ParameterizedTest
    @AutoSource
    void serialize_returns_correct_json_string(
        ObjectMapper mapper,
        ValueHolder value
    ) {
        JacksonJsonStrategy sut = new JacksonJsonStrategy(mapper);

        String actual = sut.serialize(value);

        String expected = "{\"value\":\"" + value.getValue() + "\"}";
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @AutoSource
    void deserialize_correctly_works_with_non_generic_type(
        ObjectMapper mapper,
        ValueHolder value
    ) {
        JacksonJsonStrategy sut = new JacksonJsonStrategy(mapper);
        String json = sut.serialize(value);

        Object actual = sut.deserialize(ValueHolder.class, json);

        assertThat(actual).usingRecursiveComparison().isEqualTo(value);
    }

    @ParameterizedTest
    @AutoSource
    void deserialize_correctly_works_with_generic_type(
        ObjectMapper mapper,
        StreamEvent<UserCreated> value
    ) {
        mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        JacksonJsonStrategy sut = new JacksonJsonStrategy(mapper);
        String json = sut.serialize(value);

        Type type = new TypeReference<StreamEvent<UserCreated>>() {}.getType();
        Object actual = sut.deserialize(type, json);

        assertThat(actual).usingRecursiveComparison().isEqualTo(value);
    }
}
