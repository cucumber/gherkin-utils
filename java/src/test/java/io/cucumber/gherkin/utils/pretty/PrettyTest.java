package io.cucumber.gherkin.utils.pretty;

import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.Envelope;
import io.cucumber.messages.types.GherkinDocument;
import io.cucumber.messages.types.Source;
import io.cucumber.messages.types.SourceMediaType;
import org.junit.jupiter.api.Test;

import static io.cucumber.gherkin.utils.pretty.Pretty.prettyPrint;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrettyTest {
    @Test
    public void emptyFile() {
        String gherkin = "";
        GherkinDocument gherkinDocument = parse(gherkin);
        assertEquals("", prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void scenarioOutlineWithExamples() {
        GherkinDocument gherkinDocument = parse("    Feature: Overdue tasks\n" +
                "  Let users know when tasks are overdue, even when using other\n" +
                "  features of the app\n\n\n" +
                "\n\n\n\n" +
                "      Scenario Outline: eating\n" +
                "  Given there are <start> cucumbers\n" +
                "          When I eat <eat> cucumbers\n" +
                " Then I should have <left> cucumbers\n" +
                "\n" +
                "     Examples: \n" +
                "    | start | eat |left|\n" +
                "      |12 |    5 |             7 |\n" +
                "   |20|   5 |                             15 |\n");
        assertEquals("Feature: Overdue tasks\n" +
                        "  Let users know when tasks are overdue, even when using other\n" +
                        "  features of the app\n" +
                        "\n" +
                        "  Scenario Outline: eating\n" +
                        "    Given there are <start> cucumbers\n" +
                        "    When I eat <eat> cucumbers\n" +
                        "    Then I should have <left> cucumbers\n" +
                        "\n" +
                        "    Examples:\n" +
                        "      | start | eat | left |\n" +
                        "      | 12    | 5   | 7    |\n" +
                        "      | 20    | 5   | 15   |\n",
                prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void languageHeaderIsNotEn() {
        GherkinDocument gherkinDocument = parse("# language: no\n" +
                "Egenskap: hallo");
        assertEquals("# language: no\n" +
                "Egenskap: hallo\n", prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void emptyScenarios() {
        GherkinDocument gherkinDocument = parse("Feature: hello\n" +
                "\n" +
                "  Scenario: one\n" +
                "\n" +
                "  Scenario: Two");
        assertEquals("Feature: hello\n" +
                "\n" +
                "  Scenario: one\n" +
                "\n" +
                "  Scenario: Two\n", prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void twoScenarios() {
        GherkinDocument gherkinDocument = parse("Feature: hello\n" +
                "\n" +
                "  Scenario: one\n" +
                "    Given hello\n" +
                "\n" +
                "  Scenario: two\n" +
                "    Given world");
        assertEquals("Feature: hello\n" +
                "\n" +
                "  Scenario: one\n" +
                "    Given hello\n" +
                "\n" +
                "  Scenario: two\n" +
                "    Given world\n", prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void twoScenariosInRule() {
        GherkinDocument gherkinDocument = parse("Feature: hello\n" +
                "\n" +
                "  Rule: ok\n" +
                "\n" +
                "    Scenario: one\n" +
                "      Given hello\n" +
                "\n" +
                "    Scenario: two\n" +
                "      Given world\n");
        assertEquals("Feature: hello\n" +
                "\n" +
                "  Rule: ok\n" +
                "\n" +
                "    Scenario: one\n" +
                "      Given hello\n" +
                "\n" +
                "    Scenario: two\n" +
                "      Given world\n", prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void featureWithBackgroundAndScenario() {
        GherkinDocument gherkinDocument = parse("Feature: hello\n" +
                "\n" +
                "  Background: bbb\n" +
                "    Given hello\n" +
                "\n" +
                "  Scenario: two\n" +
                "    Given world");
        assertEquals("Feature: hello\n" +
                "\n" +
                "  Background: bbb\n" +
                "    Given hello\n" +
                "\n" +
                "  Scenario: two\n" +
                "    Given world\n", prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void ruleWithBackgroundAndScenario() {
        GherkinDocument gherkinDocument = parse("Feature: hello\n" +
                "\n" +
                "  Rule: machin\n" +
                "\n" +
                "    Background: bbb\n" +
                "      Given hello\n" +
                "\n" +
                "    Scenario: two\n" +
                "      Given world\n");
        assertEquals("Feature: hello\n" +
                "\n" +
                "  Rule: machin\n" +
                "\n" +
                "    Background: bbb\n" +
                "      Given hello\n" +
                "\n" +
                "    Scenario: two\n" +
                "      Given world\n", prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void tags() {
        GherkinDocument gherkinDocument = parse("@featureTag\n" +
                "Feature: hello\n" +
                "\n" +
                "  Rule: machin\n" +
                "\n" +
                "    Background: bbb\n" +
                "      Given hello\n" +
                "\n" +
                "    @scenarioTag @secondTag\n" +
                "    Scenario: two\n" +
                "      Given world\n");
        assertEquals("@featureTag\n" +
                "Feature: hello\n" +
                "\n" +
                "  Rule: machin\n" +
                "\n" +
                "    Background: bbb\n" +
                "      Given hello\n" +
                "\n" +
                "    @scenarioTag @secondTag\n" +
                "    Scenario: two\n" +
                "      Given world\n", prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void exampleTables() {
        GherkinDocument gherkinDocument = parse("Feature: hello\n" +
                "\n" +
                "  # i am tag hear me roar\n" +
                "  Scenario: one\n" +
                "    #some comment in the scenario\n" +
                "    #another line in the comment in the scenario\n" +
                "    Given a a <text> and a <number>\n" +
                "\n" +
                "    # comment1 is here   \n  " +
                "    Examples: some data\n" +
                " # comment2 is here     \n" +
                "      | text | number |\n" +
                " # comment3 is here     \n" +
                " # comment4 is here     \n" +
                "      | a    |      1 |\n" +
                "      | ab   |     10 |\n" +
                " # comment5 is here     \n" +
                " # comment6 is here     \n" +
                "      | abc  |    100 |\n");
        assertEquals("Feature: hello\n" +
                "\n" +
                "  # i am tag hear me roar\n" +
                "  Scenario: one\n" +
                "    # some comment in the scenario\n" +
                "    # another line in the comment in the scenario\n" +
                "    Given a a <text> and a <number>\n" +
                "\n" +
                "    # comment1 is here\n" +
                "    Examples: some data\n" +
                "      # comment2 is here\n" +
                "      | text | number |\n" +
                "      # comment3 is here\n" +
                "      # comment4 is here\n" +
                "      | a    | 1      |\n" +
                "      | ab   | 10     |\n" +
                "      # comment5 is here\n" +
                "      # comment6 is here\n" +
                "      | abc  | 100    |\n", prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void dataTables() {
        GherkinDocument gherkinDocument = parse("Feature: hello\n" +
                "\n" +
                "  Scenario: one\n" +
                "    Given a data table:\n" +
                "      | text | numbers |\n" +
                "      | a    |       1 |\n" +
                "      | ab   |      10 |\n" +
                "      | abc  |     100 |\n");
        assertEquals("Feature: hello\n" +
                "\n" +
                "  Scenario: one\n" +
                "    Given a data table:\n" +
                "      | text | numbers |\n" +
                "      | a    | 1       |\n" +
                "      | ab   | 10      |\n" +
                "      | abc  | 100     |\n", prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void cjkTables() {
        GherkinDocument gherkinDocument = parse("Feature: hello\n" +
                "\n" +
                "  Scenario: one\n" +
                "    Given a data table:\n" +
                "      |路| numbers |\n" +
                "      |路|       1 |\n" +
                "      |路步|      10 |\n" +
                "      |路步路|     100 |\n");
        assertEquals("Feature: hello\n" +
                "\n" +
                "  Scenario: one\n" +
                "    Given a data table:\n" +
                "      | 路     | numbers |\n" +
                "      | 路     | 1       |\n" +
                "      | 路步   | 10      |\n" +
                "      | 路步路 | 100     |\n", prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void docString() {
        GherkinDocument gherkinDocument = parse("Feature: hello\n" +
                "\n" +
                "  Scenario: one\n" +
                "    Given a data table:\n" +
                "      | text | numbers |\n" +
                "      | a    |       1 |\n" +
                "      | ab   |      10 |\n" +
                "      | abc  |     100 |\n");
        assertEquals("Feature: hello\n" +
                "\n" +
                "  Scenario: one\n" +
                "    Given a data table:\n" +
                "      | text | numbers |\n" +
                "      | a    | 1       |\n" +
                "      | ab   | 10      |\n" +
                "      | abc  | 100     |\n", prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void description() {
        GherkinDocument gherkinDocument = parse("Feature: hello\n" +
                "  So this is a feature\n" +
                "\n" +
                "  Rule: machin\n" +
                "    The first rule of the feature states things\n" +
                "\n" +
                "    Background: bbb\n" +
                "      We can have some explications for the background\n" +
                "\n" +
                "      Given hello\n" +
                "\n" +
                "    Scenario: two\n" +
                "      This scenario will do things, maybe\n" +
                "\n" +
                "      Given world\n");
        assertEquals("Feature: hello\n" +
                "  So this is a feature\n" +
                "\n" +
                "  Rule: machin\n" +
                "    The first rule of the feature states things\n" +
                "\n" +
                "    Background: bbb\n" +
                "      We can have some explications for the background\n" +
                "\n" +
                "      Given hello\n" +
                "\n" +
                "    Scenario: two\n" +
                "      This scenario will do things, maybe\n" +
                "\n" +
                "      Given world\n", prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void commentAtStartAndEndOfFile() {
        GherkinDocument gherkinDocument = parse("#   i am comment at start of file \n" +
                "Feature: hello\n" +
                "\n" +
                "  # i am tag hear me roar\n" +
                "  Scenario: one\n" +
                "    #some comment in the scenario\n" +
                "    #another line in the comment in the scenario\n" +
                "    Given a a <text> and a <number>\n" +
                "# i am a comment at the end of the file.");
        assertEquals("# i am comment at start of file\n" +
                        "Feature: hello\n" +
                        "\n" +
                        "  # i am tag hear me roar\n" +
                        "  Scenario: one\n" +
                        "    # some comment in the scenario\n" +
                        "    # another line in the comment in the scenario\n" +
                        "    Given a a <text> and a <number>\n" +
                        "# i am a comment at the end of the file.\n",
                prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    void commentBeforeExamples() {
        GherkinDocument gherkinDocument = parse("Feature: Comment before an Examples\n" +
                "\n" +
                "  Scenario Outline: with examples\n" +
                "    Given the <value> minimalism\n" +
                "    # then something happens\n" +
                "\n" +
                "    Examples:\n" +
                "    | value |\n" +
                "    | 1     |\n" +
                "    | 2     |\n");
        assertEquals("Feature: Comment before an Examples\n" +
                        "\n" +
                        "  Scenario Outline: with examples\n" +
                        "    Given the <value> minimalism\n" +
                        "\n" +
                        "    # then something happens\n" +
                        "    Examples:\n" +
                        "      | value |\n" +
                        "      | 1     |\n" +
                        "      | 2     |\n",
                prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    // This demonstrate some limitations like:
    // - Comments between a `@tag` and a `Scenario:` are pushed before the @tag
    // - Dangling comments (e.g. with intermediate EOL) are compacted next to some block
    // - Contiguous `###[...]` are turned into `# ##[...]`
    // - Comments after an moreIndented block are moved as indent of the following lessIndented block
    @Test
    void commentsBeforeAndAfterKeywords(){
        GherkinDocument gherkinDocument = parse("# before Feature\n" +
                "Feature: Comments everywhere\n" +
                "# after Feature\n" +
                "  # after Feature indent\n" +
                "\n" +
                "    # before Background indent\n" +
                "  # before Background\n" +
                "  Background: foo\n" +
                "  # after Background\n" +
                "\n" +
                "  # before Scenario\n" +
                "  Scenario: foo\n" +
                "  # after Scenario\n" +
                "    # before Given\n" +
                "    Given nothing\n" +
                "    # after Given\n" +
                "\n" +
                "  #####\n" +
                "  # And spaced out\n" +
                "  ####\n" +
                "\n" +
                "  # before Tag\n" +
                "  @foo\n" +
                "  # after Tag\n" +
                "  Scenario: Foo\n" +
                "    Given another\n" +
                "\n" +
                "  # before Rule\n" +
                "  Rule:\n" +
                "  # after Rule\n" +
                "    # after Rule indent\n" +
                "\n" +
                "    # background comment\n" +
                "    Background: foo\n" +
                "\n" +
                "    # middling comment\n" +
                "\n" +
                "    # another middling comment\n" +
                "\n" +
                "    # scenario comment\n" +
                "    Scenario: foo\n");
        assertEquals( "# before Feature\n" +
                        "Feature: Comments everywhere\n" +
                        "# after Feature\n" +
                        "\n" +
                        "  # after Feature indent\n" +
                        "  # before Background indent\n" +
                        "  # before Background\n" +
                        "  Background: foo\n" +
                        "\n" +
                        "  # after Background\n" +
                        "  # before Scenario\n" +
                        "  Scenario: foo\n" +
                        "  # after Scenario\n" +
                        "    # before Given\n" +
                        "    Given nothing\n" +
                        "\n" +
                        "  # after Given\n" +
                        "  # ####\n" +
                        "  # And spaced out\n" +
                        "  # ###\n" +
                        "  # before Tag\n" +
                        "  # after Tag\n" +
                        "  @foo\n" +
                        "  Scenario: Foo\n" +
                        "    Given another\n" +
                        "\n" +
                        "  # before Rule\n" +
                        "  Rule:\n" +
                        "  # after Rule\n" +
                        "\n" +
                        "    # after Rule indent\n" +
                        "    # background comment\n" +
                        "    Background: foo\n" +
                        "\n" +
                        "    # middling comment\n" +
                        "    # another middling comment\n" +
                        "    # scenario comment\n" +
                        "    Scenario: foo\n",
                prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    void emptyNames(){
        GherkinDocument gherkinDocument = parse("# before Feature\n" +
                "Feature:   \n" +
                "\n" +
                "  Background:   \n" +
                "\n" +
                "  Scenario:   \n" +
                "    Given nothing\n" +
                "\n" +
                "  @foo\n" +
                "  Scenario:   \n" +
                "    Given another\n" +
                "\n" +
                "  Rule:   \n" +
                "\n" +
                "    Background:   \n" +
                "    Example:   \n" +
                "      Given   \n" +
                "      When   \n" +
                "      Then   \n" +
                "    Scenario:   \n");
        assertEquals("# before Feature\n" +
                "Feature:\n" +
                "\n" +
                "  Background:\n" +
                "\n" +
                "  Scenario:\n" +
                "    Given nothing\n" +
                "\n" +
                "  @foo\n" +
                "  Scenario:\n" +
                "    Given another\n" +
                "\n" +
                "  Rule:\n" +
                "\n" +
                "    Background:\n" +
                "\n" +
                "    Example:\n" +
                "      Given   \n" +
                "      When   \n" +
                "      Then   \n" +
                "\n" +
                "    Scenario:\n",
                prettyPrint(gherkinDocument, Syntax.gherkin));
    }
    
    private GherkinDocument parse(String gherkin) {
        GherkinParser parser = GherkinParser
                .builder()
                .includeGherkinDocument(true)
                .includeSource(false)
                .includePickles(false)
                .build();
        return parser.parse(Envelope.of(new Source("test.feature", gherkin, SourceMediaType.TEXT_X_CUCUMBER_GHERKIN_PLAIN)))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No envelope"))
                .getGherkinDocument()
                .orElseThrow(() -> new RuntimeException("No gherkin document"));
    }
}
