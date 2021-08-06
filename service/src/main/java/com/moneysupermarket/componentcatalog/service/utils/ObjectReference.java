package com.moneysupermarket.componentcatalog.service.utils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.function.UnaryOperator;

import static java.util.Objects.nonNull;

@NoArgsConstructor
@AllArgsConstructor
public final class ObjectReference<T> {

    private T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    public void set(UnaryOperator<T> mapper) {
        this.value = mapper.apply(this.value);
    }

    public void clear() {
        this.value = null;
    }

    public boolean isPresent() {
        return nonNull(value);
    }

    public boolean isEmpty() {
        return !isPresent();
    }
}
