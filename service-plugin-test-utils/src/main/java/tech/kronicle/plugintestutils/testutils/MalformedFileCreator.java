package tech.kronicle.plugintestutils.testutils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.nonNull;

public class MalformedFileCreator {

    public static final byte[] MALFORMED_UTF8_BYTES = { (byte) 0x80, (byte) 0x81 };

    public static void createMalformedFile(Path file) {
        createFile(file, true, null, null);
    }

    public static void createMalformedFile(Path file, String startContent, String endContent) {
        createFile(file, true, startContent, endContent);
    }

    public static void createRegularFile(Path file, String content) {
        createFile(file, false, content, null);
    }

    public static void createFile(Path file, boolean malformed) {
        createFile(file, malformed, null, null);
    }

    public static void createFile(Path file, boolean malformed, String startContent, String endContent) {
        try (OutputStream out = Files.newOutputStream(file)) {
            if (nonNull(startContent)) {
                write(out, startContent);
            }
            if (malformed) {
                out.write(MALFORMED_UTF8_BYTES);
            }
            if (nonNull(endContent)) {
                write(out, endContent);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(OutputStream out, String startContent) throws IOException {
        out.write(startContent.getBytes(StandardCharsets.UTF_8));
    }
}
