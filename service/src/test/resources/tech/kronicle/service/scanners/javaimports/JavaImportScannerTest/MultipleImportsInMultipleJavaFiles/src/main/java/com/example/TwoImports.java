package tech.kronicle.service.scanners.javaimports.JavaImportScannerTest.MultipleImportsInMultipleJavaFiles.src.main.java.com.example;

import java.util.Set;
import java.util.stream.Stream;

public class TwoImports {

    // Ensure import is used so it does not get removed by an IDE
    private Set<String> ensureSetImportIsUsed;
    // Ensure import is used so it does not get removed by an IDE
    private Stream<String> ensureStreamImportIsUsed;
}