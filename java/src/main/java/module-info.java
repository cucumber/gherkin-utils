module io.cucumber.gherkin.utils {
    requires org.jspecify;

    requires transitive io.cucumber.messages;
    requires transitive io.cucumber.gherkin;

    exports io.cucumber.gherkin.utils;
    exports io.cucumber.gherkin.utils.pretty;
}
