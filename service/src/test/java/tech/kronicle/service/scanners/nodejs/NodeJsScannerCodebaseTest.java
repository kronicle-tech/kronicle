package tech.kronicle.service.scanners.nodejs;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareDependencyType;
import tech.kronicle.sdk.models.SoftwareScope;
import tech.kronicle.service.scanners.models.Codebase;
import tech.kronicle.service.scanners.models.Output;

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
        assertThat(software).containsExactly(Software.builder().name("core-js").version("3.21.0").packaging("npm-package").dependencyType(SoftwareDependencyType.DIRECT).build());
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
        assertThat(software).containsExactly(Software.builder().name("prettier").version("2.5.1").packaging("npm-package").dependencyType(SoftwareDependencyType.DIRECT).scope(SoftwareScope.DEV).build());
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
                Software.builder().name("@eslint/eslintrc").version("1.0.5").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("@humanwhocodes/config-array").version("0.9.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("@humanwhocodes/object-schema").version("1.2.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("acorn").version("8.7.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("acorn-jsx").version("5.3.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("ajv").version("6.12.6").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("ansi-regex").version("5.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("ansi-styles").version("4.3.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("argparse").version("2.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("balanced-match").version("1.0.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("brace-expansion").version("1.1.11").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("callsites").version("3.1.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("chalk").version("4.1.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("color-convert").version("2.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("color-name").version("1.1.4").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("concat-map").version("0.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("cross-spawn").version("7.0.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("debug").version("4.3.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("deep-is").version("0.1.4").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("doctrine").version("3.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("escape-string-regexp").version("4.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("eslint").version("8.8.0").packaging("npm-package").dependencyType(SoftwareDependencyType.DIRECT).scope(SoftwareScope.DEV).build(),
                Software.builder().name("eslint-scope").version("7.1.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("eslint-utils").version("3.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("eslint-visitor-keys").version("2.1.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("eslint-visitor-keys").version("3.2.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("espree").version("9.3.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("esquery").version("1.4.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("esrecurse").version("4.3.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("estraverse").version("5.3.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("esutils").version("2.0.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("fast-deep-equal").version("3.1.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("fast-json-stable-stringify").version("2.1.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("fast-levenshtein").version("2.0.6").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("file-entry-cache").version("6.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("flat-cache").version("3.0.4").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("flatted").version("3.2.5").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("fs.realpath").version("1.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("functional-red-black-tree").version("1.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("glob").version("7.2.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("glob-parent").version("6.0.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("globals").version("13.12.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("has-flag").version("4.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("ignore").version("4.0.6").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("ignore").version("5.2.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("import-fresh").version("3.3.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("imurmurhash").version("0.1.4").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("inflight").version("1.0.6").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("inherits").version("2.0.4").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("is-descriptor").version("3.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.DIRECT).build(),
                Software.builder().name("is-extglob").version("2.1.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("is-glob").version("4.0.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("isexe").version("2.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("js-yaml").version("4.1.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("json-schema-traverse").version("0.4.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("json-stable-stringify-without-jsonify").version("1.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("levn").version("0.4.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("lodash.merge").version("4.6.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("minimatch").version("3.0.4").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("ms").version("2.1.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("natural-compare").version("1.4.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("once").version("1.4.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("optionator").version("0.9.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("parent-module").version("1.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("path-is-absolute").version("1.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("path-key").version("3.1.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("prelude-ls").version("1.2.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("punycode").version("2.1.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("regexpp").version("3.2.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("resolve-from").version("4.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("rimraf").version("3.0.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("shebang-command").version("2.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("shebang-regex").version("3.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("strip-ansi").version("6.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("strip-json-comments").version("3.1.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("supports-color").version("7.2.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("text-table").version("0.2.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("type-check").version("0.4.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("type-fest").version("0.20.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("uri-js").version("4.4.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("v8-compile-cache").version("2.3.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("which").version("2.0.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("word-wrap").version("1.2.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().name("wrappy").version("1.0.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build()
        );
    }
}
