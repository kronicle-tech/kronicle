package tech.kronicle.service.tests.readme;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Priority;
import tech.kronicle.sdk.models.TestOutcome;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.sdk.models.readme.ReadmeState;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ReadmeTestTest {

    private final ReadmeTest underTest = new ReadmeTest();

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheTest() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Checks whether a component's repo includes a README file at its root");
    }

    @Test
    public void priorityShouldReturnHigh() {
        // When
        Priority returnValue = underTest.priority();

        // Then
        assertThat(returnValue).isEqualTo(Priority.HIGH);
    }

    @Test
    public void testShouldReturnFailWhenAComponentHasNoReadme() {
        // Given
        Component component = Component.builder().build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("readme")
                        .outcome(TestOutcome.FAIL)
                        .priority(Priority.HIGH)
                        .message("Component has no README file")
                        .build());
    }

    @Test
    public void testShouldReturnFailWhenAComponentHasAnEmptyReadme() {
        // Given
        Component component = createComponent(
                ReadmeState.builder()
                        .content("")
                        .build()
        );

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("readme")
                        .outcome(TestOutcome.FAIL)
                        .priority(Priority.HIGH)
                        .message("Component has an empty README file")
                        .build());
    }

    @Test
    public void testShouldReturnPassWhenAComponentHasANonEmptyReadme() {
        // Given
        Component component = createComponent(
                ReadmeState.builder()
                        .content("# Read Me")
                        .build()
        );

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("readme")
                        .outcome(TestOutcome.PASS)
                        .priority(Priority.HIGH)
                        .message("Component has a README file")
                        .build());
    }

    private Component createComponent(ReadmeState readme) {
        return Component.builder()
                .states(List.of(readme))
                .build();
    }
}
