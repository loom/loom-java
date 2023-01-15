package test.loom.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import autoparams.AutoSource;
import autoparams.customization.Customization;
import autoparams.mockito.MockitoCustomizer;
import loom.messaging.Message;
import test.loom.Tuple;

import org.junit.jupiter.params.ParameterizedTest;

class MessageBus_specs {

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer.class)
    void send_correctly_sends_single_message(
        MessageBusSpy sut,
        String partitionKey,
        Message message
    ) {
        sut.send(partitionKey, message);

        assertThat(sut.getCalls()).hasSize(1);
        Tuple<String, List<Message>> call = sut.getCalls().get(0);
        assertThat(call.getItem1()).isEqualTo(partitionKey);
        assertThat(call.getItem2()).hasSize(1);
        assertThat(call.getItem2().get(0)).isSameAs(message);
    }
}
