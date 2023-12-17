package foundation;

import foundation.protocols.StringProcessor;

import java.util.List;
import java.util.Objects;

public class Tester {
    public static void test(
            String inputFolderPath,
            String testOutputFolderPath,
            String standardOutputFolderPath,
            String differencesOutputFolderPath,
            List<Pair<String, String>> inputOutputFilenameList,
            StringProcessor stringProcessor
    ) {
        Objects.requireNonNull(inputFolderPath);
        Objects.requireNonNull(testOutputFolderPath);
        Objects.requireNonNull(standardOutputFolderPath);
        Objects.requireNonNull(differencesOutputFolderPath);
        Objects.requireNonNull(inputOutputFilenameList);
        Objects.requireNonNull(stringProcessor);

        for (final var inputOutputFilename : inputOutputFilenameList) {
            final var inputFileName = inputOutputFilename.first();
            final var outputFileName = inputOutputFilename.second();
            final var sourceCode = IO.simpleInputFromFolder(inputFolderPath, inputFileName);
            final var testOutput = stringProcessor.process(sourceCode);
            final var standardOutput = IO.simpleInputFromFolder(standardOutputFolderPath, outputFileName);
            final var differentLines = StringUtils.findDifferingLines(testOutput, standardOutput);
            final var differencesLog = String.join("\n", differentLines);

            IO.simpleOutputToFolder(testOutputFolderPath, outputFileName, testOutput);
            IO.simpleOutputToFolder(differencesOutputFolderPath, outputFileName, differencesLog);
        }
    }
}
