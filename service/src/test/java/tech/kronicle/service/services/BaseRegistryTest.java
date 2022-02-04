package tech.kronicle.service.services;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseRegistryTest {

    private BaseRegistry<RegistryItem> underTest;

    @Test
    public void getAllItemsReturnsAllItems() {
        // Given
        List<RegistryItem> items = List.of(
                new TestRegistryItemA("test-item-1"),
                new TestRegistryItemA("test-item-2"),
                new TestRegistryItemB("test-item-3"),
                new TestRegistryItemB("test-item-4")
        );
        underTest = new BaseRegistry<>(items);

        // When
        List<RegistryItem> returnValue = underTest.getAllItems();

        // Then
        assertThat(returnValue).containsExactlyElementsOf(items);
    }

    private static class TestRegistryItemA implements RegistryItem {

        private String id;

        public TestRegistryItemA(String id) {
            this.id = id;
        }

        @Override
        public String id() {
            return id;
        }
    }

    private static class TestRegistryItemB implements RegistryItem {

        private String id;

        public TestRegistryItemB(String id) {
            this.id = id;
        }

        @Override
        public String id() {
            return id;
        }
    }
}
