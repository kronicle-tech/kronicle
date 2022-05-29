package tech.kronicle.plugins.aws;

import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.aws.synthetics.services.SyntheticsService;
import tech.kronicle.plugintestutils.scanners.BaseScannerTest;
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.DiscoveredState;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.aws.testutils.ComponentUtils.createComponent;

public class AwsSyntheticsCanariesScannerTest extends BaseScannerTest {

    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

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
    public void scanShouldAddChecksToTheComponent() {
        // Given
        SyntheticsService service = mock(SyntheticsService.class);
        AwsSyntheticsCanariesScanner underTest = new AwsSyntheticsCanariesScanner(service);
        Component component = createComponent(1);
        CheckState check1 = createCheck(1, 1);
        CheckState check2 = createCheck(1, 2);
        CheckState check3 = createCheck(2, 1);
        CheckState check4 = createCheck(2, 2);
        when(service.getCanaryLastRunsForComponent(component)).thenReturn(List.of(
                check1,
                check2,
                check3,
                check4
        ));

        // When
        Output<Void, Component> returnValue = underTest.scan(component);

        // Then
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getErrors()).isEmpty();
        assertThat(returnValue.getCacheTtl()).isEqualTo(CACHE_TTL);
        Component transformedComponent = getMutatedComponent(returnValue, component);
        assertThat(transformedComponent).isEqualTo(
                component.withStates(List.of(
                        check1,
                        check2,
                        check3,
                        check4
                ))
        );
    }

    @Test
    public void scanShouldNotTransformTheComponentIfNoChecksAreFound() {
        // Given
        SyntheticsService service = mock(SyntheticsService.class);
        AwsSyntheticsCanariesScanner underTest = new AwsSyntheticsCanariesScanner(service);
        Component component = createComponent(1);
        when(service.getCanaryLastRunsForComponent(component)).thenReturn(List.of());

        // When
        Output<Void, Component> returnValue = underTest.scan(component);

        // Then
        assertThat(returnValue).isEqualTo(new Output<>(null, null, null, CACHE_TTL));
    }

    private CheckState createCheck(
            int profileAndRegionNumber,
            int checkNumber
    ) {
        return CheckState.builder()
                .environmentId(createEnvironmentId(profileAndRegionNumber))
                .name("test-check-name-" + profileAndRegionNumber + "-" + checkNumber)
                .build();
    }

    private String createEnvironmentId(int environmentNumber) {
        return "test-environment-id-" + environmentNumber;
    }
}
