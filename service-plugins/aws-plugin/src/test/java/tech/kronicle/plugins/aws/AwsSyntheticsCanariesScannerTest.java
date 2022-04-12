package tech.kronicle.plugins.aws;

import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.synthetics.services.SyntheticsService;
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.ComponentState;
import tech.kronicle.sdk.models.EnvironmentPluginState;
import tech.kronicle.sdk.models.EnvironmentState;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.aws.testutils.AwsProfileAndRegionUtils.createProfileAndRegion;
import static tech.kronicle.plugins.aws.testutils.ComponentUtils.createComponent;

public class AwsSyntheticsCanariesScannerTest {

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // Given
        AwsSyntheticsCanariesScanner underTest = new AwsSyntheticsCanariesScanner(null);

        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("aws-synthetics-canaries");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // Given
        AwsSyntheticsCanariesScanner underTest = new AwsSyntheticsCanariesScanner(null);

        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo(
                "Finds AWS CloudWatch Synthetics Canaries and adds the state of those canaries to components"
        );
    }

    @Test
    public void notesShouldReturnNull() {
        // Given
        AwsSyntheticsCanariesScanner underTest = new AwsSyntheticsCanariesScanner(null);

        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void refreshShouldRefreshTheService() {
        // Given
        SyntheticsService service = mock(SyntheticsService.class);
        AwsSyntheticsCanariesScanner underTest = new AwsSyntheticsCanariesScanner(service);

        // When
        underTest.refresh(ComponentMetadata.builder().build());

        // Then
        verify(service).refresh();
    }

    @Test
    public void scanShouldAddLogSummariesToTheComponent() {
        // Given
        SyntheticsService service = mock(SyntheticsService.class);
        AwsSyntheticsCanariesScanner underTest = new AwsSyntheticsCanariesScanner(service);
        Component component = createComponent(1);
        when(service.getCanaryLastRunsForComponent(component)).thenReturn(List.of(
                createChecksForProfileAndRegion(1),
                createChecksForProfileAndRegion(2)
        ));

        // When
        Output<Void> returnValue = underTest.scan(component);

        // Then
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getErrors()).isEmpty();
        UnaryOperator<Component> componentTransformer = returnValue.getComponentTransformer();
        Component transformedComponent = componentTransformer.apply(component);
        assertThat(transformedComponent).isEqualTo(
                component.withState(
                        ComponentState.builder()
                                .environments(List.of(
                                        createEnvironment(1),
                                        createEnvironment(2)
                                ))
                                .build()
                )
        );
    }

    @Test
    public void scanShouldNotTransformTheComponentIfNoLogSummariesAreFound() {
        // Given
        SyntheticsService service = mock(SyntheticsService.class);
        AwsSyntheticsCanariesScanner underTest = new AwsSyntheticsCanariesScanner(service);
        Component component = createComponent(1);
        when(service.getCanaryLastRunsForComponent(component)).thenReturn(List.of(
                createEmptyChecksForProfileAndRegion(1),
                createEmptyChecksForProfileAndRegion(2)
        ));

        // When
        Output<Void> returnValue = underTest.scan(component);

        // Then
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getErrors()).isEmpty();
        UnaryOperator<Component> componentTransformer = returnValue.getComponentTransformer();
        Component transformedComponent = componentTransformer.apply(component);
        assertThat(transformedComponent).isEqualTo(component);
    }

    private Map.Entry<AwsProfileAndRegion, List<CheckState>> createChecksForProfileAndRegion(
            int profileAndRegionNumber
    ) {
        return Map.entry(createProfileAndRegion(profileAndRegionNumber), List.of(
                createCheck(profileAndRegionNumber, 1),
                createCheck(profileAndRegionNumber, 2)
        ));
    }

    private Map.Entry<AwsProfileAndRegion, List<CheckState>> createEmptyChecksForProfileAndRegion(
            int profileAndRegionNumber
    ) {
        return Map.entry(createProfileAndRegion(profileAndRegionNumber), List.of());
    }

    private EnvironmentState createEnvironment(int environmentNumber) {
        return EnvironmentState.builder()
                .id("test-environment-id-" + environmentNumber)
                .plugins(List.of(
                        EnvironmentPluginState.builder()
                                .id("aws")
                                .checks(List.of(
                                        CheckState.builder()
                                                .name("test-check-name-" + environmentNumber + "-1")
                                                .build(),
                                        CheckState.builder()
                                                .name("test-check-name-" + environmentNumber + "-2")
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }

    private CheckState createCheck(
            int profileAndRegionNumber,
            int checkNumber
    ) {
        return CheckState.builder()
                .name("test-check-name-" + profileAndRegionNumber + "-" + checkNumber)
                .build();
    }
}
