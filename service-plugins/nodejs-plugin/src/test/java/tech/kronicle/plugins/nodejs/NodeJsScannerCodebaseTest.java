package tech.kronicle.plugins.nodejs;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.nodejs.internal.services.npm.NpmPackageExtractor;
import tech.kronicle.sdk.models.SoftwareType;
import tech.kronicle.utils.FileUtils;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareDependencyType;
import tech.kronicle.sdk.models.SoftwareScope;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.utils.FileUtilsFactory.createFileUtils;
import static tech.kronicle.utils.JsonMapperFactory.createJsonMapper;

@Slf4j
public class NodeJsScannerCodebaseTest extends BaseNodeJsScannerTest {

    private final FileUtils fileUtils = createFileUtils();
    private final NodeJsScanner underTest = new NodeJsScanner(fileUtils, new NpmPackageExtractor(fileUtils, createJsonMapper()));

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
        assertThat(software).containsExactly(Software.builder().scannerId("nodejs").name("core-js").type(SoftwareType.NPM_PACKAGE).version("3.21.0").packaging("npm-package").dependencyType(SoftwareDependencyType.DIRECT).build());
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
        assertThat(software).containsExactly(Software.builder().scannerId("nodejs").name("prettier").type(SoftwareType.NPM_PACKAGE).version("2.5.1").packaging("npm-package").dependencyType(SoftwareDependencyType.DIRECT).scope(SoftwareScope.DEV).build());
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
                Software.builder().scannerId("nodejs").name("@eslint/eslintrc").type(SoftwareType.NPM_PACKAGE).version("1.0.5").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("@humanwhocodes/config-array").type(SoftwareType.NPM_PACKAGE).version("0.9.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("@humanwhocodes/object-schema").type(SoftwareType.NPM_PACKAGE).version("1.2.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("acorn").type(SoftwareType.NPM_PACKAGE).version("8.7.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("acorn-jsx").type(SoftwareType.NPM_PACKAGE).version("5.3.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("ajv").type(SoftwareType.NPM_PACKAGE).version("6.12.6").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("ansi-regex").type(SoftwareType.NPM_PACKAGE).version("5.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("ansi-styles").type(SoftwareType.NPM_PACKAGE).version("4.3.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("argparse").type(SoftwareType.NPM_PACKAGE).version("2.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("balanced-match").type(SoftwareType.NPM_PACKAGE).version("1.0.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("brace-expansion").type(SoftwareType.NPM_PACKAGE).version("1.1.11").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("callsites").type(SoftwareType.NPM_PACKAGE).version("3.1.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("chalk").type(SoftwareType.NPM_PACKAGE).version("4.1.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("color-convert").type(SoftwareType.NPM_PACKAGE).version("2.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("color-name").type(SoftwareType.NPM_PACKAGE).version("1.1.4").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("concat-map").type(SoftwareType.NPM_PACKAGE).version("0.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("cross-spawn").type(SoftwareType.NPM_PACKAGE).version("7.0.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("debug").type(SoftwareType.NPM_PACKAGE).version("4.3.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("deep-is").type(SoftwareType.NPM_PACKAGE).version("0.1.4").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("doctrine").type(SoftwareType.NPM_PACKAGE).version("3.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("escape-string-regexp").type(SoftwareType.NPM_PACKAGE).version("4.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("eslint").type(SoftwareType.NPM_PACKAGE).version("8.8.0").packaging("npm-package").dependencyType(SoftwareDependencyType.DIRECT).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("eslint-scope").type(SoftwareType.NPM_PACKAGE).version("7.1.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("eslint-utils").type(SoftwareType.NPM_PACKAGE).version("3.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("eslint-visitor-keys").type(SoftwareType.NPM_PACKAGE).version("2.1.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("eslint-visitor-keys").type(SoftwareType.NPM_PACKAGE).version("3.2.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("espree").type(SoftwareType.NPM_PACKAGE).version("9.3.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("esquery").type(SoftwareType.NPM_PACKAGE).version("1.4.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("esrecurse").type(SoftwareType.NPM_PACKAGE).version("4.3.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("estraverse").type(SoftwareType.NPM_PACKAGE).version("5.3.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("esutils").type(SoftwareType.NPM_PACKAGE).version("2.0.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("fast-deep-equal").type(SoftwareType.NPM_PACKAGE).version("3.1.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("fast-json-stable-stringify").type(SoftwareType.NPM_PACKAGE).version("2.1.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("fast-levenshtein").type(SoftwareType.NPM_PACKAGE).version("2.0.6").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("file-entry-cache").type(SoftwareType.NPM_PACKAGE).version("6.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("flat-cache").type(SoftwareType.NPM_PACKAGE).version("3.0.4").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("flatted").type(SoftwareType.NPM_PACKAGE).version("3.2.5").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("fs.realpath").type(SoftwareType.NPM_PACKAGE).version("1.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("functional-red-black-tree").type(SoftwareType.NPM_PACKAGE).version("1.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("glob").type(SoftwareType.NPM_PACKAGE).version("7.2.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("glob-parent").type(SoftwareType.NPM_PACKAGE).version("6.0.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("globals").type(SoftwareType.NPM_PACKAGE).version("13.12.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("has-flag").type(SoftwareType.NPM_PACKAGE).version("4.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("ignore").type(SoftwareType.NPM_PACKAGE).version("4.0.6").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("ignore").type(SoftwareType.NPM_PACKAGE).version("5.2.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("import-fresh").type(SoftwareType.NPM_PACKAGE).version("3.3.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("imurmurhash").type(SoftwareType.NPM_PACKAGE).version("0.1.4").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("inflight").type(SoftwareType.NPM_PACKAGE).version("1.0.6").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("inherits").type(SoftwareType.NPM_PACKAGE).version("2.0.4").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("is-descriptor").type(SoftwareType.NPM_PACKAGE).version("3.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.DIRECT).build(),
                Software.builder().scannerId("nodejs").name("is-extglob").type(SoftwareType.NPM_PACKAGE).version("2.1.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("is-glob").type(SoftwareType.NPM_PACKAGE).version("4.0.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("isexe").type(SoftwareType.NPM_PACKAGE).version("2.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("js-yaml").type(SoftwareType.NPM_PACKAGE).version("4.1.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("json-schema-traverse").type(SoftwareType.NPM_PACKAGE).version("0.4.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("json-stable-stringify-without-jsonify").type(SoftwareType.NPM_PACKAGE).version("1.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("levn").type(SoftwareType.NPM_PACKAGE).version("0.4.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("lodash.merge").type(SoftwareType.NPM_PACKAGE).version("4.6.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("minimatch").type(SoftwareType.NPM_PACKAGE).version("3.0.4").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("ms").type(SoftwareType.NPM_PACKAGE).version("2.1.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("natural-compare").type(SoftwareType.NPM_PACKAGE).version("1.4.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("once").type(SoftwareType.NPM_PACKAGE).version("1.4.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("optionator").type(SoftwareType.NPM_PACKAGE).version("0.9.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("parent-module").type(SoftwareType.NPM_PACKAGE).version("1.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("path-is-absolute").type(SoftwareType.NPM_PACKAGE).version("1.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("path-key").type(SoftwareType.NPM_PACKAGE).version("3.1.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("prelude-ls").type(SoftwareType.NPM_PACKAGE).version("1.2.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("punycode").type(SoftwareType.NPM_PACKAGE).version("2.1.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("regexpp").type(SoftwareType.NPM_PACKAGE).version("3.2.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("resolve-from").type(SoftwareType.NPM_PACKAGE).version("4.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("rimraf").type(SoftwareType.NPM_PACKAGE).version("3.0.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("shebang-command").type(SoftwareType.NPM_PACKAGE).version("2.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("shebang-regex").type(SoftwareType.NPM_PACKAGE).version("3.0.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("strip-ansi").type(SoftwareType.NPM_PACKAGE).version("6.0.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("strip-json-comments").type(SoftwareType.NPM_PACKAGE).version("3.1.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("supports-color").type(SoftwareType.NPM_PACKAGE).version("7.2.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("text-table").type(SoftwareType.NPM_PACKAGE).version("0.2.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("type-check").type(SoftwareType.NPM_PACKAGE).version("0.4.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("type-fest").type(SoftwareType.NPM_PACKAGE).version("0.20.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("uri-js").type(SoftwareType.NPM_PACKAGE).version("4.4.1").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("v8-compile-cache").type(SoftwareType.NPM_PACKAGE).version("2.3.0").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("which").type(SoftwareType.NPM_PACKAGE).version("2.0.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("word-wrap").type(SoftwareType.NPM_PACKAGE).version("1.2.3").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build(),
                Software.builder().scannerId("nodejs").name("wrappy").type(SoftwareType.NPM_PACKAGE).version("1.0.2").packaging("npm-package").dependencyType(SoftwareDependencyType.TRANSITIVE).scope(SoftwareScope.DEV).build()
        );
    }
}
