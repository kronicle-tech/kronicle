package tech.kronicle.sdk.models;

public interface DependencyWithIdentity {

    Integer getSourceIndex();
    Integer getTargetIndex();
    String getTypeId();
    String getLabel();
    String getDescription();
}
