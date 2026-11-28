package io.cucumber.gherkin.utils.test;

import io.cucumber.gherkin.utils.GherkinDocumentWalker;
import io.cucumber.gherkin.utils.GherkinDocumentWalker.Handler;
import io.cucumber.messages.types.Background;
import io.cucumber.messages.types.Comment;
import io.cucumber.messages.types.DataTable;
import io.cucumber.messages.types.DocString;
import io.cucumber.messages.types.Examples;
import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.GherkinDocument;
import io.cucumber.messages.types.Rule;
import io.cucumber.messages.types.Scenario;
import io.cucumber.messages.types.Step;
import io.cucumber.messages.types.TableRow;
import io.cucumber.messages.types.Tag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import static io.cucumber.gherkin.utils.test.TestCase.findFeatures;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class GherkinDocumentWalkerAcceptanceTest {

    private static void formatTo(GherkinDocument document, OutputStream outputStream) {
        var walker = new GherkinDocumentWalker<>(new LoggingHandler());
        try (var out = new PrintWriter(new OutputStreamWriter(outputStream, UTF_8))) {
            walker.walkGherkinDocument(document, out::println);
        }

        // Run with noop formatter to cover the default methods of the handler
        var noopWalker = new GherkinDocumentWalker<>(o -> new Object());
        noopWalker.walkGherkinDocument(document, new Object());
    }

    @Nested
    class GherkinUtils {

        static List<TestCase> acceptance() throws IOException {
            var actualDirectory = Path.of("../testdata/gherkin-utils/src");
            var expectedDirectory = Path.of("../testdata/gherkin-utils/expected");
            return findFeatures(actualDirectory)
                    .stream()
                    .map(actual -> new TestCase( actual, expectedDirectory, GherkinDocumentWalkerAcceptanceTest::formatTo))
                    .toList();
        }


        @ParameterizedTest
        @MethodSource("acceptance")
        @DisabledIfEnvironmentVariable(named = "UPDATE_EXPECTED_FILES", matches = "true")
        void test(TestCase testCase) throws Exception {
            var formatted = testCase.format(testCase.actual());
            assertThat(formatted).isEqualToIgnoringNewLines(testCase.expected());
        }

        @ParameterizedTest
        @MethodSource("acceptance")
        @EnabledIfEnvironmentVariable(named = "UPDATE_EXPECTED_FILES", matches = "true")
        void updateExpectedFiles(TestCase testCase) throws Exception {
            try (OutputStream out = Files.newOutputStream(testCase.expectedPath())) {
                testCase.formatTo(testCase.actual(), out);
                Files.copy(testCase.expectedPath(), System.out);
            }
        }
    }


    @Nested
    class CompatibilityKit {

        static List<TestCase> acceptance() throws IOException {
            var actualDirectory = Path.of("../testdata/compatibility-kit/src");
            var expectedDirectory = Path.of("../testdata/compatibility-kit/expected");
            return findFeatures(actualDirectory)
                    .stream()
                    .map(actual -> new TestCase(actual, expectedDirectory, GherkinDocumentWalkerAcceptanceTest::formatTo))
                    .toList();
        }


        @ParameterizedTest
        @MethodSource("acceptance")
        @DisabledIfEnvironmentVariable(named = "UPDATE_EXPECTED_FILES", matches = "true")
        void test(TestCase testCase) throws Exception {
            var formatted = testCase.format(testCase.actual());
            assertThat(formatted).isEqualToIgnoringNewLines(testCase.expected());
        }

        @ParameterizedTest
        @MethodSource("acceptance")
        @EnabledIfEnvironmentVariable(named = "UPDATE_EXPECTED_FILES", matches = "true")
        void updateExpectedFiles(TestCase testCase) throws Exception {
            try (OutputStream out = Files.newOutputStream(testCase.expectedPath())) {
                testCase.formatTo(testCase.actual(), out);
                Files.copy(testCase.expectedPath(), System.out);
            }
        }
    }

    private final static class LoggingHandler implements Handler<LoggingHandler.Context, Consumer<String>> {

        @Override
        public Context createContext(Consumer<String> consumer) {
            return new Context(consumer);
        }

        @Override
        public void startGherkinDocument(Context context, GherkinDocument gherkinDocument) {
            context.log();
        }

        @Override
        public void finishGherkinDocument(Context context, GherkinDocument gherkinDocument) {
            context.log();
        }

        @Override
        public void handleGherkinDocument(Context context, GherkinDocument gherkinDocument) {
            context.log();
        }

        @Override
        public void startFeature(Context context, Feature feature) {
            context.log();
        }

        @Override
        public void handleFeature(Context context, Feature feature) {
            context.log();
        }

        @Override
        public void finishFeature(Context context, Feature feature) {
            context.log();
        }

        @Override
        public void startLanguage(Context context, String language) {
            context.log();
        }

        @Override
        public void handleLanguage(Context context, String language) {
            context.log();
        }

        @Override
        public void finishLanguage(Context context, String language) {
            context.log();
        }

        @Override
        public void startDescription(Context context, String description) {
            context.log();
        }

        @Override
        public void handleDescription(Context context, String description) {
            context.log();
        }

        @Override
        public void finishDescription(Context context, String description) {
            context.log();
        }

        @Override
        public void startRule(Context context, Rule rule) {
            context.log();
        }

        @Override
        public void handleRule(Context context, Rule rule) {
            context.log();
        }

        @Override
        public void finishRule(Context context, Rule rule) {
            context.log();
        }

        @Override
        public void startBackground(Context context, Background background) {
            context.log();
        }

        @Override
        public void handleBackground(Context context, Background background) {
            context.log();
        }

        @Override
        public void finishBackground(Context context, Background background) {
            context.log();
        }

        @Override
        public void startScenario(Context context, Scenario scenario) {
            context.log();
        }

        @Override
        public void handleScenario(Context context, Scenario scenario) {
            context.log();
        }

        @Override
        public void finishScenario(Context context, Scenario scenario) {
            context.log();
        }

        @Override
        public void startStep(Context context, Step step) {
            context.log();
        }

        @Override
        public void handleStep(Context context, Step step) {
            context.log();
        }

        @Override
        public void finishStep(Context context, Step step) {
            context.log();
        }

        @Override
        public void startExamples(Context context, Examples examples) {
            context.log();
        }

        @Override
        public void handleExamples(Context context, Examples examples) {
            context.log();
        }

        @Override
        public void finishExamples(Context context, Examples examples) {
            context.log();
        }

        @Override
        public void startExamplesTableRow(Context context, TableRow tableRow) {
            context.log();
        }

        @Override
        public void handleExamplesTableRow(Context context, TableRow tableRow) {
            context.log();
        }

        @Override
        public void finishExamplesTableRow(Context context, TableRow tableRow) {
            context.log();
        }

        @Override
        public void startDataTable(Context context, DataTable dataTable) {
            context.log();
        }

        @Override
        public void handleDataTable(Context context, DataTable dataTable) {
            context.log();
        }

        @Override
        public void finishDataTable(Context context, DataTable dataTable) {
            context.log();
        }

        @Override
        public void startDataTableRow(Context context, TableRow tableRow) {
            context.log();
        }

        @Override
        public void handleDataTableRow(Context context, TableRow tableRow) {
            context.log();
        }

        @Override
        public void finishDataTableRow(Context context, TableRow tableRow) {
            context.log();
        }

        @Override
        public void startDocString(Context context, DocString docString) {
            context.log();
        }

        @Override
        public void handleDocString(Context context, DocString docString) {
            context.log();
        }

        @Override
        public void finishDocString(Context context, DocString docString) {
            context.log();
        }

        @Override
        public void startTags(Context context, List<Tag> tags) {
            context.log();
        }

        @Override
        public void handleTags(Context context, List<Tag> tags) {
            context.log();
        }

        @Override
        public void finishTags(Context context, List<Tag> tags) {
            context.log();
        }

        @Override
        public void startDetachedComments(Context context, List<Comment> comments) {
            context.log();
        }

        @Override
        public void handleDetachedComments(Context context, List<Comment> comments) {
            context.log();
        }

        @Override
        public void finishDetachedComment(Context context, List<Comment> comments) {
            context.log();
        }

        @Override
        public void startComments(Context context, List<Comment> comments) {
            context.log();
        }

        @Override
        public void handleComments(Context context, List<Comment> comments) {
            context.log();
        }

        @Override
        public void finishComments(Context context, List<Comment> comments) {
            context.log();
        }

        static class Context {

            private final Consumer<String> consumer;
            private int depth = 0;

            Context(Consumer<String> consumer) {
                this.consumer = consumer;
            }

            void log() {
                var caller = new Exception().getStackTrace()[1].getMethodName();
                if (caller.startsWith("finish")) {
                    depth--;
                }

                consumer.accept(" ".repeat(depth) + caller);

                if (caller.startsWith("start")) {
                    depth++;
                }

            }
        }
    }
}
