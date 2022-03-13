package tech.kronicle.plugins.aws.models;

import java.util.List;

public interface Page<T> {

    List<T> getItems();

    String getNextPage();
}
