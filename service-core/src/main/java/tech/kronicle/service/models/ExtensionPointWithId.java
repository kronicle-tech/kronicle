package tech.kronicle.service.models;

import org.pf4j.ExtensionPoint;

public interface ExtensionPointWithId extends ExtensionPoint {

    String id();
}
