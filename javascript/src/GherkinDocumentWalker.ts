// This file is DEPRECATED - use ./walkGherkinDocument instead
import type {
  Background,
  Feature,
  FeatureChild,
  GherkinDocument,
  Rule,
  RuleChild,
  Scenario,
  Step,
  Tag,
} from '@cucumber/messages'

export interface IFilters {
  acceptScenario?: (scenario: Scenario) => boolean
  acceptStep?: (step: Step) => boolean
  acceptBackground?: (background: Background) => boolean
  acceptRule?: (rule: Rule) => boolean
  acceptFeature?: (feature: Feature) => boolean
}

export interface IHandlers {
  handleStep?: (step: Step) => void
  handleScenario?: (scenario: Scenario) => void
  handleBackground?: (background: Background) => void
  handleRule?: (rule: Rule) => void
  handleFeature?: (feature: Feature) => void
}

const defaultFilters: IFilters = {
  acceptScenario: () => true,
  acceptStep: () => true,
  acceptBackground: () => true,
  acceptRule: () => true,
  acceptFeature: () => true,
}

export const rejectAllFilters: IFilters = {
  acceptScenario: () => false,
  acceptStep: () => false,
  acceptBackground: () => false,
  acceptRule: () => false,
  acceptFeature: () => false,
}

const defaultHandlers: IHandlers = {
  handleStep: () => null,
  handleScenario: () => null,
  handleBackground: () => null,
  handleRule: () => null,
  handleFeature: () => null,
}

export default class GherkinDocumentWalker {
  private readonly filters: IFilters
  private readonly handlers: IHandlers

  constructor(filters?: IFilters, handlers?: IHandlers) {
    this.filters = { ...defaultFilters, ...filters }
    this.handlers = { ...defaultHandlers, ...handlers }
  }

  public walkGherkinDocument(gherkinDocument: GherkinDocument): GherkinDocument | null {
    if (!gherkinDocument.feature) {
      return null
    }

    const feature = this.walkFeature(gherkinDocument.feature)

    if (!feature) {
      return null
    }

    return {
      feature,
      comments: gherkinDocument.comments,
      uri: gherkinDocument.uri,
    }
  }

  protected walkFeature(feature: Feature): Feature {
    const keptChildren = this.walkFeatureChildren(feature.children)

    this.handlers.handleFeature(feature)

    const backgroundKept = keptChildren.find((child) => child.background)

    if (this.filters.acceptFeature(feature) || backgroundKept) {
      return this.copyFeature(
        feature,
        feature.children.map((child) => {
          if (child.background) {
            return {
              background: this.copyBackground(child.background),
            }
          }
          if (child.scenario) {
            return {
              scenario: this.copyScenario(child.scenario),
            }
          }
          if (child.rule) {
            return {
              rule: this.copyRule(child.rule, child.rule.children),
            }
          }
          return child
        })
      )
    }

    if (keptChildren.find((child) => child !== null)) {
      return this.copyFeature(feature, keptChildren)
    }
  }

  private copyFeature(feature: Feature, children: FeatureChild[]): Feature {
    return {
      children: this.filterFeatureChildren(feature, children),
      location: feature.location,
      language: feature.language,
      keyword: feature.keyword,
      name: feature.name,
      description: feature.description,
      tags: this.copyTags(feature.tags),
    }
  }

  private copyTags(tags: readonly Tag[]): Tag[] {
    return tags.map((tag) => ({
      name: tag.name,
      id: tag.id,
      location: tag.location,
    }))
  }

  private filterFeatureChildren(feature: Feature, children: FeatureChild[]): FeatureChild[] {
    const copyChildren: FeatureChild[] = []

    const scenariosKeptById = new Map(
      children.filter((child) => child.scenario).map((child) => [child.scenario.id, child])
    )

    const ruleKeptById = new Map(
      children.filter((child) => child.rule).map((child) => [child.rule.id, child])
    )

    for (const child of feature.children) {
      if (child.background) {
        copyChildren.push({
          background: this.copyBackground(child.background),
        })
      }

      if (child.scenario) {
        const scenarioCopy = scenariosKeptById.get(child.scenario.id)
        if (scenarioCopy) {
          copyChildren.push(scenarioCopy)
        }
      }

      if (child.rule) {
        const ruleCopy = ruleKeptById.get(child.rule.id)
        if (ruleCopy) {
          copyChildren.push(ruleCopy)
        }
      }
    }
    return copyChildren
  }

  private walkFeatureChildren(children: readonly FeatureChild[]): FeatureChild[] {
    const childrenCopy: FeatureChild[] = []

    for (const child of children) {
      let backgroundCopy: Background = null
      let scenarioCopy: Scenario = null
      let ruleCopy: Rule = null

      if (child.background) {
        backgroundCopy = this.walkBackground(child.background)
      }
      if (child.scenario) {
        scenarioCopy = this.walkScenario(child.scenario)
      }
      if (child.rule) {
        ruleCopy = this.walkRule(child.rule)
      }

      if (backgroundCopy || scenarioCopy || ruleCopy) {
        childrenCopy.push({
          background: backgroundCopy,
          scenario: scenarioCopy,
          rule: ruleCopy,
        })
      }
    }

    return childrenCopy
  }

  protected walkRule(rule: Rule): Rule {
    const children = this.walkRuleChildren(rule.children)

    this.handlers.handleRule(rule)

    const backgroundKept = children.find((child) => child?.background)
    const scenariosKept = children.filter((child) => child?.scenario)

    if (this.filters.acceptRule(rule) || backgroundKept) {
      return this.copyRule(rule, rule.children)
    }
    if (scenariosKept.length > 0) {
      return this.copyRule(rule, scenariosKept)
    }
  }

  private copyRule(rule: Rule, children: readonly RuleChild[]): Rule {
    return {
      id: rule.id,
      name: rule.name,
      description: rule.description,
      location: rule.location,
      keyword: rule.keyword,
      children: this.filterRuleChildren(rule.children, children),
      tags: this.copyTags(rule.tags),
    }
  }

  private filterRuleChildren(
    children: readonly RuleChild[],
    childrenKept: readonly RuleChild[]
  ): RuleChild[] {
    const childrenCopy: RuleChild[] = []
    const scenariosKeptIds = childrenKept
      .filter((child) => child.scenario)
      .map((child) => child.scenario.id)

    for (const child of children) {
      if (child.background) {
        childrenCopy.push({
          background: this.copyBackground(child.background),
        })
      }
      if (child.scenario && scenariosKeptIds.includes(child.scenario.id)) {
        childrenCopy.push({
          scenario: this.copyScenario(child.scenario),
        })
      }
    }

    return childrenCopy
  }

  private walkRuleChildren(children: readonly RuleChild[]): RuleChild[] {
    const childrenCopy: RuleChild[] = []

    for (const child of children) {
      if (child.background) {
        childrenCopy.push({
          background: this.walkBackground(child.background),
        })
      }
      if (child.scenario) {
        childrenCopy.push({
          scenario: this.walkScenario(child.scenario),
        })
      }
    }
    return childrenCopy
  }

  protected walkBackground(background: Background): Background {
    const steps = this.walkAllSteps(background.steps)
    this.handlers.handleBackground(background)

    if (this.filters.acceptBackground(background) || steps.find((step) => step !== null)) {
      return this.copyBackground(background)
    }
  }

  private copyBackground(background: Background): Background {
    return {
      id: background.id,
      name: background.name,
      location: background.location,
      keyword: background.keyword,
      steps: background.steps.map((step) => this.copyStep(step)),
      description: background.description,
    }
  }

  protected walkScenario(scenario: Scenario): Scenario {
    const steps = this.walkAllSteps(scenario.steps)
    this.handlers.handleScenario(scenario)

    if (this.filters.acceptScenario(scenario) || steps.find((step) => step !== null)) {
      return this.copyScenario(scenario)
    }
  }

  private copyScenario(scenario: Scenario): Scenario {
    return {
      id: scenario.id,
      name: scenario.name,
      description: scenario.description,
      location: scenario.location,
      keyword: scenario.keyword,
      examples: scenario.examples,
      steps: scenario.steps.map((step) => this.copyStep(step)),
      tags: this.copyTags(scenario.tags),
    }
  }

  protected walkAllSteps(steps: readonly Step[]): Step[] {
    return steps.map((step) => this.walkStep(step))
  }

  protected walkStep(step: Step): Step {
    this.handlers.handleStep(step)
    if (!this.filters.acceptStep(step)) {
      return null
    }
    return this.copyStep(step)
  }

  private copyStep(step: Step): Step {
    return {
      id: step.id,
      keyword: step.keyword,
      keywordType: step.keywordType,
      location: step.location,
      text: step.text,
      dataTable: step.dataTable,
      docString: step.docString,
    }
  }
}
