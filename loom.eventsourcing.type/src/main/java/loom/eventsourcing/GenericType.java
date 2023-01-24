package loom.eventsourcing;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

class GenericType implements ParameterizedType {

    private final Type rawType;
    private final Type[] typeArguments;

    public GenericType(Type rawType, Type... typeArguments) {
        this.rawType = rawType;
        this.typeArguments = typeArguments.clone();
    }

    @Override
    public Type[] getActualTypeArguments() {
        return typeArguments;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
