package tech.kronicle.plugins.keysoftware.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.keysoftware.config.KeySoftwareConfig;
import tech.kronicle.plugins.keysoftware.config.KeySoftwareRuleConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class KeySoftwareRuleProviderTest {

    private static final KeySoftwareRuleConfig RULE_1 = new KeySoftwareRuleConfig("test-software-name-1", "test-key-software-name-1");
    private static final KeySoftwareRuleConfig RULE_2 = new KeySoftwareRuleConfig("test-software-name-2", "test-key-software-name-2");
    private static final KeySoftwareRuleConfig RULE_3 = new KeySoftwareRuleConfig("test-software-name-3", "test-key-software-name-3");
    private static final KeySoftwareRuleConfig RULE_4 = new KeySoftwareRuleConfig("test-software-name-4", "test-key-software-name-4");

    @Test
    public void shouldHandleCustomRulesListBeingNull() {
        // Given
        KeySoftwareRuleProvider underTest = new KeySoftwareRuleProvider(new KeySoftwareConfig(
                true,
                List.of(RULE_1, RULE_2),
                null
        ));
        
        // When
        List<KeySoftwareRuleConfig> returnValue = underTest.getRules();
        
        // Then
        assertThat(returnValue).containsExactly(RULE_1, RULE_2);
    }

    @Test
    public void shouldHandleCustomRulesListBeingEmpty() {
        // Given
        KeySoftwareRuleProvider underTest = new KeySoftwareRuleProvider(new KeySoftwareConfig(
                true,
                List.of(RULE_1, RULE_2),
                List.of()
        ));

        // When
        List<KeySoftwareRuleConfig> returnValue = underTest.getRules();

        // Then
        assertThat(returnValue).containsExactly(RULE_1, RULE_2);
    }

    @Test
    public void shouldUseNotDefaultRulesWhenTheyAreDisabled() {
        // Given
        KeySoftwareRuleProvider underTest = new KeySoftwareRuleProvider(new KeySoftwareConfig(
                false,
                List.of(RULE_1, RULE_2),
                List.of()
        ));

        // When
        List<KeySoftwareRuleConfig> returnValue = underTest.getRules();

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void shouldUseBothDefaultRulesAndCustomRulesWhenDefaultRulesAreEnabled() {
        // Given
        KeySoftwareRuleProvider underTest = new KeySoftwareRuleProvider(new KeySoftwareConfig(
                true,
                List.of(RULE_1, RULE_2),
                List.of(RULE_3, RULE_4)
        ));

        // When
        List<KeySoftwareRuleConfig> returnValue = underTest.getRules();

        // Then
        assertThat(returnValue).containsExactly(RULE_1, RULE_2, RULE_3, RULE_4);
    }

    @Test
    public void shouldUseJustCustomRulesWhenDefaultRulesAreDisabled() {
        // Given
        KeySoftwareRuleProvider underTest = new KeySoftwareRuleProvider(new KeySoftwareConfig(
                false,
                List.of(RULE_1, RULE_2),
                List.of(RULE_3, RULE_4)
        ));

        // When
        List<KeySoftwareRuleConfig> returnValue = underTest.getRules();

        // Then
        assertThat(returnValue).containsExactly(RULE_3, RULE_4);
    }
}
