import {
  AstBuilder,
  GherkinClassicTokenMatcher,
  type GherkinInMarkdownTokenMatcher,
  Parser,
} from '@cucumber/gherkin'
import { type GherkinDocument, IdGenerator } from '@cucumber/messages'

export default function parse(
  source: string,
  tokenMatcher:
    | GherkinClassicTokenMatcher
    | GherkinInMarkdownTokenMatcher = new GherkinClassicTokenMatcher()
): GherkinDocument {
  const newId = IdGenerator.uuid()
  const parser = new Parser(new AstBuilder(newId), tokenMatcher)
  try {
    const gherkinDocument = parser.parse(source)
    gherkinDocument.uri = ''
    return gherkinDocument
  } catch (err) {
    err.message += `\n${source}`
    throw err
  }
}
