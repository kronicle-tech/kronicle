package tech.kronicle.plugins.nodejs;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareDependencyType;
import tech.kronicle.sdk.models.SoftwareScope;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest()
@ContextConfiguration(classes = { NodeJsScannerTestConfiguration.class})
public class NodeJsScannerCodebaseTest extends BaseNodeJsScannerTest {

    @Autowired
    private NodeJsScanner underTest;

    @Override
    protected Logger log() {
        return log;
    }

    @Test
    public void shouldHandleNoPackageLockFiles() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("NoPackageLockFiles"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatNodeJsIsNotUsed(component);
        assertThat(getSoftwareRepositories(component)).isEmpty();
        List<Software> software = getSoftware(component);
        assertThat(software).isEmpty();
    }

    @Test
    public void shouldNpmPackageLockButNoPackageJson() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("NpmPackageLockButNoPackageJson"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatNodeJsIsUsed(component);
        assertThat(getSoftwareRepositories(component)).isEmpty();
        List<Software> software = getSoftware(component);
        assertThat(software).isEmpty();
    }

    @Test
    public void shouldHandleNpmPackageLockThatIsEmpty() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("NpmPackageLockThatIsEmpty"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatNodeJsIsUsed(component);
        assertThat(getSoftwareRepositories(component)).isEmpty();
        List<Software> software = getSoftware(component);
        assertThat(software).isEmpty();
    }

    @Test
    public void shouldHandleNpmPackageLockWithSimpleDependency() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("NpmPackageLockWithSimpleDependency"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatNodeJsIsUsed(component);
        assertThat(getSoftwareRepositories(component)).isEmpty();
        List<Software> software = getSoftware(component);
        assertThat(software).containsExactly(Software.builder().scannerId("nodejs").name("core-js").version("3.21.0").packaging("npm-package").dependencyType(SoftwareDependencyType.DIRECT).build());
    }

    @Test
    public void shouldHandleNpmPackageLockWithSimpleDevDependency() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("NpmPackageLockWithSimpleDevDependency"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatNodeJsIsUsed(component);
        assertThat(getSoftwareRepositories(component)).isEmpty();
        List<Software> software = getSoftware(component);
        assertThat(software).containsExactly(Software.builder().scannerId("nodejs").name("prettier").version("2.5.1").packaging("npm-package").dependencyType(SoftwareDependencyType.DIRECT).scope(SoftwareScope.DEV).build());
    }

    @Test
    public void shouldHandleNpmPackageLockWithComplexDependencies() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("NpmPackageLockWithComplexDependencies"));

        // When
        Output<Void> output = underTest.scan(codebase);

        // Then
        assertThat(output.getOutput()).isNull();
        Component component = getMutatedComponent(output);
        assertThatNodeJsIsUsed(component);
        assertThat(getSoftwareRepositories(component)).isEmpty();
        List<Software> software = getSoftware(component);
        assertThat(software).containsExactly(
                Software.builder().scannerId("nodejs").name("@eslint/eslintrc").version("1.0.5").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("@humanwhocodes/config-array").version("0.9.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("@humanwhocodes/object-schema").version("1.2.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("acorn").version("8.7.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("acorn-jsx").version("5.3.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("ajv").version("6.12.6").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("ansi-regex").version("5.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("ansi-styles").version("4.3.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("argparse").version("2.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("balanced-match").version("1.0.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("brace-expansion").version("1.1.11").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("callsites").version("3.1.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("chalk").version("4.1.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("color-convert").version("2.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("color-name").version("1.1.4").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("concat-map").version("0.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("cross-spawn").version("7.0.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("debug").version("4.3.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("deep-is").version("0.1.4").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("doctrine").version("3.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("escape-string-regexp").version("4.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("eslint").version("8.8.0").packaging("npm-package").dependencyType(SoftwareDependencyType.DIRECT).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("eslint-scope").version("7.1.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("eslint-utils").version("3.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("eslint-visitor-keys").version("2.1.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("eslint-visitor-keys").version("3.2.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("espree").version("9.3.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("esquery").version("1.4.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("esrecurse").version("4.3.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("estraverse").version("5.3.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("esutils").version("2.0.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("fast-deep-equal").version("3.1.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("fast-json-stable-stringify").version("2.1.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("fast-levenshtein").version("2.0.6").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("file-entry-cache").version("6.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("flat-cache").version("3.0.4").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("flatted").version("3.2.5").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("fs.realpath").version("1.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("functional-red-black-tree").version("1.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("glob").version("7.2.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("glob-parent").version("6.0.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("globals").version("13.12.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("has-flag").version("4.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("ignore").version("4.0.6").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("ignore").version("5.2.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("import-fresh").version("3.3.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("imurmurhash").version("0.1.4").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("inflight").version("1.0.6").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("inherits").version("2.0.4").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("is-descriptor").version("3.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.DIRECT).build(),
                Software.builder().scannerId("nodejs").name("is-extglob").version("2.1.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("is-glob").version("4.0.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("isexe").version("2.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("js-yaml").version("4.1.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("json-schema-traverse").version("0.4.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("json-stable-stringify-without-jsonify").version("1.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("levn").version("0.4.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("lodash.merge").version("4.6.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("minimatch").version("3.0.4").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("ms").version("2.1.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("natural-compare").version("1.4.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("once").version("1.4.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("optionator").version("0.9.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("parent-module").version("1.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("path-is-absolute").version("1.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("path-key").version("3.1.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("prelude-ls").version("1.2.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("punycode").version("2.1.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("regexpp").version("3.2.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("resolve-from").version("4.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("rimraf").version("3.0.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("shebang-command").version("2.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("shebang-regex").version("3.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("strip-ansi").version("6.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("strip-json-comments").version("3.1.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("supports-color").version("7.2.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("text-table").version("0.2.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("type-check").version("0.4.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("type-fest").version("0.20.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("uri-js").version("4.4.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("v8-compile-cache").version("2.3.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("which").version("2.0.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("word-wrap").version("1.2.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("wrappy").version("1.0.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build()
        );
    }
}
