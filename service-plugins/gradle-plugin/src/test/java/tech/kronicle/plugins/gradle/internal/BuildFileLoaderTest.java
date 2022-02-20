package tech.kronicle.plugins.gradle.internal;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.plugins.gradle.internal.services.BuildFileCache;
import tech.kronicle.plugins.gradle.internal.services.BuildFileLoader;
import tech.kronicle.plugins.gradle.internal.services.PropertyExpander;
import tech.kronicle.plugins.gradle.internal.services.PropertyRetriever;
import tech.kronicle.service.utils.FileUtils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BuildFileLoaderTest {

    @Mock
    private FileUtils fileUtils;
    private BuildFileLoader underTest;

    @BeforeEach
    public void setUp() {
        underTest = new BuildFileLoader(fileUtils, new BuildFileCache(), new PropertyExpander(new PropertyRetriever()));
    }

    @Test
    public void loadBuildFileShouldLoadBuildFile() {
        // Given
        Path buildFile = Path.of("/", "tmp", "some.gradle");
        Path codebaseDir = Path.of("/tmp");
        when(fileUtils.readFileContent(Path.of("/tmp/some.gradle"))).thenReturn("\"Hello, World!\"");

        // When
        List<ASTNode> nodes = underTest.loadBuildFile(buildFile, codebaseDir);

        // Then
        assertThat(nodes).hasSize(2);
        ASTNode node;
        node = nodes.get(0);
        assertThat(node).isInstanceOf(BlockStatement.class);
        node = nodes.get(1);
        assertThat(node).isInstanceOf(ClassNode.class);
    }

    @Test
    public void loadBuildFileShouldCheckBuildFileIsWithinCodebaseDir() {
        // Given
        Path buildFile = Path.of("some.gradle");
        Path codebaseDir = Path.of("/tmp");

        // When
        Throwable thrown = catchThrowable(() -> underTest.loadBuildFile(buildFile, codebaseDir));

        // Then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertThat(thrown).hasMessage("buildFile path \"some.gradle\" is not within codebaseDir path \"/tmp\"");
    }

    @Test
    public void resolveApplyFromFileShouldResolveApplyFromFileNameAgainstCodebaseDir() {
        // Given
        String applyFromFileName = "some.gradle";
        Path codebaseDir = Path.of("/tmp");
        Path buildFile = Path.of("/tmp/subproject/build.gradle");
        Map<String, String> properties = new HashMap<>();

        // When
        Path returnValue = underTest.resolveApplyFromFile(applyFromFileName, buildFile, properties);

        // Then
        assertThat(returnValue.toString()).isEqualTo("/tmp/subproject/some.gradle");
    }

    @Test
    public void resolveApplyFromFileShouldExpandApplyFromFileNameVariables() {
        // Given
        String applyFromFileName = "some_${variable}.gradle";
        Path codebaseDir = Path.of("/tmp");
        Path buildFile = Path.of("/tmp/subproject/build.gradle");
        Map<String, String> properties = new HashMap<>();
        properties.put("variable", "test_value");

        // When
        Path returnValue = underTest.resolveApplyFromFile(applyFromFileName, buildFile, properties);

        // Then
        assertThat(returnValue.toString()).isEqualTo("/tmp/subproject/some_test_value.gradle");
    }
}
