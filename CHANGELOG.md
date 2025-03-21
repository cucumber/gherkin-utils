# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]

## [9.2.0] - 2025-02-02
### Changed
- Update dependency gherkin to v31

## [9.1.0] - 2025-01-22
### Changed
- Update dependency gherkin to v30

## [9.0.1] - 2025-01-22
### Fixed
- [JavaScript] Amend parse bug report URL in error message

## [9.0.0] - 2024-03-26
### Changed
- [JavaScript] Fix background parameter name in GherkinDocumentHandlers ([#68](https://github.com/cucumber/gherkin-utils/pull/68))

### Fixed
- [JavaScript] Correct repo URL in `package.json`

## [8.0.6] - 2024-02-23
### Fixed
- [JavaScript] Preserve trailing comments at end of file when prettying in JavaScript ([#41](https://github.com/cucumber/gherkin-utils/pull/41)).
- [JavaScript] Prevent the introduction of trailing whitespace after headings ([#34](https://github.com/cucumber/gherkin-utils/issues/34))
- [JavaScript] Fix GherkinDocumentWalker filtering of `Rules` (cucumber/react-components#136)
- [JavaScript] Fix test execution in Windows - Closes (cucumber/gherkin-utils#2)
- [Java] Fix docstring pretty formatting ([#58](https://github.com/cucumber/gherkin-utils/issues/58))
- Table formatting for full width characters ([#53](https://github.com/cucumber/gherkin-utils/pull/53))
- [Java] Fix urls in project metadata

## [8.0.5] - 2023-06-02
### Changed
- Upgrade to `@cucumber/messages` `22.0.0`

### Fixed
- Trim trailing spaces

## [8.0.4] - 2023-05-25
### Changed
- [Java] Improve prettyPrint of comments, courtesy of @blacelle

## [8.0.3] - 2023-05-11
### Fixed
- Walk comments in `walkGherkinDocument`
- Pretty print comments in `pretty`

## [8.0.2] - 2022-11-21
### Added

### Changed
- [Java] Enabled reproducible builds
- Use GitHub Actions for releases

### Deprecated

### Fixed

### Removed

## [8.0.1] - 2022-11-01
### Fixed
- Declared required dependencies correctly ([#2072](https://github.com/cucumber/common/pull/2072))
- Update `@cucumber/gherkin-utils` `package.json` to use correct `bin` option.

## [8.0.0] - 2022-05-31
### Changed
- Upgrade to `@cucumber/gherkin` `24.0.0`

## [7.0.0] - 2021-09-01
### Added
- Added new command-line tool for formatting Gherkin documents and converting to/from Gherkin/Markdown.
Run `npx @cucumber/gherkin-utils --help` for details about usage.

### Changed
- Upgrade to `@cucumber/messages` `17.1.0`
- Upgrade to `@cucumber/gherkin` `21.0.0`

### Deprecated

## [6.0.0] - 2021-07-08
### Changed
- Bump `@cucumber/messages` version to 17.0.0

## [5.1.0] - 2021-05-26
### Added
- Added `getSource`, `getFeature`, `getBackground`, `getRule`, `getScenario` and `getExamples` methods

## [5.0.2] - 2021-05-17
### Changed
- Upgrade to `@cucumber/message-streams` `2.0.0`

## [5.0.1] - 2021-05-17
### Fixed
- Use `^x.y.z` version for `@cucumber/*` dependencies, allowing minor and patch releases to be picked up.

## [5.0.0] - 2021-05-15
### Added
- Added `Query#getSources(): readonly messages.ISource[]`

### Changed
- Upgrade to messages 16.0.0

## [4.0.0] - 2021-03-29
### Changed
- Upgrade to messages 15.0.0

## [3.0.0] - 2021-02-07
### Changed
- Upgrade to messages 14.0.0

## [2.1.1] - 2020-12-13
### Fixed
- [JavaScript] Remove unneded dependency on `@cucumber/gherkin`

## [2.1.0] - 2020-11-03
### Added
- [JavaScript] The `Query` class has been added to this library, moved from `@cucumber/gherkin`

## [2.0.1] - 2020-09-02
### Fixed
- Do not fail when walking empty or commented Gherkin documents.
([#1169](https://github.com/cucumber/cucumber/pull/1169)
[@vincent-psarga](https://github.com/vincent-psarga)
[@aslakhellesoy](https://github.com/aslakhellesoy)
[@cbliard](https://github.com/cbliard))

## [2.0.0] - 2020-08-07
### Changed
- Update `messages` to 13.0.1

## [1.0.1] - 2020-06-29
### Fixed
- [JavaScript] Upgrade Gherkin

## [1.0.0] - 2020-06-25
### Added
- First release

[Unreleased]: https://github.com/cucumber/gherkin-utils/compare/v9.2.0...HEAD
[9.2.0]: https://github.com/cucumber/gherkin-utils/compare/v9.1.0...v9.2.0
[9.1.0]: https://github.com/cucumber/gherkin-utils/compare/v9.0.1...v9.1.0
[9.0.1]: https://github.com/cucumber/gherkin-utils/compare/v9.0.0...v9.0.1
[9.0.0]: https://github.com/cucumber/gherkin-utils/compare/v8.0.6...v9.0.0
[8.0.6]: https://github.com/cucumber/gherkin-utils/compare/v8.0.5...v8.0.6
[8.0.5]: https://github.com/cucumber/gherkin-utils/compare/v8.0.4...v8.0.5
[8.0.4]: https://github.com/cucumber/gherkin-utils/compare/v8.0.3...v8.0.4
[8.0.3]: https://github.com/cucumber/gherkin-utils/compare/v8.0.2...v8.0.3
[8.0.2]: https://github.com/cucumber/gherkin-utils/compare/v8.0.1...v8.0.2
[8.0.1]: https://github.com/cucumber/gherkin-utils/compare/v8.0.0...v8.0.1
[8.0.0]: https://github.com/cucumber/gherkin-utils/compare/v7.0.0...v8.0.0
[7.0.0]: https://github.com/cucumber/gherkin-utils/compare/v6.0.0...v7.0.0
[6.0.0]: https://github.com/cucumber/gherkin-utils/compare/v5.1.0...v6.0.0
[5.1.0]: https://github.com/cucumber/gherkin-utils/compare/v5.0.2...v5.1.0
[5.0.2]: https://github.com/cucumber/gherkin-utils/compare/v5.0.1...v5.0.2
[5.0.1]: https://github.com/cucumber/gherkin-utils/compare/v5.0.0...v5.0.1
[5.0.0]: https://github.com/cucumber/gherkin-utils/compare/v4.0.0...v5.0.0
[4.0.0]: https://github.com/cucumber/gherkin-utils/compare/v3.0.0...v4.0.0
[3.0.0]: https://github.com/cucumber/gherkin-utils/compare/v2.1.1...v3.0.0
[2.1.1]: https://github.com/cucumber/gherkin-utils/compare/v2.1.0...v2.1.1
[2.1.0]: https://github.com/cucumber/gherkin-utils/compare/v2.0.1...v2.1.0
[2.0.1]: https://github.com/cucumber/gherkin-utils/compare/v2.0.0...v2.0.1
[2.0.0]: https://github.com/cucumber/gherkin-utils/compare/v1.0.1...v2.0.0
[1.0.1]: https://github.com/cucumber/gherkin-utils/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/cucumber/gherkin-utils/compare/v1.0.0
