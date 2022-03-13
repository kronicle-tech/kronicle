package tech.kronicle.plugins.aws.utils;

import tech.kronicle.plugins.aws.models.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.Objects.nonNull;

public final class PageFetcher {

    public static <T> List<T> fetchAllPages(Function<String, Page<T>> fetchPage) {
        List<T> items = new ArrayList<>();
        String nextToken = null;

        do {
            Page<T> page = fetchPage.apply(nextToken);
            items.addAll(page.getItems());
            nextToken = page.getNextPage();
        } while (nonNull(nextToken) && !nextToken.isEmpty());

        return items;
    }

    private PageFetcher() {
    }
}
