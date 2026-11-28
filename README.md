<h1 align="center">
  <img src="https://raw.githubusercontent.com/cucumber/cucumber-js/46a5a78107be27e99c6e044c69b6e8f885ce456c/docs/images/logo.svg" alt="Cucumber logo" width="75">
  <br>
  gherkin-utils
</h1>
<p align="center">
  <b>Utilities for working with Gherkin documents</b>
</p>

<p align="center">
  <a href="https://www.npmjs.com/package/@cucumber/gherkin-utils">
    <img src="https://img.shields.io/npm/v/@cucumber/gherkin-utils.svg?color=dark-green" alt="npm">
  </a>
  <a href="https://central.sonatype.com/artifact/io.cucumber/gherkin-utils">
    <img src="https://img.shields.io/maven-central/v/io.cucumber/gherkin-utils.svg?label=Maven%20Central&color=dark-green" alt="maven-central">
  </a>
</p>

## Gherkin Document Walker

The elements of a [Cucumber Messages](https://github.com/cucumber/messages) 
`GherkinDocument`. Gherkin documents can be walked using the 
`GherkinDocumentWalker`. For an example see the
[Gherkin Formatter](https://github.com/cucumber/gherkin-formatter).

The walker uses an event based model. The matching `start*` and `handle*`,
and `finish*` methods of the handler are called as the walker traverses the
document.

Notes:

* Tags are treated on a block by block basis. A block is a set of continuous tag
  lines.
* Comments lines are treated on a block by block basis. A block is a set
  of continues comment lines. The tag section can not contain detached comments. 
* In addition to the elements of the Gherkin document the walker distinguishes
  attached and detached comment blocks. Attached comment blocks are processed
  within the scope of the element they are attached to.
* If the line immediately after a comment block is not empty the comment block
  is considered attached to that line. Otherwise, it is detached.


## Usage and installation

* [Java](./java/README.md)
* [JavaScript](./javascript/README.md)
