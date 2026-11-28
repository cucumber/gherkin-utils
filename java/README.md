[![Maven Central](https://img.shields.io/maven-central/v/io.cucumber/gherkin-utils.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:io.cucumber%20AND%20a:gherkin-utils)

Gherkin Utils Usage and Installation
=====================================

## Installation

Download it from Maven Central.

## Usage

Use [io.cucumber:gherkin](https://github.com/cucumber/gherkin) to parse a
feature file. Then use the `GherkinDocumentWalker` to process it. 

```java
GherkinParser parser = GherkinParser.builder()
        .includePickles(false)
        .build();

parser.parse(Path.of("path/to/example.feature"))
        .map(Envelope::getGherkinDocument)
        .filter(Optional::isPresent)
        .flatMap(Optional::stream)
        .findFirst()
        .ifPresent(gherkinDocument -> {
            class Context {
            
                private final Consumer<String> out;
            
                Context(Consumer<String> out) {
                    this.out = out;
                }
            }
            
            var handler = new GherkinDocumentWalker.Handler<Context, Consumer<String>>() {
            
                @Override
                public Context createContext(Consumer<String> consumer) {
                    return new Context(consumer);
                }
            
                @Override
                public void handleFeature(Context context, Feature feature) {
                    context.out.accept(feature.getName());
                }
                        
                //...
            
            };
            
            var result = new ArrayList<String>();
            var documentWalker = new GherkinDocumentWalker<>(handler);
                    documentWalker.walkGherkinDocument(gherkinDocument, result::add);
            
            // Use result
        });
```
