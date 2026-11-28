module io.cucumber.gherkin.utils.test {
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;

    requires io.cucumber.gherkin;
    requires io.cucumber.gherkin.utils;
    requires io.cucumber.messages;
    requires org.assertj.core;

    opens io.cucumber.gherkin.utils.test;
}
