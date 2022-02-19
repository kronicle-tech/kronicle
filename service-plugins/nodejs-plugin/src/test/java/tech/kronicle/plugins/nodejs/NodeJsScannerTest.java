package tech.kronicle.plugins.nodejs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugintestutils.scanners.BaseCodebaseScannerTest;

import static org.assertj.core.api.Assertions.assertThat;

public class NodeJsScannerTest extends BaseCodebaseScannerTest {

    private NodeJsScanner underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new NodeJsScanner(null, null);
    }

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("nodejs");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Scans a component's codebase for any node.js package-lock.json or yarn.lock files to find what software is used");
    }

    @Test
    public void notesShouldReturnTheNotesForTheScanner() {
        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isEqualTo("If the scanner finds node.js package-lock.json or yarn.lock files, it will:\n"
                + "\n"
                + "* Find the names and versions of any npm packages used");
    }
}
