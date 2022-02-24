package tech.kronicle.service.services;

import org.junit.jupiter.api.Test;
import org.pf4j.PluginManager;
import tech.kronicle.pluginapi.ExtensionPointWithId;
import tech.kronicle.service.services.testutils.FakePluginManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseExtensionRegistryTest {

    private TestBaseExtensionRegistry underTest;

    @Test
    public void getAllItemsReturnsAllItems() {
        // Given
        List<ExtensionPointWithId> items = List.of(
                new TestExtensionPointWithIdA("test-item-1"),
                new TestExtensionPointWithIdA("test-item-2"),
                new TestExtensionPointWithIdB("test-item-3"),
                new TestExtensionPointWithIdB("test-item-4")
        );
        underTest = new TestBaseExtensionRegistry(new FakePluginManager<>(items, ExtensionPointWithId.class));

        // When
        List<ExtensionPointWithId> returnValue = underTest.getAllItems();

        // Then
        assertThat(returnValue).containsExactlyElementsOf(items);
    }

    private static class TestBaseExtensionRegistry extends BaseExtensionRegistry<ExtensionPointWithId> {

        public TestBaseExtensionRegistry(PluginManager pluginManager) {
            super(pluginManager);
        }

        @Override
        protected Class<ExtensionPointWithId> getItemType() {
            return ExtensionPointWithId.class;
        }
    }

    private static class TestExtensionPointWithIdA implements ExtensionPointWithId {

        private String id;

        public TestExtensionPointWithIdA(String id) {
            this.id = id;
        }

        @Override
        public String id() {
            return id;
        }
    }

    private static class TestExtensionPointWithIdB implements ExtensionPointWithId {

        private String id;

        public TestExtensionPointWithIdB(String id) {
            this.id = id;
        }

        @Override
        public String id() {
            return id;
        }
    }
}
