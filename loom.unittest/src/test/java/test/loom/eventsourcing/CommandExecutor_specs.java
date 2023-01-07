package test.loom.eventsourcing;

import static org.assertj.core.api.Assertions.assertThat;

import autoparams.AutoSource;
import autoparams.customization.Customization;
import autoparams.mockito.MockitoCustomizer;
import org.junit.jupiter.params.ParameterizedTest;
import test.loom.CreateUser;

class CommandExecutor_specs {

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void getCommandType_returns_class_of_command(
        CreateUserCommandExecutor sut
    ) {
        Class<?> actual = sut.getCommandType();
        assertThat(actual).isEqualTo(CreateUser.class);
    }
}
