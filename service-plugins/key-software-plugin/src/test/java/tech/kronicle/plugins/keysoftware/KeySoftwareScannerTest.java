package tech.kronicle.plugins.keysoftware;

import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.keysoftware.config.KeySoftwareRuleConfig;
import tech.kronicle.plugins.keysoftware.services.KeySoftwareRuleProvider;
import tech.kronicle.plugintestutils.scanners.BaseScannerTest;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.KeySoftware;
import tech.kronicle.sdk.models.Software;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KeySoftwareScannerTest extends BaseScannerTest {

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // Given
        KeySoftwareScanner underTest = createUnderTest(List.of());

        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("key-software");
    }
    
    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // Given
        KeySoftwareScanner underTest = createUnderTest(List.of());

        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo(
                "Processes all software found by other scanners and looks for certain configured `key software` to "
                + "find what version(s), if any, a component uses.  Key software is typically things like Gradle "
                + "and Spring Boot");
    }

    @Test
    public void notesShouldReturnNull() {
        // Given
        KeySoftwareScanner underTest = createUnderTest(List.of());

        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void scanShouldHandleNoSoftware() {
        // Given
        KeySoftwareScanner underTest = createUnderTest(List.of(new KeySoftwareRuleConfig("test", "test")));

        // When
        Output<Void> returnValue = underTest.scan(createComponent(List.of()));

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        List<KeySoftware> keySoftware = getMutatedComponent(returnValue).getKeySoftware();
        assertThat(keySoftware).isEmpty();
    }

    @Test
    public void scanShouldHandleNoRules() {
        // Given
        KeySoftwareScanner underTest = createUnderTest(List.of());

        // When
        Output<Void> returnValue = underTest.scan(createComponent(List.of(Software.builder().build())));

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        List<KeySoftware> keySoftware = getMutatedComponent(returnValue).getKeySoftware();
        assertThat(keySoftware).isEmpty();
    }

    @Test
    public void scanShouldHandleRuleThatDoesNotMatch() {
        // Given
        KeySoftwareScanner underTest = createUnderTest(List.of(new KeySoftwareRuleConfig("test-software-name", "test-key-software-name")));

        // When
        Output<Void> returnValue = underTest.scan(createComponent(List.of(Software.builder().name("other-software-name").build())));

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        List<KeySoftware> keySoftware = getMutatedComponent(returnValue).getKeySoftware();
        assertThat(keySoftware).isEmpty();
    }

    @Test
    public void scanShouldMatchKeySoftwareItem() {
        // Given
        KeySoftwareScanner underTest = createUnderTest(List.of(new KeySoftwareRuleConfig("test-software-name", "test-key-software-name")));
        Software softwareItem = Software.builder()
                .name("test-software-name")
                .version("1.2.3")
                .build();

        // When
        Output<Void> returnValue = underTest.scan(createComponent(List.of(softwareItem)));

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        List<KeySoftware> keySoftware = getMutatedComponent(returnValue).getKeySoftware();
        assertThat(keySoftware).hasSize(1);
        KeySoftware keySoftwareItem;
        keySoftwareItem = keySoftware.get(0);
        assertThat(keySoftwareItem.getName()).isEqualTo("test-key-software-name");
        assertThat(keySoftwareItem.getVersions()).containsExactly("1.2.3");
    }

    @Test
    public void scanShouldMatchMultipleKeySoftwareItems() {
        // Given
        KeySoftwareScanner underTest = createUnderTest(List.of(new KeySoftwareRuleConfig("test-software-name", "test-key-software-name")));
        Software softwareItem1 = Software.builder()
                .name("test-software-name")
                .version("4.5.6")
                .build();
        Software softwareItem2 = Software.builder()
                .name("test-software-name")
                .version("1.2.3")
                .build();

        // When
        Output<Void> returnValue = underTest.scan(createComponent(List.of(softwareItem1, softwareItem2)));

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        List<KeySoftware> keySoftware = getMutatedComponent(returnValue).getKeySoftware();
        assertThat(keySoftware).hasSize(1);
        KeySoftware keySoftwareItem;
        keySoftwareItem = keySoftware.get(0);
        assertThat(keySoftwareItem.getName()).isEqualTo("test-key-software-name");
        assertThat(keySoftwareItem.getVersions()).containsExactly("4.5.6", "1.2.3");
    }

    @Test
    public void scanShouldMatchMultipleKeySoftwareItemsAndSortThemByVersion() {
        // Given
        KeySoftwareScanner underTest = createUnderTest(List.of(new KeySoftwareRuleConfig("test-software-name", "test-key-software-name")));
        Software softwareItem1 = Software.builder()
                .name("test-software-name")
                .version("1.2.3")
                .build();
        Software softwareItem2 = Software.builder()
                .name("test-software-name")
                .version("4.5.6")
                .build();

        // When
        Output<Void> returnValue = underTest.scan(createComponent(List.of(softwareItem1, softwareItem2)));

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        List<KeySoftware> keySoftware = getMutatedComponent(returnValue).getKeySoftware();
        assertThat(keySoftware).hasSize(1);
        KeySoftware keySoftwareItem;
        keySoftwareItem = keySoftware.get(0);
        assertThat(keySoftwareItem.getName()).isEqualTo("test-key-software-name");
        assertThat(keySoftwareItem.getVersions()).containsExactly("4.5.6", "1.2.3");
    }

    @Test
    public void scanShouldMatchMultipleKeySoftwareItemsAndDeduplicateThem() {
        // Given
        KeySoftwareScanner underTest = createUnderTest(List.of(new KeySoftwareRuleConfig("test-software-name", "test-key-software-name")));
        Software softwareItem1 = Software.builder()
                .name("test-software-name")
                .version("1.2.3")
                .build();
        Software softwareItem2 = Software.builder()
                .name("test-software-name")
                .version("1.2.3")
                .build();

        // When
        Output<Void> returnValue = underTest.scan(createComponent(List.of(softwareItem1, softwareItem2)));

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        List<KeySoftware> keySoftware = getMutatedComponent(returnValue).getKeySoftware();
        assertThat(keySoftware).hasSize(1);
        KeySoftware keySoftwareItem;
        keySoftwareItem = keySoftware.get(0);
        assertThat(keySoftwareItem.getName()).isEqualTo("test-key-software-name");
        assertThat(keySoftwareItem.getVersions()).containsExactly("1.2.3");
    }

    @Test
    public void scanShouldMatchMultipleRules() {
        // Given
        KeySoftwareRuleConfig rule1 = new KeySoftwareRuleConfig("test-software-name-1", "test-key-software-name-1");
        KeySoftwareRuleConfig rule2 = new KeySoftwareRuleConfig("test-software-name-2", "test-key-software-name-2");
        KeySoftwareScanner underTest = createUnderTest(List.of(rule1, rule2));
        Software softwareItem1 = Software.builder()
                .name("test-software-name-1")
                .version("1.2.3")
                .build();
        Software softwareItem2 = Software.builder()
                .name("test-software-name-2")
                .version("4.5.6")
                .build();

        // When
        Output<Void> returnValue = underTest.scan(createComponent(List.of(softwareItem1, softwareItem2)));

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        List<KeySoftware> keySoftware = getMutatedComponent(returnValue).getKeySoftware();
        assertThat(keySoftware).hasSize(2);
        KeySoftware keySoftwareItem;
        keySoftwareItem = keySoftware.get(0);
        assertThat(keySoftwareItem.getName()).isEqualTo("test-key-software-name-1");
        assertThat(keySoftwareItem.getVersions()).containsExactly("1.2.3");
        keySoftwareItem = keySoftware.get(1);
        assertThat(keySoftwareItem.getName()).isEqualTo("test-key-software-name-2");
        assertThat(keySoftwareItem.getVersions()).containsExactly("4.5.6");
    }

    private KeySoftwareScanner createUnderTest(List<KeySoftwareRuleConfig> rules) {
        KeySoftwareRuleProvider mockRuleProvider = mock(KeySoftwareRuleProvider.class);
        when(mockRuleProvider.getRules()).thenReturn(rules);
        return new KeySoftwareScanner(mockRuleProvider);
    }

    private Component createComponent(List<Software> software) {
        return Component.builder()
                .software(software)
                .build();
    }
}
