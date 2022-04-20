package tech.kronicle.plugins.gitlab.models.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import tech.kronicle.plugins.gitlab.constants.GitLabApiHeaders;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@Getter
public class PagedResource<T> {

    private static final PagedResource<?> EMPTY = new PagedResource<>();

    private final List<T> items;
    private final Integer nextPage;

    @SneakyThrows
    public PagedResource(
            HttpResponse<String> response,
            TypeReference<List<T>> bodyTypeReference,
            ObjectMapper objectMapper
    ) {
        items = objectMapper.readValue(response.body(), bodyTypeReference);
        nextPage = getNextPage(response);
    }

    private PagedResource() {
        items = List.of();
        nextPage = null;
    }

    public static <T> PagedResource<T> empty() {
      @SuppressWarnings("unchecked")
      PagedResource<T> value = (PagedResource<T>) EMPTY;
      return value;
    }

    private Integer getNextPage(HttpResponse<String> response) {
        return getOptionalNextPage(response).map(Integer::parseInt).orElse(null);
    }

    private Optional<String> getOptionalNextPage(HttpResponse<String> response) {
        return response.headers().firstValue(GitLabApiHeaders.X_NEXT_PAGE)
                .filter(value -> !value.isEmpty());
    }
}
