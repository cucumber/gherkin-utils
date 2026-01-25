package io.cucumber.gherkin.utils;

public interface Accumulator {
    void setDeepestLine(Integer line);

    Integer getDeepestLine();
}
