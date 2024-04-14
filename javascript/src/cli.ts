import { Command, Option } from 'commander'

import { version } from '../package.json'
import { formatCommand, FormatOptions } from './commands/formatCommand'

const program = new Command()
program.version(version)

// TODO: Default to running against the `features` directory
program
  .command('format')
  .arguments('[paths...]')
  .option(
    '--check',
    'Avoid writing any formatted files back; instead, exit with a non-zero status code if any files would have been modified, and zero otherwise'
  )
  .option(
    '--diff',
    'Avoid writing any formatted files back; instead, output a diff of changes that would be made and exit with a non-zero status code if any files would have been modified, and zero otherwise'
  )
  .option('-l, --language <ISO 639-1>', 'specify the language (dialect) of the source')
  .addOption(new Option('-f, --from-syntax <syntax>', 'from syntax').choices(['gherkin', 'markdown']))
  .addOption(new Option('-t, --to-syntax <syntax>', 'to syntax').choices(['gherkin', 'markdown'])
  )
  .description(
    `Formats one or more files or directories. STDIN is formatted and written to STDOUT (assuming gherkin syntax by default)`,
    {
      paths:
        'One or more .feature or .feature.md files, or directories containing such files',
    }
  )
  .action(async (files: string[], options: FormatOptions) => {
    await formatCommand(files, process.stdin.isTTY ? null : process.stdin, process.stdout, options)
  })

program.parse(process.argv)
