<h1 align="center">
  <img src="https://raw.githubusercontent.com/cucumber/cucumber-js/7df2c9b4f04099b81dc5c00cd73b404401cd6e46/docs/images/logo.svg" alt="">
  <br>
  Gherkin Utils
</h1>
<p align="center">
  <b>Utilities for working with Gherkin documents and AST</b>
</p>

<p align="center">
  <a href="https://www.npmjs.com/package/@cucumber/gherkin-utils">
    <img src="https://img.shields.io/npm/v/@cucumber/gherkin-utils.svg?color=dark-green" alt="npm">
  </a>
  <a href="https://central.sonatype.com/artifact/io.cucumber/gherkin-utils">
    <img src="https://img.shields.io/maven-central/v/io.cucumber/gherkin-utils.svg?label=Maven%20Central&color=dark-green" alt="maven-central">
  </a>
  <a href="https://github.com/cucumber/gherkin-utils/actions/workflows/release-github.yaml">
    <img src="https://github.com/cucumber/gherkin-utils/actions/workflows/release-github.yaml/badge.svg" alt="build">
  </a>
  <a href="https://opencollective.com/cucumber">
    <img src="https://opencollective.com/cucumber/backers/badge.svg" alt="backers">
  </a>
  <a href="https://opencollective.com/cucumber">
    <img src="https://opencollective.com/cucumber/sponsors/badge.svg" alt="sponsors">
  </a>
</p>

## Features

- ✨ Formatting
- 👉 Translation of `.feature` files to `.feature.md`
- 🚶‍♂️ Document walker
- 📁 Document handler

## Install

Gherkin Utils is [available on npm](https://www.npmjs.com/package/@cucumber/gherkin-utils) for JavaScript:

```console
npm install @cucumber/gherkin-utils
```

Gherkin Utils is [available on Maven Central](https://central.sonatype.com/artifact/io.cucumber/gherkin-utils) for Java, by adding the dependency to your `pom.xml`:

```xml
<dependencies>
  <dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>gherkin-utils</artifactId>
    <version>9.0.0</version>
  </dependency>
</dependencies>
```

## Usage

### Command line

The command-line tool can be used to format `.feature` files or to translate `.feature` files
into `.feature.md` files.

The following example translates all `.feature` files to `.feature.md` files and then deletes the `.feature` files (see [Markdown with Gherkin](https://github.com/cucumber/common/blob/main/gherkin/MARKDOWN_WITH_GHERKIN.md)).
**Note**: Globs must be quoted to prevent the shell from expanding the globs.

```console
npx @cucumber/gherkin-utils format --move "features/**/*.feature" "features/**/*.feature.md"
```

For more details on usage, see the help menu.

```console
npx @cucumber/gherkin-utils --help
```

### As a library

This module can also be used as a library. It provides two main utilities, `pretty` and `gherkinDocumentWalker`.

#### pretty(gherkinDocument: messages.GherkinDocument, syntax: 'gherkin' | 'markdown')

This function takes a GherkinDocument as input and returns a pretty-printed representation in Gherkin or Markdown.

```javascript
import { AstBuilder, GherkinClassicTokenMatcher, Parser } from '@cucumber/gherkin'
import { pretty } from '@cucumber/gherkin-utils'
import { IdGenerator } from '@cucumber/messages'

const uuidFn = IdGenerator.uuid()

const builder = new AstBuilder(uuidFn)
const matcher = new GherkinClassicTokenMatcher()
const parser = new Parser(builder, matcher)

const feature = `Feature:
Scenario:
Given step text`

const gherkinDocument = parser.parse(feature)

const formattedGherkinFeature = pretty(gherkinDocument)
/*
Feature:

  Scenario:
    Given step text

*/
const formattedGherkinMarkdownFeature = pretty(gherkinDocument, 'markdown')
/*
# Feature:

## Scenario:
* Given step text

*/
```

#### GherkinDocumentWalker class

The GherkinDocumentWalker is a class for walking and filtering the AST produced by Gherkin after parsing a feature file.
When running `walkGherkinDocument` on a GherkinDocument, it will produce a deep copy of the object.

It takes two arguments upon creation:

- filters: set of functions used to know if the walked elements are kept in the result. By default, all elements are kept.
- handlers: set of function that can be used to alter the produced elements.

Filtering keeps the meaning of the original GherkinDocument, which means:

- if a `Background` was present, it will always be in the `Feature` (or `Rule`)
- the kept scenarios will have the same steps and examples than the original

By default, all elements are accepted, which means that if you want to do filtering you should reject all other elements. To ease this, we also provide the `rejectAllFilters`.

Here's an example:

```typescript
import { GherkinDocumentWalker, rejectAllFilters } from '@cucumber/gherkin-utils';

// Only keeps scenarios which name include 'magic'
const filter = new GherkinDocumentWalker({
  ...rejectAllFilters,
  ...{ acceptScenario: (scenario) => scenario.name.includes('magic') },
})

// Makes a list with all the scenario names
const allScenarioNames: string[] = []
const scenarioNameFinder = new GherkinDocumentWalker({}, {
  handleScenario: (scenario) => allScenarioNames.push(scenario.name),
})
```
