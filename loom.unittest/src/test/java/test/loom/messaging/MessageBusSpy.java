package test.loom.messaging;

import java.util.ArrayList;
import java.util.List;
import loom.messaging.Message;
import loom.messaging.MessageBus;
import test.loom.Tuple;

public class MessageBusSpy implements MessageBus {
    private List<Tuple<String, List<Message>>> calls = new ArrayList<>();

    @Override
    public void send(String partitionKey, Iterable<Message> messages) {
        calls.add(new Tuple<>(partitionKey, toList(messages)));
    }

    private static <T> List<T> toList(Iterable<T> source) {
        List<T> list = new ArrayList<>();
        source.forEach(list::add);
        return list;
    }

    public List<Tuple<String, List<Message>>> getCalls() {
        return calls;
    }
}
