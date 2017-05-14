package io.loom.core.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.loom.core.event.DomainEvent;
import io.loom.core.fixtures.TitleChanged;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class GsonMessageSerializerTest {
    private final MessageSerializer messageSerializer = new GsonMessageSerializer();

    @Test
    public void gson_serialize_include_type() {
        // Arrange
        UUID aggregateId = UUID.randomUUID();
        long version = 1;
        ZonedDateTime occurrenceTime = ZonedDateTime.now();
        String title = "issue-title";
        TitleChanged event = new TitleChanged(aggregateId, version, occurrenceTime, title);

        // Act
        String sut = messageSerializer.serialize(event);

        // Assert
        Assert.assertTrue(sut.contains("\"@type\": \"io.loom.core.fixtures.TitleChanged"));
        Assert.assertTrue(sut.contains("\"aggregateId\": \"" + aggregateId.toString() + "\""));
        Assert.assertTrue(sut.contains("\"version\": " + version));
        Assert.assertTrue(sut.contains(
                "\"occurrenceTime\": " + occurrenceTime.toInstant().toEpochMilli()));
        Assert.assertTrue(sut.contains("\"title\": " + "\"" + title + "\""));

    }

    @Test
    public void gson_deserialize_with_type() {
        // Arrange
        UUID aggregateId = UUID.randomUUID();
        long version = 1;
        long occurrenceTime = ZonedDateTime.now().toInstant().toEpochMilli();
        String title = "issue-title";
        String json = "{\n  "
                + new StringJoiner(",\n  ")
                .add("\"@type\" : \"io.loom.core.fixtures.TitleChanged\"")
                .add("\"aggregateId\" : \"" + aggregateId.toString() + "\"")
                .add("\"version\" : " + version)
                .add("\"occurrenceTime\" : " + occurrenceTime)
                .add("\"title\" : " + "\"" + title + "\"")
                .toString()
                + "\n}";

        // Act
        Object sut = messageSerializer.deserialize(json);

        // Assert
        Assert.assertTrue(sut instanceof DomainEvent);
        Assert.assertTrue(sut instanceof TitleChanged);

        TitleChanged deserialized = (TitleChanged) sut;
        Assert.assertEquals(aggregateId, deserialized.getAggregateId());
        Assert.assertEquals(version, deserialized.getVersion());
        Assert.assertEquals(
                occurrenceTime, deserialized.getOccurrenceTime().toInstant().toEpochMilli());
        Assert.assertEquals(title, deserialized.getTitle());
    }

    private static class GsonMessageSerializer implements MessageSerializer {
        private static final JsonParser PARSER = new JsonParser();
        private static final String TYPE_FIELD_NAME = "@type";
        private static final Set<Class<?>> REGISTERED_TYPES
                = Collections.synchronizedSet(new HashSet<>());
        private static final GsonBuilder GSON_BUILDER = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeConverter());
        private static Gson gson = GSON_BUILDER.create();

        @Override
        public String serialize(Object message) {
            Class<?> type = message.getClass();
            if (!REGISTERED_TYPES.contains(type)) {
                register(type);
            }
            return gson.toJson(message);
        }

        @Override
        public Object deserialize(String json) {
            JsonObject jsonObj = PARSER.parse(json).getAsJsonObject();
            JsonElement typeElement = jsonObj.get(TYPE_FIELD_NAME);
            if (typeElement == null) {
                throw new JsonSyntaxException(
                        "json for deserialize must hove " + TYPE_FIELD_NAME + " : " + json);
            }
            Class<?> type;
            try {
                type = Class.forName(typeElement.getAsString());
            } catch (ClassNotFoundException ex) {
                throw new JsonIOException(ex);
            }
            return gson.fromJson(jsonObj, type);
        }

        private static void register(Class<?> type) {
            REGISTERED_TYPES.add(type);
            TypeAdapterFactory factory =
                    RuntimeTypeAdapterFactory.of(type, TYPE_FIELD_NAME)
                            .registerSubtype(type, type.getName());
            GSON_BUILDER.registerTypeAdapterFactory(factory);
            gson = GSON_BUILDER.create();
        }
    }

    private static class ZonedDateTimeConverter implements
            JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
        @Override
        public JsonElement serialize(
                ZonedDateTime src,
                Type typeOfSrc,
                JsonSerializationContext context) {
            return new JsonPrimitive(src.toInstant().toEpochMilli());
        }

        @Override
        public ZonedDateTime deserialize(
                JsonElement json,
                Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {
            long timestamp = json.getAsLong();
            return Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("UTC"));
        }
    }

    // type 을 Runtime 에 등록할 수 있는 TypeAdapterFactory 입니다
    // gson 에서 빌드 패키지에 포함시키지 않고 별도로 가져가서 사용하라고 제공하고 있습니다
    // https://github.com/google/gson/blob/master/extras/src/main/java/com/google/gson/typeadapters/RuntimeTypeAdapterFactory.java
    private static class RuntimeTypeAdapterFactory implements TypeAdapterFactory {
        private final Class<?> baseType;
        private final String typeFieldName;
        private final Map<String, Class<?>> labelToSubtype = new LinkedHashMap<String, Class<?>>();
        private final Map<Class<?>, String> subtypeToLabel = new LinkedHashMap<Class<?>, String>();

        private RuntimeTypeAdapterFactory(Class<?> baseType, String typeFieldName) {
            if (typeFieldName == null || baseType == null) {
                throw new NullPointerException();
            }
            this.baseType = baseType;
            this.typeFieldName = typeFieldName;
        }

        /**
         * Creates a new runtime type adapter using for {@code baseType} using {@code
         * typeFieldName} as the type field name. Type field names are case sensitive.
         */
        public static RuntimeTypeAdapterFactory of(Class<?> baseType, String typeFieldName) {
            return new RuntimeTypeAdapterFactory(baseType, typeFieldName);
        }

        /**
         * Creates a new runtime type adapter for {@code baseType} using {@code "type"} as
         * the type field name.
         */
        public static RuntimeTypeAdapterFactory of(Class<?> baseType) {
            return new RuntimeTypeAdapterFactory(baseType, "type");
        }

        /**
         * Registers {@code type} identified by {@code label}. Labels are case
         * sensitive.
         *
         * @throws IllegalArgumentException if either {@code type} or {@code label}
         *                                  have already been registered on this type adapter.
         */
        public RuntimeTypeAdapterFactory registerSubtype(Class<?> type, String label) {
            if (type == null || label == null) {
                throw new NullPointerException();
            }
            if (subtypeToLabel.containsKey(type) || labelToSubtype.containsKey(label)) {
                throw new IllegalArgumentException("types and labels must be unique");
            }
            labelToSubtype.put(label, type);
            subtypeToLabel.put(type, label);
            return this;
        }

        /**
         * Registers {@code type} identified by its {@link Class#getSimpleName simple
         * name}. Labels are case sensitive.
         *
         * @throws IllegalArgumentException if either {@code type} or its simple name
         *                                  have already been registered on this type adapter.
         */
        public RuntimeTypeAdapterFactory registerSubtype(Class<?> type) {
            return registerSubtype(type, type.getSimpleName());
        }

        public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
            if (type.getRawType() != baseType) {
                return null;
            }

            final Map<String, TypeAdapter<?>> labelToDelegate
                    = new LinkedHashMap<String, TypeAdapter<?>>();
            final Map<Class<?>, TypeAdapter<?>> subtypeToDelegate
                    = new LinkedHashMap<Class<?>, TypeAdapter<?>>();
            for (Map.Entry<String, Class<?>> entry : labelToSubtype.entrySet()) {
                TypeAdapter<?> delegate = gson.getDelegateAdapter(
                        this, TypeToken.get(entry.getValue()));
                labelToDelegate.put(entry.getKey(), delegate);
                subtypeToDelegate.put(entry.getValue(), delegate);
            }

            return new TypeAdapter<R>() {
                @Override
                public R read(JsonReader in) throws IOException {
                    JsonElement jsonElement = Streams.parse(in);
                    JsonElement labelJsonElement = jsonElement
                            .getAsJsonObject()
                            .remove(typeFieldName);
                    if (labelJsonElement == null) {
                        throw new JsonParseException("cannot deserialize " + baseType
                                + " because it does not define a field named " + typeFieldName);
                    }
                    String label = labelJsonElement.getAsString();
                    @SuppressWarnings("unchecked") // registration requires that subtype extends T
                            TypeAdapter<R> delegate = (TypeAdapter<R>) labelToDelegate.get(label);
                    if (delegate == null) {
                        throw new JsonParseException("cannot deserialize " + baseType
                                + " subtype named "
                                + label + "; did you forget to register a subtype?");
                    }
                    return delegate.fromJsonTree(jsonElement);
                }

                @Override
                public void write(JsonWriter out, R value) throws IOException {
                    Class<?> srcType = value.getClass();
                    String label = subtypeToLabel.get(srcType);
                    @SuppressWarnings("unchecked") // registration requires that subtype extends T
                            TypeAdapter<R> delegate =
                            (TypeAdapter<R>) subtypeToDelegate.get(srcType);
                    if (delegate == null) {
                        throw new JsonParseException("cannot serialize " + srcType.getName()
                                + "; did you forget to register a subtype?");
                    }
                    JsonObject jsonObject = delegate.toJsonTree(value).getAsJsonObject();
                    if (jsonObject.has(typeFieldName)) {
                        throw new JsonParseException("cannot serialize " + srcType.getName()
                                + " because it already defines a field named " + typeFieldName);
                    }
                    JsonObject clone = new JsonObject();
                    clone.add(typeFieldName, new JsonPrimitive(label));
                    for (Map.Entry<String, JsonElement> e : jsonObject.entrySet()) {
                        clone.add(e.getKey(), e.getValue());
                    }
                    Streams.write(clone, out);
                }
            }.nullSafe();
        }
    }
}
