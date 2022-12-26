package test.loom.json;

import static org.assertj.core.api.Assertions.assertThat;

import autoparams.AutoSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import loom.json.ClassNameTypeStrategy;
import loom.json.JacksonJsonStrategy;
import loom.json.JsonData;
import loom.json.JsonProcessor;
import org.junit.jupiter.params.ParameterizedTest;

class JsonProcessor_specs {

    @ParameterizedTest
    @AutoSource
    public void convertToJson_with_string_value_returns_expected_json_data(
        ObjectMapper mapper,
        ClassNameTypeStrategy typeStrategy,
        JacksonJsonStrategy jsonStrategy,
        String value
    ) {
        // Arrange
        JsonProcessor sut = new JsonProcessor(typeStrategy, jsonStrategy);

        // Act
        JsonData actual = sut.convertToJson(value);

        // Assert
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(new JsonData("java.lang.String", "\"" + value + "\""));
    }

    @ParameterizedTest
    @AutoSource
    public void convertToJson_with_int_value_returns_expected_json_data(
        ObjectMapper mapper,
        ClassNameTypeStrategy typeStrategy,
        JacksonJsonStrategy jsonStrategy,
        int value
    ) {
        // Arrange
        JsonProcessor sut = new JsonProcessor(typeStrategy, jsonStrategy);

        // Act
        JsonData actual = sut.convertToJson(value);

        // Assert
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(new JsonData("java.lang.Integer", Integer.toString(value)));
    }

    @ParameterizedTest
    @AutoSource
    public void convertFromJson_with_string_value_returns_expected_value(
        ObjectMapper mapper,
        ClassNameTypeStrategy typeStrategy,
        JacksonJsonStrategy jsonStrategy,
        String value
    ) {
        // Arrange
        JsonProcessor sut = new JsonProcessor(typeStrategy, jsonStrategy);
        JsonData jsonData = new JsonData("java.lang.String", "\"" + value + "\"");

        // Act
        Object actual = sut.convertFromJson(jsonData);

        // Assert
        assertThat(actual).isEqualTo(value);
    }

    @ParameterizedTest
    @AutoSource
    public void convertFromJson_with_int_value_returns_expected_value(
        ObjectMapper mapper,
        ClassNameTypeStrategy typeStrategy,
        JacksonJsonStrategy jsonStrategy,
        int value
    ) {
        // Arrange
        JsonProcessor sut = new JsonProcessor(typeStrategy, jsonStrategy);
        JsonData jsonData = new JsonData("java.lang.Integer", Integer.toString(value));

        // Act
        Object actual = sut.convertFromJson(jsonData);

        // Assert
        assertThat(actual).isEqualTo(value);
    }
}
