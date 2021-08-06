package com.example;

import java.util.List;
import java.util.stream.Stream;

public class TwoImports {

    // Ensure import is used so it does not get removed by an IDE
    private List<String> ensureListtImportIsUsed;
    // Ensure import is used so it does not get removed by an IDE
    private Stream<String> ensureStreamImportIsUsed;
}