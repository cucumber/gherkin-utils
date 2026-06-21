import type {
  Background,
  Envelope,
  Examples,
  Feature,
  GherkinDocument,
  Location,
  Pickle,
  Rule,
  Scenario,
  Source,
  Step,
} from '@cucumber/messages'
import { ArrayMultimap } from '@teppeis/multimaps'

export default class Query {
  private readonly sources: Source[] = []
  private readonly sourceByUri = new Map<string, Source>()
  private readonly gherkinDocuments: GherkinDocument[] = []
  private readonly pickles: Pickle[] = []
  private readonly locationByAstNodeId = new Map<string, Location>()
  private readonly gherkinStepByAstNodeId = new Map<string, Step>()
  private readonly pickleIdsMapByUri = new Map<string, ArrayMultimap<string, string>>()
  private readonly pickleIdsByAstNodeId = new Map<string, string[]>()
  private readonly pickleStepIdsByAstNodeId = new Map<string, string[]>()
  // AST nodes
  private readonly featureByUriLine = new Map<string, Feature>()
  private readonly backgroundByUriLine = new Map<string, Background>()
  private readonly ruleByUriLine = new Map<string, Rule>()
  private readonly scenarioByUriLine = new Map<string, Scenario>()
  private readonly examplesByUriLine = new Map<string, Examples>()
  private readonly stepByUriLine = new Map<string, Step>()

  /**
   * Gets the location (line and column) of an AST node.
   * @param astNodeId
   */
  public getLocation(astNodeId: string): Location {
    return this.locationByAstNodeId.get(astNodeId)
  }

  public getSources(): readonly Source[] {
    return this.sources
  }

  public getGherkinDocuments(): readonly GherkinDocument[] {
    return this.gherkinDocuments
  }

  public getPickles(): readonly Pickle[] {
    return this.pickles
  }

  public getSource(uri: string): Source | undefined {
    return this.sourceByUri.get(uri)
  }

  public getFeature(uri: string, line: number): Feature | undefined {
    return getAstNode(this.featureByUriLine, uri, line)
  }

  public getBackground(uri: string, line: number): Background | undefined {
    return getAstNode(this.backgroundByUriLine, uri, line)
  }

  public getRule(uri: string, line: number): Rule | undefined {
    return getAstNode(this.ruleByUriLine, uri, line)
  }

  public getScenario(uri: string, line: number): Scenario | undefined {
    return getAstNode(this.scenarioByUriLine, uri, line)
  }

  public getExamples(uri: string, line: number): Examples | undefined {
    return getAstNode(this.examplesByUriLine, uri, line)
  }

  public getStep(uri: string, line: number): Step | undefined {
    return getAstNode(this.stepByUriLine, uri, line)
  }

  /**
   * Gets all the pickle IDs
   * @param uri - the URI of the document
   * @param astNodeId - optionally restrict results to a particular AST node
   */
  public getPickleIds(uri: string, astNodeId?: string): readonly string[] {
    const pickleIdsByAstNodeId = this.pickleIdsMapByUri.get(uri)
    if (!pickleIdsByAstNodeId) {
      throw new Error(`No pickleIds for uri=${uri}`)
    }
    return astNodeId === undefined
      ? Array.from(new Set(pickleIdsByAstNodeId.values()))
      : pickleIdsByAstNodeId.get(astNodeId)
  }

  public getPickleStepIds(astNodeId: string): readonly string[] {
    return this.pickleStepIdsByAstNodeId.get(astNodeId) || []
  }

  public update(message: Envelope): Query {
    if (message.source) {
      this.sources.push(message.source)
      this.sourceByUri.set(message.source.uri, message.source)
    }

    if (message.gherkinDocument) {
      this.gherkinDocuments.push(message.gherkinDocument)

      if (message.gherkinDocument.feature) {
        this.updateGherkinFeature(message.gherkinDocument.uri, message.gherkinDocument.feature)
      }
    }

    if (message.pickle) {
      const pickle = message.pickle
      this.updatePickle(pickle)
    }

    return this
  }

  private updateGherkinFeature(uri: string, feature: Feature) {
    setAstNode(this.featureByUriLine, uri, feature)
    this.pickleIdsMapByUri.set(uri, new ArrayMultimap<string, string>())

    for (const featureChild of feature.children) {
      if (featureChild.background) {
        this.updateGherkinBackground(uri, featureChild.background)
      }

      if (featureChild.scenario) {
        this.updateGherkinScenario(uri, featureChild.scenario)
      }

      if (featureChild.rule) {
        this.updateGherkinRule(uri, featureChild.rule)
      }
    }
  }

  private updateGherkinBackground(uri: string, background: Background) {
    setAstNode(this.backgroundByUriLine, uri, background)
    for (const step of background.steps) {
      this.updateGherkinStep(uri, step)
    }
  }

  private updateGherkinRule(uri: string, rule: Rule) {
    setAstNode(this.ruleByUriLine, uri, rule)
    for (const ruleChild of rule.children) {
      if (ruleChild.background) {
        this.updateGherkinBackground(uri, ruleChild.background)
      }

      if (ruleChild.scenario) {
        this.updateGherkinScenario(uri, ruleChild.scenario)
      }
    }
  }

  private updateGherkinScenario(uri: string, scenario: Scenario) {
    setAstNode(this.scenarioByUriLine, uri, scenario)
    this.locationByAstNodeId.set(scenario.id, scenario.location)
    for (const step of scenario.steps) {
      this.updateGherkinStep(uri, step)
    }

    for (const examples of scenario.examples) {
      this.updateGherkinExamples(uri, examples)
    }
  }

  private updateGherkinExamples(uri: string, examples: Examples) {
    setAstNode(this.examplesByUriLine, uri, examples)
    for (const tableRow of examples.tableBody || []) {
      this.locationByAstNodeId.set(tableRow.id, tableRow.location)
    }
  }

  private updateGherkinStep(uri: string, step: Step) {
    setAstNode(this.stepByUriLine, uri, step)
    this.locationByAstNodeId.set(step.id, step.location)
    this.gherkinStepByAstNodeId.set(step.id, step)
  }

  private updatePickle(pickle: Pickle) {
    const pickleIdsByLineNumber = this.pickleIdsMapByUri.get(pickle.uri)

    for (const astNodeId of pickle.astNodeIds) {
      pickleIdsByLineNumber.put(astNodeId, pickle.id)
    }
    this.updatePickleSteps(pickle)
    this.pickles.push(pickle)

    for (const astNodeId of pickle.astNodeIds) {
      if (!this.pickleIdsByAstNodeId.has(astNodeId)) {
        this.pickleIdsByAstNodeId.set(astNodeId, [])
      }
      this.pickleIdsByAstNodeId.get(astNodeId).push(pickle.id)
    }
  }

  private updatePickleSteps(pickle: Pickle) {
    const pickleSteps = pickle.steps
    for (const pickleStep of pickleSteps) {
      for (const astNodeId of pickleStep.astNodeIds) {
        if (!this.pickleStepIdsByAstNodeId.has(astNodeId)) {
          this.pickleStepIdsByAstNodeId.set(astNodeId, [])
        }
        this.pickleStepIdsByAstNodeId.get(astNodeId).push(pickleStep.id)
      }
    }
  }
}

type HasLocation = {
  location: Location
}

function setAstNode<AstNode extends HasLocation>(
  map: Map<string, AstNode>,
  uri: string,
  astNode: AstNode
) {
  const line = astNode.location.line
  const uriLine = [uri, line].join(':')
  map.set(uriLine, astNode)
}

function getAstNode<AstNode>(
  map: Map<string, AstNode>,
  uri: string,
  line: number
): AstNode | undefined {
  const uriLine = [uri, line].join(':')
  return map.get(uriLine)
}
