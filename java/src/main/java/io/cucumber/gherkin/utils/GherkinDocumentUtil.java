package io.cucumber.gherkin.utils;

import io.cucumber.messages.LocationComparator;
import io.cucumber.messages.types.Background;
import io.cucumber.messages.types.DataTable;
import io.cucumber.messages.types.DocString;
import io.cucumber.messages.types.Examples;
import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.Location;
import io.cucumber.messages.types.Rule;
import io.cucumber.messages.types.Scenario;
import io.cucumber.messages.types.Step;
import io.cucumber.messages.types.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

final class GherkinDocumentUtil {

    /**
     * Groups elements into blocks of consecutive lines.
     */
    static <T> List<List<T>> groupByBlock(List<T> elements, Function<T, Location> location) {
        if (elements.isEmpty()) {
            return new ArrayList<>();
        }
        var blocks = new ArrayList<List<T>>();

        var currentBlockStartIndex = 0;
        int currentLine = location.apply(elements.get(0)).getLine();
        int size = elements.size();
        for (int i = 0; i < size; i++) {
            int candidate = location.apply(elements.get(i)).getLine();
            // Start a new block
            if (currentLine < candidate - 1) {
                blocks.add(elements.subList(currentBlockStartIndex, i));
                currentBlockStartIndex = i;
            }
            currentLine = candidate;
        }
        blocks.add(elements.subList(currentBlockStartIndex, elements.size()));
        return blocks;
    }

    static Location startOf(Feature element) {
        var location = Stream.of(element.getLocation());
        var tagLocations = element.getTags().stream().map(Tag::getLocation);
        return Stream.concat(tagLocations, location).min(new LocationComparator()).orElseThrow();
    }

    static Location startOf(Rule element) {
        var location = Stream.of(element.getLocation());
        var tagLocations = element.getTags().stream().map(Tag::getLocation);
        return Stream.concat(tagLocations, location).min(new LocationComparator()).orElseThrow();
    }

    static Location startOf(Background element) {
        return element.getLocation();
    }

    static Location startOf(Scenario element) {
        var location = Stream.of(element.getLocation());
        var tagLocations = element.getTags().stream().map(Tag::getLocation);
        return Stream.concat(tagLocations, location).min(new LocationComparator()).orElseThrow();
    }

    static Location startOf(Examples element) {
        var location = Stream.of(element.getLocation());
        var tagLocations = element.getTags().stream().map(Tag::getLocation);
        return Stream.concat(tagLocations, location).min(new LocationComparator()).orElseThrow();
    }

    static Location startOf(Step element) {
        return element.getLocation();
    }

    static Location startOf(DataTable element) {
        return element.getLocation();
    }

    static Location startOf(DocString element) {
        return element.getLocation();
    }

    private GherkinDocumentUtil() {
        /* no-op */
    }
}
