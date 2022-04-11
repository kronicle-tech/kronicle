package tech.kronicle.sdk.models;

public interface ObjectWithIdAndMerge<T> extends ObjectWithId {

    T merge(T value);
}
