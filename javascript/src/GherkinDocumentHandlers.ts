import type {
  Background,
  Comment,
  DataTable,
  DocString,
  Examples,
  Feature,
  Rule,
  Scenario,
  Step,
  TableCell,
  TableRow,
  Tag,
} from '@cucumber/messages'

export type GherkinDocumentHandlers<Acc> = {
  feature: (feature: Feature, acc: Acc) => Acc
  background: (background: Background, acc: Acc) => Acc
  rule: (rule: Rule, acc: Acc) => Acc
  scenario: (scenario: Scenario, acc: Acc) => Acc
  step: (step: Step, acc: Acc) => Acc
  examples: (examples: Examples, acc: Acc) => Acc
  tag: (tag: Tag, acc: Acc) => Acc
  comment: (comment: Comment, acc: Acc) => Acc
  dataTable: (dataTable: DataTable, acc: Acc) => Acc
  tableRow: (tableRow: TableRow, acc: Acc) => Acc
  tableCell: (tableCell: TableCell, acc: Acc) => Acc
  docString: (docString: DocString, acc: Acc) => Acc
}
