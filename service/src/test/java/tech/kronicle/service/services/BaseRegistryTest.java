package tech.kronicle.service.services;

import org.junit.jupiter.api.Test;
import org.pf4j.PluginManager;
import tech.kronicle.service.models.RegistryItem;
import tech.kronicle.service.services.testutils.FakePluginManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseRegistryTest {

    private TestBaseRegistry underTest;

    @Test
    public void getAllItemsReturnsAllItems() {
        // Given
        List<RegistryItem> items = List.of(
                new TestRegistryItemA("test-item-1"),
                new TestRegistryItemA("test-item-2"),
                new TestRegistryItemB("test-item-3"),
                new TestRegistryItemB("test-item-4")
        );
        underTest = new TestBaseRegistry(new FakePluginManager<>(items, RegistryItem.class));

        // When
        List<RegistryItem> returnValue = underTest.getAllItems();

        // Then
        assertThat(returnValue).containsExactlyElementsOf(items);
    }

    private static class TestBaseRegistry extends BaseRegistry<RegistryItem> {

        public TestBaseRegistry(PluginManager pluginManager) {
            super(pluginManager);
        }

        @Override
        protected Class<RegistryItem> getItemType() {
            return RegistryItem.class;
        }
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
