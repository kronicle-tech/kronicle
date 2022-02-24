package tech.kronicle.plugins.keysoftware;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.KeySoftware;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.plugintestutils.scanners.BaseScannerTest;
import tech.kronicle.plugins.keysoftware.config.KeySoftwareConfig;
import tech.kronicle.plugins.keysoftware.config.KeySoftwareRule;
import tech.kronicle.pluginapi.scanners.models.Output;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class KeySoftwareScannerTest extends BaseScannerTest {

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // Given
        KeySoftwareScanner underTest = new KeySoftwareScanner(new KeySoftwareConfig(List.of()));

        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("key-software");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // Given
        KeySoftwareScanner underTest = new KeySoftwareScanner(new KeySoftwareConfig(List.of()));

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
        KeySoftwareScanner underTest = new KeySoftwareScanner(new KeySoftwareConfig(List.of()));

        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void scanShouldHandleNoSoftware() {
        // Given
        KeySoftwareConfig config = new KeySoftwareConfig(List.of(new KeySoftwareRule("test", "test")));
        KeySoftwareScanner underTest = new KeySoftwareScanner(config);

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
        KeySoftwareConfig config = new KeySoftwareConfig(List.of());
        KeySoftwareScanner underTest = new KeySoftwareScanner(config);

        // When
        Output<Void> returnValue = underTest.scan(createComponent(List.of(Software.builder().build())));

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        List<KeySoftware> keySoftware = getMutatedComponent(returnValue).getKeySoftware();
        assertThat(keySoftware).isEmpty();
    }

    @Test
    public void scanShouldHandleNullRules() {
        // Given
        KeySoftwareConfig config = new KeySoftwareConfig(null);
        KeySoftwareScanner underTest = new KeySoftwareScanner(config);

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
        KeySoftwareConfig config = new KeySoftwareConfig(List.of(new KeySoftwareRule("test-software-name", "test-key-software-name")));
        KeySoftwareScanner underTest = new KeySoftwareScanner(config);

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
        KeySoftwareConfig config = new KeySoftwareConfig(List.of(new KeySoftwareRule("test-software-name", "test-key-software-name")));
        KeySoftwareScanner underTest = new KeySoftwareScanner(config);
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
        KeySoftwareConfig config = new KeySoftwareConfig(List.of(new KeySoftwareRule("test-software-name", "test-key-software-name")));
        KeySoftwareScanner underTest = new KeySoftwareScanner(config);
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
        KeySoftwareConfig config = new KeySoftwareConfig(List.of(new KeySoftwareRule("test-software-name", "test-key-software-name")));
        KeySoftwareScanner underTest = new KeySoftwareScanner(config);
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
        KeySoftwareConfig config = new KeySoftwareConfig(List.of(new KeySoftwareRule("test-software-name", "test-key-software-name")));
        KeySoftwareScanner underTest = new KeySoftwareScanner(config);
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
        KeySoftwareRule rule1 = new KeySoftwareRule("test-software-name-1", "test-key-software-name-1");
        KeySoftwareRule rule2 = new KeySoftwareRule("test-software-name-2", "test-key-software-name-2");
        KeySoftwareConfig config = new KeySoftwareConfig(List.of(rule1, rule2));
        KeySoftwareScanner underTest = new KeySoftwareScanner(config);
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

    private Component createComponent(List<Software> software) {
        return Component.builder()
                .software(software)
                .build();
    }
}
