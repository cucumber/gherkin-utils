<h1 align="center">
  <img src="https://raw.githubusercontent.com/cucumber/cucumber-js/46a5a78107be27e99c6e044c69b6e8f885ce456c/docs/images/logo.svg" alt="Cucumber logo" width="75">
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

To run Gherkin Utils as a formatter, try any of the following:

```bash
# Format `file.feature`
> npx @cucumber/gherkin-utils format features/file.feature
# Format `file.feature` and `other.feature`
> npx @cucumber/gherkin-utils format features/file.feature features/other.feature
# Format feature files directly within `features/`
> npx @cucumber/gherkin-utils format features/*.feature
# Format feature files ending with `_test.feature` in `features`
> npx @cucumber/gherkin-utils format features/*_test.feature
# Format feature files within immediate subdirectories of `features/`
> npx @cucumber/gherkin-utils format features/**/*.feature
```

To convert gherkin feature files to [Markdown with Gherkin](https://github.com/cucumber/gherkin/blob/04da83056751a1d4519d3886448e6fd0a6544fe1/MARKDOWN_WITH_GHERKIN.md) - or the other way around - while formatting, try the following:

```bash
# Format `file.feature` to gherkin markdown `file.feature.md`
npx @cucumber/gherkin-utils format --to-syntax=markdown features/file.feature
# Format `file.feature.md` to gherkin `file.feature`
npx @cucumber/gherkin-utils format --to-syntax=gherkin features/file.feature.md
```

For more details on usage, see the help menu.

```bash
npx @cucumber/gherkin-utils --help
```

### Library

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
