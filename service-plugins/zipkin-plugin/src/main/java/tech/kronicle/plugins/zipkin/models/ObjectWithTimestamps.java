package tech.kronicle.plugins.zipkin.models;

import java.util.List;

public interface ObjectWithTimestamps {

    List<Long> getTimestamps();
}
