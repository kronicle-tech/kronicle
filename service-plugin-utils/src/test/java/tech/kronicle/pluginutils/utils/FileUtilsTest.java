package tech.kronicle.pluginutils.utils;

import com.google.common.io.CharStreams;
import tech.kronicle.pluginutils.utils.AntStyleIgnoreFileLoader;
import tech.kronicle.pluginutils.utils.FileUtils;
import tech.kronicle.pluginutils.utils.ObjectReference;
import tech.kronicle.plugintestutils.BaseTest;
import tech.kronicle.plugintestutils.testutils.MalformedFileCreator;
import lombok.Value;
import lombok.With;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class FileUtilsTest extends BaseTest {

    private static final BiPredicate<Path, BasicFileAttributes> ALWAYS_TRUE_MATCHER = (ignore1, ignore2) -> true;
    private static final BiPredicate<Path, BasicFileAttributes> FILE_1_MATCHER = (path, ignored) -> Objects.equals(path.getFileName().toString(), "file1.txt");
    public static final FilesScenario FILES_SCENARIO_WITH_NOTHING = new FilesScenario(Optional.empty(), Optional.empty(), null, null);
    public static final FilesScenario FILES_SCENARIO_WITH_MAX_DEPTH = new FilesScenario(Optional.of(2), Optional.empty(), null, null);
    public static final FilesScenario FILES_SCENARIO_WITH_MATCHER = new FilesScenario(Optional.empty(), Optional.of(ALWAYS_TRUE_MATCHER), null, null);
    public static final FilesScenario FILES_SCENARIO_WITH_MAX_DEPTH_AND_MATCHER = new FilesScenario(Optional.of(2), Optional.of(ALWAYS_TRUE_MATCHER), null, null);

    private FileUtils underTest = new FileUtils(new AntStyleIgnoreFileLoader());
    @TempDir
    public Path tempDir;

    @Test
    public void readFileContentsShouldReadFileContents() throws IOException {
        // Given
        Path file = tempDir.resolve("file.txt");
        Files.writeString(file, "test");

        // When
        String returnValue = underTest.readFileContent(file);

        // Then
        assertThat(returnValue).isEqualTo("test");
    }

    @Test
    public void readFileContentsShouldWrapIOException() {
        // Given
        Path file = tempDir.resolve("file.txt");

        // When
        Throwable thrown = catchThrowable(() -> underTest.readFileContent(file));

        // Then
        assertThat(thrown).isInstanceOf(RuntimeException.class);
        assertThat(thrown).hasCauseInstanceOf(IOException.class);
    }

    @Test
    public void readFileContentsShouldReturnNullWhenFileContainsInvalidUtf8Bytes() {
        // Given
        Path file = tempDir.resolve("file.txt");
        MalformedFileCreator.createMalformedFile(file);

        // When
        String returnValue = underTest.readFileContent(file);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void openFileShouldOpenAnInputStream() throws IOException {
        // Given
        Path file = tempDir.resolve("file.txt");
        Files.writeString(file, "test");

        // When
        InputStream inputStream = underTest.openFile(file);

        // Then
        assertThat(readString(inputStream)).isEqualTo("test");
    }

    @Test
    public void openFileContentsShouldWrapIOException() {
        // Given
        Path file = tempDir.resolve("file.txt");

        // When
        Throwable thrown = catchThrowable(() -> underTest.openFile(file));

        // Then
        assertThat(thrown).isInstanceOf(RuntimeException.class);
        assertThat(thrown).hasCauseInstanceOf(IOException.class);
    }

    @Test
    public void writeFileContentsShouldWriteFileContents() throws IOException {
        // Given
        Path file = tempDir.resolve("file.txt");

        // When
        underTest.writeFileContent(file, "test");

        // Then
        assertThat(Files.readString(file)).isEqualTo("test");
    }

    @Test
    public void writeFileContentsShouldWrapIOException() {
        // When
        Throwable thrown = catchThrowable(() -> underTest.writeFileContent(tempDir, ""));

        // Then
        assertThat(thrown).isInstanceOf(RuntimeException.class);
        assertThat(thrown).hasCauseInstanceOf(IOException.class);
    }

    @Test
    public void loadPropertiesShouldLoadAPropertiesFile() throws IOException {
        // Given
        Path file = tempDir.resolve("test.properties");
        Files.writeString(file, "key1=value1\nkey2=value2");

        // When
        Properties returnValue = underTest.loadProperties(file);

        // Then
        assertThat(returnValue).hasSize(2);
        assertThat(returnValue).containsEntry("key1", "value1");
        assertThat(returnValue).containsEntry("key2", "value2");
    }

    @Test
    public void loadPropertiesShouldWrapIOException() {
        // Given
        Path file = tempDir.resolve("file.txt");

        // When
        Throwable thrown = catchThrowable(() -> underTest.loadProperties(file));

        // Then
        assertThat(thrown).isInstanceOf(RuntimeException.class);
        assertThat(thrown).hasCauseInstanceOf(IOException.class);
    }

    @ParameterizedTest
    @MethodSource("provideFilesScenarios")
    public void findFilesShouldFindFiles(FilesScenario filesScenario) throws IOException {
        // Given
        Path file1 = tempDir.resolve("file1.txt");
        Files.writeString(file1, "test1");
        Path file2 = tempDir.resolve("file2.txt");
        Files.writeString(file2, "test2");

        // When
        List<Path> returnValue = filesScenario.findFilesInvoker.apply(underTest, tempDir).collect(Collectors.toList());

        // Then
        assertThat(returnValue).containsExactlyInAnyOrder(file1, file2);
    }

    @ParameterizedTest
    @MethodSource("provideFilesScenariosWithMatcher")
    public void findFilesWithMatchShouldFindFilesThatMatch(FilesScenario filesScenario) throws IOException {
        // Given
        Path file1 = tempDir.resolve("file1.txt");
        Files.writeString(file1, "test1");
        Path file2 = tempDir.resolve("file2.txt");
        Files.writeString(file2, "test2");

        // When
        List<Path> returnValue;
        if (filesScenario.getMaxDepth().isEmpty()) {
            returnValue = underTest.findFiles(tempDir, FILE_1_MATCHER).collect(Collectors.toList());
        } else {
            returnValue = underTest.findFiles(tempDir, filesScenario.getMaxDepth().get(), FILE_1_MATCHER).collect(Collectors.toList());
        }

        // Then
        assertThat(returnValue).containsExactlyInAnyOrder(file1);
    }

    @ParameterizedTest
    @MethodSource("provideFilesScenariosWithoutMaxDepth")
    public void findFilesShouldFindFilesInSubdirectories(FilesScenario filesScenario) {
        // Given
        List<FileUtils.FileContent> fileContents = createFilesInSubdirectories();

        // When
        List<Path> returnValue = filesScenario.findFilesInvoker.apply(underTest, tempDir).collect(Collectors.toList());

        // Then
        assertThat(returnValue).containsExactlyInAnyOrder(convertFileContentListToPathArray(fileContents));
    }

    @ParameterizedTest
    @MethodSource("provideFilesScenariosWithMaxDepth")
    public void findFilesShouldFindFilesInSubdirectoriesUpToMaxDepth(FilesScenario filesScenario) {
        // Given
        createFilesInSubdirectories();

        // When
        List<Path> returnValue = filesScenario.findFilesInvoker.apply(underTest, tempDir).collect(Collectors.toList());

        // Then
        assertThat(returnValue).containsExactlyInAnyOrder(
                tempDir.resolve("file1"),
                tempDir.resolve("subdirectory1/file2"));
    }

    @ParameterizedTest
    @MethodSource("provideFilesScenariosWithoutMaxDepth")
    public void findFilesShouldReturnAllNonGitFiles(FilesScenario filesScenario) throws IOException {
        // Given
        List<FileUtils.FileContent> fileContents = createFilesInSubdirectories();
        CreateGitFiles createGitFiles = new CreateGitFiles(tempDir).invoke();

        // When
        List<Path> returnValue = filesScenario.findFilesInvoker.apply(underTest, tempDir).collect(Collectors.toList());

        // Then
        assertThat(returnValue).containsExactlyInAnyOrder(convertFileContentListToPathArray(fileContents));
        assertThat(returnValue).doesNotContain(createGitFiles.getIgnoredFile1(), createGitFiles.getIgnoredFile2(), createGitFiles.getIgnoredFile3());
    }

    @ParameterizedTest
    @MethodSource("provideFilesScenariosWithMaxDepth")
    public void findFilesWithMaxDepthShouldReturnAllNonGitFilesUpToMaxDepth(FilesScenario filesScenario) throws IOException {
        // Given
        List<FileUtils.FileContent> fileContents = createFilesInSubdirectories();
        CreateGitFiles createGitFiles = new CreateGitFiles(tempDir).invoke();

        // When
        List<Path> returnValue = filesScenario.findFilesInvoker.apply(underTest, tempDir).collect(Collectors.toList());

        // Then
        fileContents.removeIf(fileContent -> fileContent.getFile().toString().contains("/subdirectory2/"));
        assertThat(returnValue).containsExactlyInAnyOrder(convertFileContentListToPathArray(fileContents));
        assertThat(returnValue).doesNotContain(createGitFiles.getIgnoredFile1(), createGitFiles.getIgnoredFile2(), createGitFiles.getIgnoredFile3());
    }

    @ParameterizedTest
    @MethodSource("provideFilesScenariosWithoutMaxDepth")
    public void findFilesShouldIgnoreFilePathsInKronicleignoreFile(FilesScenario filesScenario) throws IOException {
        // Given
        Path file1 = tempDir.resolve("file1.txt");
        Files.writeString(file1, "test1");
        Path file2 = tempDir.resolve("file2.txt");
        Files.writeString(file2, "test2");
        Path file3 = tempDir.resolve("file3.txt");
        Files.writeString(file3, "test3");
        Path kronicleignoreFile = tempDir.resolve(".kronicleignore");
        Files.writeString(kronicleignoreFile, "file2.txt");

        // When
        List<Path> returnValue = filesScenario.findFilesInvoker.apply(underTest, tempDir).collect(Collectors.toList());

        // Then
        assertThat(returnValue).containsExactlyInAnyOrder(file1, file3, kronicleignoreFile);
    }

    @ParameterizedTest
    @MethodSource("provideFilesScenarios")
    public void findFilesShouldWrapIOException(FilesScenario filesScenario) {
        // Given
        Path file = Path.of("file.txt");

        // When
        Throwable thrown = catchThrowable(() -> filesScenario.getFindFilesInvoker().apply(underTest, file));

        // Then
        assertThat(thrown).isInstanceOf(RuntimeException.class);
        assertThat(thrown).hasCauseInstanceOf(IOException.class);
    }

    @ParameterizedTest
    @MethodSource("provideFilesScenarios")
    public void findFileContentsShouldFindFileContents(FilesScenario filesScenario) throws IOException {
        // Given
        Path file1 = tempDir.resolve("file1.txt");
        Files.writeString(file1, "test1");
        Path file2 = tempDir.resolve("file2.txt");
        Files.writeString(file2, "test2");

        // When
        List<FileUtils.FileContent> returnValue = filesScenario.findFileContentsInvoker.apply(underTest, tempDir).collect(Collectors.toList());

        // Then
        assertThat(returnValue).containsExactlyInAnyOrder(new FileUtils.FileContent(file1, "test1"), new FileUtils.FileContent(file2, "test2"));
    }

    @ParameterizedTest
    @MethodSource("provideFilesScenariosWithMatcher")
    public void findFileContentsWithMatchShouldFindFileContentsThatMatch(FilesScenario filesScenario) throws IOException {
        // Given
        Path file1 = tempDir.resolve("file1.txt");
        Files.writeString(file1, "test1");
        Path file2 = tempDir.resolve("file2.txt");
        Files.writeString(file2, "test2");

        // When
        List<FileUtils.FileContent> returnValue;
        if (filesScenario.getMaxDepth().isEmpty()) {
            returnValue = underTest.findFileContents(tempDir, FILE_1_MATCHER).collect(Collectors.toList());
        } else {
            returnValue = underTest.findFileContents(tempDir, filesScenario.getMaxDepth().get(), FILE_1_MATCHER).collect(Collectors.toList());
        }

        // Then
        assertThat(returnValue).containsExactlyInAnyOrder(new FileUtils.FileContent(file1, "test1"));
    }

    @ParameterizedTest
    @MethodSource("provideFilesScenariosWithoutMaxDepth")
    public void findFileContentsShouldFindFileContentsInSubdirectories(FilesScenario filesScenario) {
        // Given
        List<FileUtils.FileContent> fileContents = createFilesInSubdirectories();

        // When
        List<FileUtils.FileContent> returnValue = filesScenario.findFileContentsInvoker.apply(underTest, tempDir).collect(Collectors.toList());

        // Then
        assertThat(returnValue).containsExactlyInAnyOrder(convertFileContentListToFileContentArray(fileContents));
    }

    @ParameterizedTest
    @MethodSource("provideFilesScenariosWithMaxDepth")
    public void findFileContentsShouldFindFileContentsInSubdirectoriesUpToMaxDepth(FilesScenario filesScenario) {
        // Given
        List<FileUtils.FileContent> fileContents = createFilesInSubdirectories();

        // When
        List<FileUtils.FileContent> returnValue = filesScenario.findFileContentsInvoker.apply(underTest, tempDir).collect(Collectors.toList());

        // Then
        fileContents.removeIf(fileContent -> fileContent.getFile().toString().contains("/subdirectory2/"));
        assertThat(returnValue).containsExactlyInAnyOrder(convertFileContentListToFileContentArray(fileContents));
    }

    @ParameterizedTest
    @MethodSource("provideFilesScenariosWithoutMaxDepth")
    public void fileFileContentsShouldReturnAllNonGitFiles(FilesScenario filesScenario) throws IOException {
        // Given
        List<FileUtils.FileContent> fileContents = createFilesInSubdirectories();
        CreateGitFiles createGitFiles = new CreateGitFiles(tempDir).invoke();

        // When
        List<FileUtils.FileContent> returnValue = filesScenario.findFileContentsInvoker.apply(underTest, tempDir).collect(Collectors.toList());

        // Then
        assertThat(returnValue).containsExactlyInAnyOrder(convertFileContentListToFileContentArray(fileContents));
        assertThat(convertFileContentListToPathArray(returnValue)).doesNotContain(createGitFiles.getIgnoredFile1(), createGitFiles.getIgnoredFile2(), createGitFiles.getIgnoredFile3());
    }

    @ParameterizedTest
    @MethodSource("provideFilesScenariosWithMaxDepth")
    public void fileFileContendsWithMaxDepthShouldReturnAllNonGitFilesUpToMaxDepth(FilesScenario filesScenario) throws IOException {
        // Given
        List<FileUtils.FileContent> fileContents = createFilesInSubdirectories();
        CreateGitFiles createGitFiles = new CreateGitFiles(tempDir).invoke();

        // When
        List<FileUtils.FileContent> returnValue = filesScenario.findFileContentsInvoker.apply(underTest, tempDir).collect(Collectors.toList());

        // Then
        fileContents.removeIf(fileContent -> fileContent.getFile().toString().contains("/subdirectory2/"));
        assertThat(returnValue).containsExactlyInAnyOrder(convertFileContentListToFileContentArray(fileContents));
        assertThat(convertFileContentListToPathArray(returnValue)).doesNotContain(createGitFiles.getIgnoredFile1(), createGitFiles.getIgnoredFile2(), createGitFiles.getIgnoredFile3());
    }

    @ParameterizedTest
    @MethodSource("provideFilesScenariosWithoutMaxDepth")
    public void findFileContentsShouldIgnoreFilePathsInKronicleignoreFile(FilesScenario filesScenario) throws IOException {
        // Given
        Path file1 = tempDir.resolve("file1.txt");
        Files.writeString(file1, "test1");
        Path file2 = tempDir.resolve("file2.txt");
        Files.writeString(file2, "test2");
        Path file3 = tempDir.resolve("file3.txt");
        Files.writeString(file3, "test3");
        Path kronicleignoreFile = tempDir.resolve(".kronicleignore");
        Files.writeString(kronicleignoreFile, "file2.txt");

        // When
        List<FileUtils.FileContent> returnValue = filesScenario.findFileContentsInvoker.apply(underTest, tempDir).collect(Collectors.toList());

        // Then
        assertThat(returnValue).containsExactlyInAnyOrder(
                new FileUtils.FileContent(file1, "test1"),
                new FileUtils.FileContent(file3, "test3"),
                new FileUtils.FileContent(kronicleignoreFile, "file2.txt"));
    }

    @ParameterizedTest
    @MethodSource("provideFilesScenarios")
    public void findFileContentsShouldWrapIOException(FilesScenario filesScenario) {
        // Given
        Path file = Path.of("file.txt");

        // When
        Throwable thrown = catchThrowable(() -> filesScenario.getFindFileContentsInvoker().apply(underTest, file));

        // Then
        assertThat(thrown).isInstanceOf(RuntimeException.class);
        assertThat(thrown).hasCauseInstanceOf(IOException.class);
    }

    @Test
    public void findFileContentsShouldReturnNullWhenFileContainsInvalidUtf8Bytes() {
        // Given
        Path file1 = tempDir.resolve("malformed_file.txt");
        MalformedFileCreator.createMalformedFile(file1);
        Path file2 = tempDir.resolve("regular_file.txt");
        MalformedFileCreator.createRegularFile(file2, "Regular text");

        // When
        List<FileUtils.FileContent> returnValue = underTest.findFileContents(tempDir).collect(Collectors.toList());

        // Then
        assertThat(returnValue).containsExactlyInAnyOrder(new FileUtils.FileContent(file2, "Regular text"));
    }

    public static Stream<FilesScenario> provideFilesScenariosWithMaxDepth() {
        return addInvokersToFilesScenarios(Stream.of(
                FILES_SCENARIO_WITH_MAX_DEPTH,
                FILES_SCENARIO_WITH_MAX_DEPTH_AND_MATCHER));
    }

    public static Stream<FilesScenario> provideFilesScenariosWithoutMaxDepth() {
        return addInvokersToFilesScenarios(Stream.of(
                FILES_SCENARIO_WITH_NOTHING,
                FILES_SCENARIO_WITH_MATCHER));
    }

    public static Stream<FilesScenario> provideFilesScenariosWithMatcher() {
        return addInvokersToFilesScenarios(Stream.of(
                FILES_SCENARIO_WITH_MATCHER,
                FILES_SCENARIO_WITH_MAX_DEPTH_AND_MATCHER));
    }

    public static Stream<FilesScenario> provideFilesScenarios() {
        return addInvokersToFilesScenarios(Stream.of(
                FILES_SCENARIO_WITH_NOTHING,
                FILES_SCENARIO_WITH_MAX_DEPTH,
                FILES_SCENARIO_WITH_MATCHER,
                FILES_SCENARIO_WITH_MAX_DEPTH_AND_MATCHER));
    }

    private static Stream<FilesScenario> addInvokersToFilesScenarios(Stream<FilesScenario> filesScenarios) {
        return filesScenarios.map(filesScenario -> {
            if (filesScenario.getMaxDepth().isEmpty() && filesScenario.getMatcher().isEmpty()) {
                return filesScenario.withFindFilesInvoker(FileUtils::findFiles)
                        .withFindFileContentsInvoker(FileUtils::findFileContents);
            } else if (filesScenario.getMaxDepth().isPresent()) {
                return filesScenario.withFindFilesInvoker((FileUtils underTest, Path start) -> underTest.findFiles(start, filesScenario.getMaxDepth().get()))
                        .withFindFileContentsInvoker((FileUtils underTest, Path start) -> underTest.findFileContents(start, filesScenario.getMaxDepth().get()));
            } else if (filesScenario.getMatcher().isPresent()) {
                return filesScenario.withFindFilesInvoker((FileUtils underTest, Path start) -> underTest.findFiles(start, filesScenario.getMatcher().get()))
                        .withFindFileContentsInvoker((FileUtils underTest, Path start) -> underTest.findFileContents(start, filesScenario.getMatcher().get()));
            } else {
                return filesScenario.withFindFilesInvoker((FileUtils underTest, Path start) -> underTest.findFiles(start, filesScenario.getMaxDepth().get(), filesScenario.getMatcher().get()))
                        .withFindFileContentsInvoker((FileUtils underTest, Path start) -> underTest.findFileContents(start, filesScenario.getMaxDepth().get(), filesScenario.getMatcher().get()));
            }
        });
    }

    private String readString(InputStream inputStream) throws IOException {
        String content;
        try (Reader reader = new InputStreamReader(inputStream)) {
            content = CharStreams.toString(reader);
        }
        return content;
    }

    private List<FileUtils.FileContent> createFilesInSubdirectories() {
        return IntStream.range(0, 10).mapToObj(number -> {
                    ObjectReference<Path> dir = new ObjectReference<>(tempDir);
                    IntStream.range(0, number).forEachOrdered(number2 -> dir.set(dir.get().resolve("subdirectory" + (number2 + 1))));
                    Path file = dir.get().resolve("file" + (number + 1));
                    String content = file.getFileName().toString();
                    try {
                        Files.createDirectories(dir.get());
                        Files.writeString(file, content);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return new FileUtils.FileContent(file, content);
                })
                .collect(Collectors.toList());
    }

    private Path[] convertFileContentListToPathArray(List<FileUtils.FileContent> list) {
        return list.stream()
                .map(FileUtils.FileContent::getFile)
                .toArray(Path[]::new);
    }

    private FileUtils.FileContent[] convertFileContentListToFileContentArray(List<FileUtils.FileContent> list) {
        return list.toArray(new FileUtils.FileContent[0]);
    }

    @Value
    @With
    private static class FilesScenario {

        Optional<Integer> maxDepth;
        Optional<BiPredicate<Path, BasicFileAttributes>> matcher;
        BiFunction<FileUtils, Path, Stream<Path>> findFilesInvoker;
        BiFunction<FileUtils, Path, Stream<FileUtils.FileContent>> findFileContentsInvoker;
    }

    private class CreateGitFiles {

        private final Path codebaseDir;
        private Path ignoredFile1;
        private Path ignoredFile2;
        private Path ignoredFile3;

        public CreateGitFiles(Path codebaseDir) {
            this.codebaseDir = codebaseDir;
        }

        public Path getIgnoredFile1() {
            return ignoredFile1;
        }

        public Path getIgnoredFile2() {
            return ignoredFile2;
        }

        public Path getIgnoredFile3() {
            return ignoredFile3;
        }

        public CreateGitFiles invoke() throws IOException {
            Path gitDir = codebaseDir.resolve(".git");
            ignoredFile1 = gitDir.resolve("ignored_file1.txt");
            Path ignoredSubdirectory1 = gitDir.resolve("ignored_subdirectory1");
            ignoredFile2 = ignoredSubdirectory1.resolve("ignored_file2.txt");
            Path ignoredSubdirectory2 = ignoredSubdirectory1.resolve("ignored_subdirectory2");
            ignoredFile3 = ignoredSubdirectory2.resolve("ignored_file3.txt");
            FileSystemUtils.deleteRecursively(gitDir);
            Files.createDirectories(ignoredSubdirectory2);
            Files.createFile(ignoredFile1);
            Files.createFile(ignoredFile2);
            Files.createFile(ignoredFile3);
            return this;
        }
    }
}
