package io.cucumber.gherkin.utils.test;

import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.Envelope;
import io.cucumber.messages.types.GherkinDocument;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

class TestCase {

    private static final GherkinParser parser = GherkinParser.builder()
            .includePickles(false)
            .build();

    private final BiConsumer<GherkinDocument, OutputStream> formatter;
    private final String name;
    private final Path actualPath;
    private final Path expectedPath;


    TestCase(Path actualPath, Path expectedDirectory, BiConsumer<GherkinDocument, OutputStream> formatter) {
        this.name = getName(actualPath);
        this.formatter = formatter;
        this.actualPath = actualPath;
        this.expectedPath = expectedDirectory.resolve(getExpectedName(name));
    }

    TestCase(String testCaseName, Path actualPath, Path expectedDirectory, BiConsumer<GherkinDocument, OutputStream> formatter) {
        this.name = getName(actualPath);
        this.formatter = formatter;
        this.actualPath = actualPath;
        this.expectedPath = expectedDirectory.resolve(getExpectedName(name));
    }

    private  String getExpectedName(String name) {
        return name + ".feature";
    }

    private static String getName(Path actualPath) {
        String fileName = actualPath.getFileName().toString();
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    static List<Path> findFeatures(Path dir) throws IOException {
        List<Path> result;
        try (Stream<Path> paths = Files.list(dir)) {
            result = paths
                    .filter(path -> path.getFileName().toString().endsWith(".feature"))
                    .toList();
        }
        return result;
    }

    String format(GherkinDocument document) {
        var out = new ByteArrayOutputStream();
        formatter.accept(document, out);
        return out.toString(StandardCharsets.UTF_8);
    }

    void formatTo(GherkinDocument document, OutputStream out) {
        formatter.accept(document, out);
    }

    GherkinDocument actual() throws IOException {
        return parseGherkinDocument(actualPath);
    }

    String expected() throws IOException {
        return Files.readString(expectedPath);
    }

    Path expectedPath() {
        return expectedPath;
    }

    @Override
    public String toString() {
        return name;
    }

    private static GherkinDocument parseGherkinDocument(Path path) throws IOException {
        return parser.parse(path)
                .filter(envelope -> envelope.getGherkinDocument().isPresent())
                .findFirst()
                .flatMap(Envelope::getGherkinDocument)
                .orElseThrow();
    }
}
