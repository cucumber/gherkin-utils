package io.cucumber.gherkin.utils;

import io.cucumber.messages.types.Background;
import io.cucumber.messages.types.Comment;
import io.cucumber.messages.types.DataTable;
import io.cucumber.messages.types.DocString;
import io.cucumber.messages.types.Examples;
import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.FeatureChild;
import io.cucumber.messages.types.GherkinDocument;
import io.cucumber.messages.types.Location;
import io.cucumber.messages.types.Rule;
import io.cucumber.messages.types.RuleChild;
import io.cucumber.messages.types.Scenario;
import io.cucumber.messages.types.Step;
import io.cucumber.messages.types.TableRow;
import io.cucumber.messages.types.Tag;

import java.util.List;
import java.util.stream.Collectors;

import static io.cucumber.gherkin.utils.GherkinDocumentWalker.CommentBlock.groupByCommentBlock;
import static io.cucumber.gherkin.utils.GherkinDocumentUtil.groupByBlock;

/**
 * Utility to walk a Gherkin document.
 *
 * <p>The walker uses an event based model. The matching {@code start*},
 * {@code handle*}, and {@code finish*} methods of the {@link Handler} are
 * called as the walker traverses the document.
 *
 * <p>Tags are treated on a per-line basis,
 * {@link Handler#handleTags(Object, List)} is called for each set of tags on
 * the same line.
 *
 * <p>Comments lines are treated on a block by block basis. A block is a set
 * of continues comment lines, {@link Handler#handleComments(Object, List)}
 * is invoked once for each block.
 *
 * <p>In addition to the elements of the {@link GherkinDocument} the walker
 * distinguishes attached and detached comment blocks. Attached comment blocks
 * are processed within the scope of the element they are attached to.
 *
 * <p>If the line immediately after a comment block is not empty the comment
 * block is considered attached to that line. Otherwise, it is detached.
 */
public final class GherkinDocumentWalker<Context, Consumer> {

    private final Handler<Context, Consumer> handler;

    public GherkinDocumentWalker(Handler<Context, Consumer> handler) {
        this.handler = handler;
    }

    public void walkGherkinDocument(GherkinDocument gherkinDocument, Consumer consumer) {
        var context = handler.createContext(consumer);
        var remainingComments = groupByCommentBlock(gherkinDocument.getComments());
        handler.startGherkinDocument(context, gherkinDocument);
        handler.handleGherkinDocument(context, gherkinDocument);
        gherkinDocument.getFeature()
                .ifPresent(feature -> walkFeature(context, feature, remainingComments));
        remainingComments.forEach(comment -> walkDetachedComment(context, comment));
        handler.finishGherkinDocument(context, gherkinDocument);
    }

    private void walkFeature(Context context, Feature feature, List<CommentBlock> remainingComments) {
        walkDetachedCommentsUpTo(context, remainingComments, GherkinDocumentUtil.startOf(feature));
        handler.startFeature(context, feature);
        walkFeatureLanguage(context, feature);
        walkTags(context, feature.getTags(), remainingComments);
        walkCommentsUpto(context, remainingComments, feature.getLocation());
        handler.handleFeature(context, feature);
        walkDescription(context, feature.getDescription());
        walkFeatureChildren(context, feature, remainingComments);
        handler.finishFeature(context, feature);
    }

    private void walkFeatureLanguage(Context context, Feature feature) {
        var language = feature.getLanguage();
        handler.startLanguage(context, language);
        handler.handleLanguage(context, language);
        handler.finishLanguage(context, language);
    }

    private void walkDescription(Context context, String description) {
        // Descriptions are empty when not provided
        if (description.isEmpty()) {
            return;
        }
        handler.startDescription(context, description);
        handler.handleDescription(context, description);
        handler.finishDescription(context, description);
    }

    private void walkFeatureChildren(Context context, Feature feature, List<CommentBlock> remainingComments) {
        feature.getChildren().forEach(featureChild -> walkFeatureChild(context, featureChild, remainingComments));
    }

    private void walkFeatureChild(Context context, FeatureChild child, List<CommentBlock> remainingComments) {
        child.getRule()
                .ifPresent(rule -> walkRule(context, rule, remainingComments));
        child.getBackground()
                .ifPresent(background -> walkBackground(context, background, remainingComments));
        child.getScenario()
                .ifPresent(scenario -> walkScenario(context, scenario, remainingComments));
    }

    private void walkRule(Context context, Rule rule, List<CommentBlock> remainingComments) {
        walkDetachedCommentsUpTo(context, remainingComments, GherkinDocumentUtil.startOf(rule));
        handler.startRule(context, rule);
        walkTags(context, rule.getTags(), remainingComments);
        walkCommentsUpto(context, remainingComments, rule.getLocation());
        handler.handleRule(context, rule);
        walkDescription(context, rule.getDescription());
        walkRuleChildren(context, rule, remainingComments);
        handler.finishRule(context, rule);
    }

    private void walkRuleChildren(Context context, Rule rule, List<CommentBlock> remainingComments) {
        rule.getChildren().forEach(featureChild -> walkRuleChild(context, featureChild, remainingComments));
    }

    private void walkRuleChild(Context context, RuleChild child, List<CommentBlock> remainingComments) {
        child.getBackground()
                .ifPresent(background -> walkBackground(context, background, remainingComments));
        child.getScenario()
                .ifPresent(scenario -> walkScenario(context, scenario, remainingComments));
    }

    private void walkBackground(Context context, Background background, List<CommentBlock> remainingComments) {
        walkDetachedCommentsUpTo(context, remainingComments, GherkinDocumentUtil.startOf(background));
        handler.startBackground(context, background);
        walkCommentsUpto(context, remainingComments, background.getLocation());
        handler.handleBackground(context, background);
        walkDescription(context, background.getDescription());
        walkSteps(context, background.getSteps(), remainingComments);
        handler.finishBackground(context, background);
    }

    private void walkScenario(Context context, Scenario scenario, List<CommentBlock> remainingComments) {
        walkDetachedCommentsUpTo(context, remainingComments, GherkinDocumentUtil.startOf(scenario));
        handler.startScenario(context, scenario);
        walkTags(context, scenario.getTags(), remainingComments);
        walkCommentsUpto(context, remainingComments, scenario.getLocation());
        handler.handleScenario(context, scenario);
        walkDescription(context, scenario.getDescription());
        walkSteps(context, scenario.getSteps(), remainingComments);
        walkExamples(context, scenario, remainingComments);
        handler.finishScenario(context, scenario);
    }

    private void walkSteps(Context context, List<Step> steps, List<CommentBlock> remainingComments) {
        steps.forEach(step -> walkStep(context, step, remainingComments));
    }

    private void walkStep(Context context, Step step, List<CommentBlock> remainingComments) {
        walkDetachedCommentsUpTo(context, remainingComments, GherkinDocumentUtil.startOf(step));
        handler.startStep(context, step);
        walkCommentsUpto(context, remainingComments, step.getLocation());
        handler.handleStep(context, step);
        walkStepArguments(context, step, remainingComments);
        handler.finishStep(context, step);
    }

    private void walkStepArguments(Context context, Step step, List<CommentBlock> remainingComments) {
        var dataTable = step.getDataTable();
        var docString = step.getDocString();

        var dataTableLocation = dataTable.map(DataTable::getLocation).map(Location::getLine).orElse(-1);
        var docStringLocation = docString.map(DocString::getLocation).map(Location::getLine).orElse(-1);

        if (dataTableLocation < docStringLocation) {
            dataTable.ifPresent(it -> walkDataTable(context, it, remainingComments));
            docString.ifPresent(it -> walkDocString(context, it, remainingComments));
        } else {
            docString.ifPresent(it -> walkDocString(context, it, remainingComments));
            dataTable.ifPresent(it -> walkDataTable(context, it, remainingComments));
        }
    }

    private void walkDataTable(Context context, DataTable dataTable, List<CommentBlock> remainingComments) {
        walkDetachedCommentsUpTo(context, remainingComments, GherkinDocumentUtil.startOf(dataTable));
        handler.startDataTable(context, dataTable);
        walkCommentsUpto(context, remainingComments, dataTable.getLocation());
        handler.handleDataTable(context, dataTable);
        walkDataTableRows(context, dataTable, remainingComments);
        handler.finishDataTable(context, dataTable);
    }

    private void walkDataTableRows(Context context, DataTable dataTable, List<CommentBlock> remainingComments) {
        dataTable.getRows().forEach(tableRow -> walkDatTableRow(context, tableRow, remainingComments));
    }

    private void walkDatTableRow(Context context, TableRow tableRow, List<CommentBlock> remainingComments) {
        walkCommentsUpto(context, remainingComments, tableRow.getLocation());
        handler.startDataTableRow(context, tableRow);
        handler.handleDataTableRow(context, tableRow);
        handler.finishDataTableRow(context, tableRow);
    }

    private void walkDocString(Context context, DocString docString, List<CommentBlock> remainingComments) {
        walkDetachedCommentsUpTo(context, remainingComments, GherkinDocumentUtil.startOf(docString));
        handler.startDocString(context, docString);
        walkCommentsUpto(context, remainingComments, docString.getLocation());
        handler.handleDocString(context, docString);
        handler.finishDocString(context, docString);
    }

    private void walkExamples(Context context, Scenario scenario, List<CommentBlock> remainingComments) {
        scenario.getExamples().forEach(examples -> walkExamples(context, examples, remainingComments));
    }

    private void walkExamples(Context context, Examples examples, List<CommentBlock> remainingComments) {
        walkDetachedCommentsUpTo(context, remainingComments, GherkinDocumentUtil.startOf(examples));
        handler.startExamples(context, examples);
        walkTags(context, examples.getTags(), remainingComments);
        walkCommentsUpto(context, remainingComments, examples.getLocation());
        handler.handleExamples(context, examples);
        walkDescription(context, examples.getDescription());
        examples.getTableHeader().ifPresent(headerRow -> {
            walkExampleTableRow(context, headerRow, remainingComments);
            examples.getTableBody().forEach(bodyRow -> walkExampleTableRow(context, bodyRow, remainingComments));
        });
        handler.finishExamples(context, examples);
    }

    private void walkExampleTableRow(Context context, TableRow tableRow, List<CommentBlock> remainingComments) {
        walkDetachedCommentsUpTo(context, remainingComments, tableRow.getLocation());
        handler.startExamplesTableRow(context, tableRow);
        walkCommentsUpto(context, remainingComments, tableRow.getLocation());
        handler.handleExamplesTableRow(context, tableRow);
        handler.finishExamplesTableRow(context, tableRow);
    }

    private void walkTags(Context context, List<Tag> tags, List<CommentBlock> remainingComments) {
        if (tags.isEmpty()) {
            return;
        }
        walkDetachedCommentsUpTo(context, remainingComments, tags.get(0).getLocation());
        handler.startTags(context, tags);
        groupByBlock(tags, Tag::getLocation).forEach(block -> {
            var location = block.get(0).getLocation();
            walkCommentsUpto(context, remainingComments, location);
            handler.handleTags(context, block);
        });
        handler.finishTags(context, tags);
    }

    private void walkCommentsUpto(Context context, List<CommentBlock> remainingComments, Location upTo) {
        int upToLine = upTo.getLine();
        var iterator = remainingComments.iterator();
        while (iterator.hasNext()) {
            var commentBlock = iterator.next();
            if (commentBlock.start().getLine() < upToLine) {
                iterator.remove();
                walkComment(context, commentBlock);
            } else {
                return;
            }
        }
    }

    private void walkComment(Context context, CommentBlock block) {
        var comments = block.comments();
        handler.startComments(context, comments);
        handler.handleComments(context, comments);
        handler.finishComments(context, comments);
    }

    private void walkDetachedCommentsUpTo(Context context, List<CommentBlock> remainingComments, Location upTo) {
        // A comment is detached if it is not above the current line.
        int upToLine = upTo.getLine() - 1;
        var iterator = remainingComments.iterator();
        while (iterator.hasNext()) {
            var commentBlock = iterator.next();
            if (commentBlock.finish().getLine() < upToLine) {
                iterator.remove();
                walkDetachedComment(context, commentBlock);
            } else {
                return;
            }
        }
    }

    private void walkDetachedComment(Context context, CommentBlock block) {
        var comments = block.comments();
        handler.startDetachedComments(context, comments);
        handler.handleDetachedComments(context, comments);
        handler.finishDetachedComment(context, comments);
    }

    record CommentBlock(Location start, Location finish, List<Comment> comments) {

        static List<CommentBlock> groupByCommentBlock(List<Comment> comments) {
            var blocks = groupByBlock(comments, Comment::getLocation);
            return blocks.stream() //
                    .map(block -> {
                        var start = block.get(0).getLocation();
                        var end = block.get(block.size() - 1).getLocation();
                        return new CommentBlock(start, end, block);
                    }) //
                    .collect(Collectors.toList());
        }
    }

    /**
     * A state free handler for events emitted by the {@link GherkinDocumentWalker}.
     *
     * <p>The instance of {@code Context} should be used to store state. The
     * {@code Output} instance should be used to output any results to.
     *
     * @param <Context> type of the object that stores the state for this
     *                  handler.
     * @param <Output>  the sink that consumes the result produced by this
     *                  handler.
     */
    public interface Handler<Context, Output> {

        Context createContext(Output output);


        default void startGherkinDocument(Context context, GherkinDocument gherkinDocument) {
        }

        default void finishGherkinDocument(Context context, GherkinDocument gherkinDocument) {
        }

        default void handleGherkinDocument(Context context, GherkinDocument gherkinDocument) {
        }


        default void startFeature(Context context, Feature feature) {
        }

        default void handleFeature(Context context, Feature feature) {
        }

        default void finishFeature(Context context, Feature feature) {
        }


        default void startLanguage(Context context, String language) {
        }

        default void handleLanguage(Context context, String language) {
        }

        default void finishLanguage(Context context, String language) {
        }


        default void startDescription(Context context, String description) {
        }

        default void handleDescription(Context context, String description) {
        }

        default void finishDescription(Context context, String description) {
        }


        default void startRule(Context context, Rule rule) {
        }

        default void handleRule(Context context, Rule rule) {
        }

        default void finishRule(Context context, Rule rule) {
        }


        default void startBackground(Context context, Background background) {
        }

        default void handleBackground(Context context, Background background) {
        }

        default void finishBackground(Context context, Background background) {
        }


        default void startScenario(Context context, Scenario scenario) {
        }

        default void handleScenario(Context context, Scenario scenario) {
        }

        default void finishScenario(Context context, Scenario scenario) {
        }


        default void startStep(Context context, Step step) {
        }

        default void handleStep(Context context, Step step) {
        }

        default void finishStep(Context context, Step step) {
        }


        default void startExamples(Context context, Examples examples) {
        }

        default void handleExamples(Context context, Examples examples) {
        }

        default void finishExamples(Context context, Examples examples) {
        }


        default void startExamplesTableRow(Context context, TableRow tableRow) {
        }

        default void handleExamplesTableRow(Context context, TableRow tableRow) {
        }

        default void finishExamplesTableRow(Context context, TableRow tableRow) {
        }


        default void startDataTable(Context context, DataTable dataTable) {
        }

        default void handleDataTable(Context context, DataTable dataTable) {
        }

        default void finishDataTable(Context context, DataTable dataTable) {
        }


        default void startDataTableRow(Context context, TableRow tableRow) {
        }

        default void handleDataTableRow(Context context, TableRow tableRow) {
        }

        default void finishDataTableRow(Context context, TableRow tableRow) {
        }


        default void startDocString(Context context, DocString docString) {
        }

        default void handleDocString(Context context, DocString docString) {
        }

        default void finishDocString(Context context, DocString docString) {
        }


        default void startTags(Context context, List<Tag> tags) {
        }

        default void handleTags(Context context, List<Tag> tags) {
        }

        default void finishTags(Context context, List<Tag> tags) {
        }


        default void startDetachedComments(Context context, List<Comment> comments) {
        }

        default void handleDetachedComments(Context context, List<Comment> comments) {

        }

        default void finishDetachedComment(Context context, List<Comment> comments) {
        }


        default void startComments(Context context, List<Comment> comments) {
        }

        default void handleComments(Context context, List<Comment> comments) {
        }

        default void finishComments(Context context, List<Comment> comments) {
        }
    }
}
