package tech.kronicle.pluginapi;

import org.pf4j.ExtensionPoint;

public interface ExtensionPointWithId extends ExtensionPoint {

    String id();
}
