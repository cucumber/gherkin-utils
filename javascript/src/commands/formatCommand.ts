import { AstBuilder, GherkinClassicTokenMatcher, GherkinInMarkdownTokenMatcher, Parser, } from '@cucumber/gherkin'
import * as messages from '@cucumber/messages'
import * as diff from 'diff'
import fs, { unlink as unlinkCb } from 'fs'
import path from 'path'
import { Readable, Writable } from 'stream'
import { promisify } from 'util'

import pretty, { Syntax } from '../pretty'

const unlink = promisify(unlinkCb)

export type FormatOptions = {
  check?: boolean
  diff?: boolean
  fromSyntax?: Syntax
  toSyntax?: Syntax
  language?: string
}

type FileFormat = {
  readableSyntax: Syntax
  writableSyntax: Syntax
  readable: () => Readable
  writable: () => Writable
  afterWrite: () => Promise<void>
}

// TODO: Support `.md` files
// TODO: Support globs
// TODO: Ignore conversion failures
function getFeatureFiles(filePath: string): string[] {
  const stat = fs.statSync(filePath)
  let files: string[] = []

  if (stat.isFile() && path.extname(filePath) === '.feature') {
    files.push(filePath)
  } else if (stat.isDirectory()) {
    const dirFiles = fs.readdirSync(filePath)
    for (const file of dirFiles) {
      const fullPath = path.join(filePath, file)
      files = files.concat(getFeatureFiles(fullPath))
    }
  }

  return files
}

export async function formatCommand(
  paths: string[],
  stdin: Readable | null,
  stdout: Writable | null,
  options: FormatOptions
): Promise<void> {
  let files: string[] = []

  for (const filePath of paths) {
    files = files.concat(getFeatureFiles(filePath))
  }

  const fileFormats: FileFormat[] = files.map((file) => {
    const toFile = syntaxPath(file, options.toSyntax)
    return {
      readableSyntax: syntaxFromPath(file, options.fromSyntax),
      writableSyntax: syntaxFromPath(toFile, options.toSyntax),
      readable: () => fs.createReadStream(file),
      writable: () => fs.createWriteStream(toFile),
      afterWrite: file !== toFile ? () => unlink(file) : () => Promise.resolve()
    }
  })
  if (stdin && stdout) {
    fileFormats.push({
      readableSyntax: options.fromSyntax || 'gherkin',
      writableSyntax: options.toSyntax || 'gherkin',
      readable: () => stdin,
      writable: () => stdout,
      afterWrite: () => Promise.resolve()
    })
  }

  let unchangedCount = 0
  let reformatCount = 0
  let failuresCount = 0

  for (const fileFormat of fileFormats) {
    try {
      const wouldBeReformatted = await convert(fileFormat, options.language, options)
      wouldBeReformatted ? reformatCount++ : unchangedCount++
    } catch (error) {
      // TODO: Include file path
      failuresCount++
      console.error(`Failed to convert file: ${error.message}`)
    }
  }

  if (failuresCount > 0) {
    console.error(`❌ ${failuresCount} file${getPlural(failuresCount)} failed to format`)
  }
  if (unchangedCount > 0) {
    console.log(`🥒 ${unchangedCount} file${getPlural(unchangedCount)} left unchanged`)
  }
  if (reformatCount > 0) {
    const tense = options.check ? 'would be ' : ''
    console.log(`🥒 ${reformatCount} file${getPlural(reformatCount)} ${tense}reformatted`)
    process.exit(1)
  }
  if (failuresCount > 0) {
    process.exit(1)
  }
}

async function convert(fileFormat: FileFormat, language: string, options: FormatOptions) {
  const source = await read(fileFormat.readable())
  const gherkinDocument = parse(source, fileFormat.readableSyntax, language)
  const output = pretty(gherkinDocument, fileFormat.writableSyntax)
  try {
    // Sanity check that what we generated is OK.
    parse(output, fileFormat.writableSyntax, gherkinDocument.feature?.language)
  } catch (err) {
    err.message += `The generated output is not parseable. This is a bug.
Please report a bug at https://github.com/cucumber/gherkin/issues

--- Generated ${fileFormat.writableSyntax} source ---
${output}
------
`
    throw err
  }

  const reformatRequired = source !== output
  if (!reformatRequired) return false

  // TODO: fix: will prevent diff on `--check --diff`
  if (options.check) {
    return true
  }

  // TODO: Include absolute file path in diff
  if (options.diff) {
    const differences = diff.createPatch('filename.feature', source, output)
    console.log(differences)
    return true
  }

  const writable = fileFormat.writable()
  writable.write(output)
  writable.end()
  await new Promise((resolve) => writable.once('finish', resolve))
  await fileFormat.afterWrite()

  return reformatRequired
}

function parse(source: string, syntax: Syntax, language: string) {
  if (!syntax) throw new Error('No syntax')
  const fromParser = new Parser(
    new AstBuilder(messages.IdGenerator.uuid()),
    syntax === 'gherkin'
      ? new GherkinClassicTokenMatcher(language)
      : new GherkinInMarkdownTokenMatcher(language)
  )
  return fromParser.parse(source)
}

async function read(readable: Readable): Promise<string> {
  const chunks = []
  for await (const chunk of readable) chunks.push(chunk)
  return Buffer.concat(chunks).toString('utf-8')
}

function syntaxPath(file: string, syntax: Syntax): string {
  if (syntax === 'markdown') {
    if (syntaxFromPath(file) === 'markdown') return file
    return file + '.md'
  }

  if (syntax === 'gherkin') {
    if (syntaxFromPath(file) === 'gherkin') return file
    return file.replace(/\.feature\.md/, '.feature')
  }

  return file
}

function syntaxFromPath(file: string, explicitSyntax?: Syntax): Syntax {
  let syntax: Syntax
  if (path.extname(file) === '.feature') syntax = 'gherkin'
  if (path.extname(file) === '.md') syntax = 'markdown'
  if (!syntax) throw new Error(`Cannot determine syntax from path ${file}`)
  if (explicitSyntax && explicitSyntax !== syntax) throw new Error(`Cannot treat ${file} as ${explicitSyntax}`)
  return syntax
}

function getPlural(count: number): string {
  return count === 1 ? '' : 's'
}
