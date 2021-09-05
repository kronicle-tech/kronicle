package tech.kronicle.service.scanners.linesofcode.services;

import com.google.common.base.Ascii;
import lombok.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class LinesOfCodeCounter {

    public LinesOfCodeCountResult countLinesOfCode(InputStream contentIn) {
        byte[] chunk = new byte[1024];
        int readCount;
        try {
            readCount = contentIn.read(chunk);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int lineCount = 1;
        int asciiCount = 0;
        int otherCount = 0;

        while (readCount != -1) {
            for (int index = 0; index < readCount; index++) {
                byte value = chunk[index];

                if (value < Ascii.HT) {
                    return new LinesOfCodeCountResult(FileType.BINARY, 0);
                }

                if (value == Ascii.LF) {
                    lineCount++;
                    asciiCount++;
                } else if (value == Ascii.HT || value == Ascii.FF || value == Ascii.CR) {
                    asciiCount++;
                } else if (value > Ascii.US && value < Ascii.DEL) {
                    asciiCount++;
                } else {
                    otherCount++;
                }
            }

            try {
                readCount = contentIn.read(chunk);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (otherCount == 0) {
            return new LinesOfCodeCountResult(FileType.TEXT, lineCount);
        }

        if (100 * otherCount / (asciiCount + otherCount) > 95) {
            return new LinesOfCodeCountResult(FileType.BINARY, 0);
        }

        return new LinesOfCodeCountResult(FileType.TEXT, lineCount);
    }

    @Value
    public static class LinesOfCodeCountResult {

        FileType fileType;
        Integer linesOfCodeCount;
    }

    public enum FileType {

        TEXT,
        BINARY
    }

}
