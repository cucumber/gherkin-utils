package io.cucumber.gherkin.utils.pretty;

import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.Envelope;
import io.cucumber.messages.types.GherkinDocument;
import io.cucumber.messages.types.Source;
import io.cucumber.messages.types.SourceMediaType;
import org.junit.jupiter.api.Test;

import static io.cucumber.gherkin.utils.pretty.Pretty.prettyPrint;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("MisleadingEscapedSpace")
public class PrettyTest {
    @Test
    public void emptyFile() {
        String gherkin = "";
        GherkinDocument gherkinDocument = parse(gherkin);
        assertEquals("", prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void scenarioOutlineWithExamples() {
        GherkinDocument gherkinDocument = parse("""
                    Feature: Overdue tasks
                  Let users know when tasks are overdue, even when using other
                  features of the app
                
                
                
                
                
                
                      Scenario Outline: eating
                  Given there are <start> cucumbers
                          When I eat <eat> cucumbers
                 Then I should have <left> cucumbers
                
                     Examples:\s
                    | start | eat |left|
                      |12 |    5 |             7 |
                   |20|   5 |                             15 |
                """);
        assertEquals("""
                        Feature: Overdue tasks
                          Let users know when tasks are overdue, even when using other
                          features of the app
                        
                          Scenario Outline: eating
                            Given there are <start> cucumbers
                            When I eat <eat> cucumbers
                            Then I should have <left> cucumbers
                        
                            Examples:
                              | start | eat | left |
                              | 12    | 5   | 7    |
                              | 20    | 5   | 15   |
                        """,
                prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void languageHeaderIsNotEn() {
        GherkinDocument gherkinDocument = parse("""
                # language: no
                Egenskap: hallo""");
        assertEquals("""
                # language: no
                Egenskap: hallo
                """, prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void emptyScenarios() {
        GherkinDocument gherkinDocument = parse("""
                Feature: hello
                
                  Scenario: one
                
                  Scenario: Two""");
        assertEquals("""
                Feature: hello
                
                  Scenario: one
                
                  Scenario: Two
                """, prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void twoScenarios() {
        GherkinDocument gherkinDocument = parse("""
                Feature: hello
                
                  Scenario: one
                    Given hello
                
                  Scenario: two
                    Given world""");
        assertEquals("""
                Feature: hello
                
                  Scenario: one
                    Given hello
                
                  Scenario: two
                    Given world
                """, prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void twoScenariosInRule() {
        GherkinDocument gherkinDocument = parse("""
                Feature: hello
                
                  Rule: ok
                
                    Scenario: one
                      Given hello
                
                    Scenario: two
                      Given world
                """);
        assertEquals("""
                Feature: hello
                
                  Rule: ok
                
                    Scenario: one
                      Given hello
                
                    Scenario: two
                      Given world
                """, prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void featureWithBackgroundAndScenario() {
        GherkinDocument gherkinDocument = parse("""
                Feature: hello
                
                  Background: bbb
                    Given hello
                
                  Scenario: two
                    Given world""");
        assertEquals("""
                Feature: hello
                
                  Background: bbb
                    Given hello
                
                  Scenario: two
                    Given world
                """, prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void ruleWithBackgroundAndScenario() {
        GherkinDocument gherkinDocument = parse("""
                Feature: hello
                
                  Rule: machin
                
                    Background: bbb
                      Given hello
                
                    Scenario: two
                      Given world
                """);
        assertEquals("""
                Feature: hello
                
                  Rule: machin
                
                    Background: bbb
                      Given hello
                
                    Scenario: two
                      Given world
                """, prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void tags() {
        GherkinDocument gherkinDocument = parse("""
                @featureTag
                Feature: hello
                
                  Rule: machin
                
                    Background: bbb
                      Given hello
                
                    @scenarioTag @secondTag
                    Scenario: two
                      Given world
                """);
        assertEquals("""
                @featureTag
                Feature: hello
                
                  Rule: machin
                
                    Background: bbb
                      Given hello
                
                    @scenarioTag @secondTag
                    Scenario: two
                      Given world
                """, prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void exampleTables() {
        GherkinDocument gherkinDocument = parse("""
                Feature: hello
                
                  # i am tag hear me roar
                  Scenario: one
                    #some comment in the scenario
                    #another line in the comment in the scenario
                    Given a a <text> and a <number>
                
                    # comment1 is here  \s
                  \
                    Examples: some data
                 # comment2 is here    \s
                      | text | number |
                 # comment3 is here    \s
                 # comment4 is here    \s
                      | a    |      1 |
                      | ab   |     10 |
                 # comment5 is here    \s
                 # comment6 is here    \s
                      | abc  |    100 |
                """);
        assertEquals("""
                Feature: hello
                
                  # i am tag hear me roar
                  Scenario: one
                    # some comment in the scenario
                    # another line in the comment in the scenario
                    Given a a <text> and a <number>
                
                    # comment1 is here
                    Examples: some data
                      # comment2 is here
                      | text | number |
                      # comment3 is here
                      # comment4 is here
                      | a    | 1      |
                      | ab   | 10     |
                      # comment5 is here
                      # comment6 is here
                      | abc  | 100    |
                """, prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void dataTables() {
        GherkinDocument gherkinDocument = parse("""
                Feature: hello
                
                  Scenario: one
                    Given a data table:
                      | text | numbers |
                      | a    |       1 |
                      | ab   |      10 |
                      | abc  |     100 |
                """);
        assertEquals("""
                Feature: hello
                
                  Scenario: one
                    Given a data table:
                      | text | numbers |
                      | a    | 1       |
                      | ab   | 10      |
                      | abc  | 100     |
                """, prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void cjkTables() {
        GherkinDocument gherkinDocument = parse("""
                Feature: hello
                
                  Scenario: one
                    Given a data table:
                      |路| numbers |
                      |路|       1 |
                      |路步|      10 |
                      |路步路|     100 |
                """);
        assertEquals("""
                Feature: hello
                
                  Scenario: one
                    Given a data table:
                      | 路     | numbers |
                      | 路     | 1       |
                      | 路步   | 10      |
                      | 路步路 | 100     |
                """, prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void docString() {
        GherkinDocument gherkinDocument = parse("""
                        Feature: hello
                        
                          Scenario: one
                            Given a doc string:
                               ""\"ndjson
                         { "hello": "world" }
                         { "goodbye": "moon" }
                               ""\"
                        """
                );

        assertEquals("""
                Feature: hello
                
                  Scenario: one
                    Given a doc string:
                      ""\"ndjson
                      { "hello": "world" }
                      { "goodbye": "moon" }
                      ""\"
                """, prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void description() {
        GherkinDocument gherkinDocument = parse("""
                Feature: hello
                  So this is a feature
                
                  Rule: machin
                    The first rule of the feature states things
                
                    Background: bbb
                      We can have some explications for the background
                
                      Given hello
                
                    Scenario: two
                      This scenario will do things, maybe
                
                      Given world
                """);
        assertEquals("""
                Feature: hello
                  So this is a feature
                
                  Rule: machin
                    The first rule of the feature states things
                
                    Background: bbb
                      We can have some explications for the background
                
                      Given hello
                
                    Scenario: two
                      This scenario will do things, maybe
                
                      Given world
                """, prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    public void commentAtStartAndEndOfFile() {
        GherkinDocument gherkinDocument = parse("""
                #   i am comment at start of file\s
                Feature: hello
                
                  # i am tag hear me roar
                  Scenario: one
                    #some comment in the scenario
                    #another line in the comment in the scenario
                    Given a a <text> and a <number>
                # i am a comment at the end of the file.""");
        assertEquals("""
                        # i am comment at start of file
                        Feature: hello
                        
                          # i am tag hear me roar
                          Scenario: one
                            # some comment in the scenario
                            # another line in the comment in the scenario
                            Given a a <text> and a <number>
                        # i am a comment at the end of the file.
                        """,
                prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    void commentBeforeExamples() {
        GherkinDocument gherkinDocument = parse("""
                Feature: Comment before an Examples
                
                  Scenario Outline: with examples
                    Given the <value> minimalism
                    # then something happens
                
                    Examples:
                    | value |
                    | 1     |
                    | 2     |
                """);
        assertEquals("""
                        Feature: Comment before an Examples
                        
                          Scenario Outline: with examples
                            Given the <value> minimalism
                        
                            # then something happens
                            Examples:
                              | value |
                              | 1     |
                              | 2     |
                        """,
                prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    // This demonstrate some limitations like:
    // - Comments between a `@tag` and a `Scenario:` are pushed before the @tag
    // - Dangling comments (e.g. with intermediate EOL) are compacted next to some block
    // - Contiguous `###[...]` are turned into `# ##[...]`
    // - Comments after an moreIndented block are moved as indent of the following lessIndented block
    @Test
    void commentsBeforeAndAfterKeywords(){
        GherkinDocument gherkinDocument = parse("""
                # before Feature
                Feature: Comments everywhere
                # after Feature
                  # after Feature indent
                
                    # before Background indent
                  # before Background
                  Background: foo
                  # after Background
                
                  # before Scenario
                  Scenario: foo
                  # after Scenario
                    # before Given
                    Given nothing
                    # after Given
                
                  #####
                  # And spaced out
                  ####
                
                  # before Tag
                  @foo
                  # after Tag
                  Scenario: Foo
                    Given another
                
                  # before Rule
                  Rule:
                  # after Rule
                    # after Rule indent
                
                    # background comment
                    Background: foo
                
                    # middling comment
                
                    # another middling comment
                
                    # scenario comment
                    Scenario: foo
                """);
        assertEquals("""
                        # before Feature
                        Feature: Comments everywhere
                        # after Feature
                        
                          # after Feature indent
                          # before Background indent
                          # before Background
                          Background: foo
                        
                          # after Background
                          # before Scenario
                          Scenario: foo
                          # after Scenario
                            # before Given
                            Given nothing
                        
                          # after Given
                          # ####
                          # And spaced out
                          # ###
                          # before Tag
                          # after Tag
                          @foo
                          Scenario: Foo
                            Given another
                        
                          # before Rule
                          Rule:
                          # after Rule
                        
                            # after Rule indent
                            # background comment
                            Background: foo
                        
                            # middling comment
                            # another middling comment
                            # scenario comment
                            Scenario: foo
                        """,
                prettyPrint(gherkinDocument, Syntax.gherkin));
    }

    @Test
    void emptyNames(){
        GherkinDocument gherkinDocument = parse("""
                # before Feature
                Feature:  \s
                
                  Background:  \s
                
                  Scenario:  \s
                    Given nothing
                
                  @foo
                  Scenario:  \s
                    Given another
                
                  Rule:  \s
                
                    Background:  \s
                    Example:  \s
                      Given  \s
                      When  \s
                      Then  \s
                    Scenario:  \s
                """);
        assertEquals("""
                        # before Feature
                        Feature:
                        
                          Background:
                        
                          Scenario:
                            Given nothing
                        
                          @foo
                          Scenario:
                            Given another
                        
                          Rule:
                        
                            Background:
                        
                            Example:
                              Given  \s
                              When  \s
                              Then  \s
                        
                            Scenario:
                        """,
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
