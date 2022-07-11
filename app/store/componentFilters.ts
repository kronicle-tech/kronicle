import { Module, Mutation, VuexModule } from 'vuex-module-decorators'
import { Route } from 'vue-router'
import {
  Component,
  ComponentEnvironmentState,
  ComponentState,
} from '~/types/kronicle-service'

// TODO: This module can be simplified when https://github.com/championswimmer/vuex-module-decorators/issues/335 is fixed

interface ReadOnlyState {
  components: Component[]
  environmentIds: string[]
  pluginIds: string[]
  testOutcomes: string[]
  teamIds: string[]
  componentTypes: string[]
  tags: string[]
  platformIds: string[]
  componentId: string | undefined
}

function updateQuery(state: ReadOnlyState) {
  const url = new URL(document.location.href)
  addQueryParams(url, 'environmentId', state.environmentIds)
  addQueryParams(url, 'pluginId', state.pluginIds)
  addQueryParams(url, 'testOutcome', state.testOutcomes)
  addQueryParams(url, 'teamId', state.teamIds)
  addQueryParams(url, 'componentType', state.componentTypes)
  addQueryParams(url, 'tag', state.tags)
  addQueryParams(url, 'platformId', state.platformIds)
  addQueryParams(url, 'componentId', state.componentId)
  history.pushState(undefined, '', url.href)
}

function addQueryParams(
  url: URL,
  name: string,
  values: string | string[] | undefined
) {
  url.searchParams.delete(name)
  if (values) {
    if (Array.isArray(values)) {
      values.forEach((value) => url.searchParams.append(name, value))
    } else {
      url.searchParams.append(name, values)
    }
  }
}

function isComponentEnvironmentState(
  componentState: ComponentState
): componentState is ComponentEnvironmentState {
  return 'environmentId' in componentState
}

function getFilteredComponents(state: ReadOnlyState): Component[] {
  if (!state.components) {
    return []
  }

  if (state.componentId) {
    return state.components.filter(
      (component) => component.id === state.componentId
    )
  }

  let filteredComponents = state.components

  if (state.environmentIds.length > 0) {
    filteredComponents = filteredComponents
      .map((component) => {
        if (!component.states) {
          return undefined
        }

        const filteredStates = component.states.filter(
          (componentState) =>
            !isComponentEnvironmentState(componentState) ||
            state.environmentIds.includes(componentState.environmentId)
        )

        if (filteredStates.length === 0) {
          return undefined
        } else {
          const componentDeepClone = JSON.parse(
            JSON.stringify(component)
          ) as Component
          componentDeepClone.states = filteredStates
          return componentDeepClone
        }
      })
      .filter((component) => component !== undefined) as Component[]
  }

  if (state.pluginIds.length > 0) {
    filteredComponents = filteredComponents
      .map((component) => {
        if (!component.states) {
          return undefined
        }

        const filteredStates = component.states.filter(
          (componentState) =>
            !isComponentEnvironmentState(componentState) ||
            state.pluginIds.includes(componentState.pluginId)
        )

        if (filteredStates.length === 0) {
          return undefined
        } else {
          const componentDeepClone = JSON.parse(
            JSON.stringify(component)
          ) as Component
          componentDeepClone.states = filteredStates
          return componentDeepClone
        }
      })
      .filter((component) => component !== undefined) as Component[]
  }

  if (state.testOutcomes.length > 0) {
    filteredComponents = filteredComponents
      .map((component) => {
        if (!component.testResults) {
          return undefined
        }

        const filteredTestResults = component.testResults.filter((testResult) =>
          state.testOutcomes.includes(testResult.outcome)
        )

        if (filteredTestResults.length === 0) {
          return undefined
        } else if (
          filteredTestResults.length === component.testResults.length
        ) {
          return component
        } else {
          const componentDeepClone = JSON.parse(
            JSON.stringify(component)
          ) as Component
          componentDeepClone.testResults = filteredTestResults
          return componentDeepClone
        }
      })
      .filter((component) => component !== undefined) as Component[]
  }

  if (state.teamIds.length > 0) {
    filteredComponents = filteredComponents.filter((component) =>
      component.teams?.some(
        (componentTeam) =>
          componentTeam.type !== 'previous' &&
          state.teamIds.includes(componentTeam.teamId)
      )
    )
  }

  if (state.componentTypes.length > 0) {
    filteredComponents = filteredComponents.filter((component) =>
      state.componentTypes.includes(component.type)
    )
  }

  if (state.tags.length > 0) {
    filteredComponents = filteredComponents.filter((component) =>
      component.tags?.some((tag) => state.tags.includes(tag.key))
    )
  }

  if (state.platformIds.length > 0) {
    filteredComponents = filteredComponents.filter((component) =>
      state.platformIds.includes(component.platformId || 'undefined')
    )
  }

  return filteredComponents
}

function getComponentIds(components: Component[]) {
  return components.map((component) => component.id)
}

function ensureQueryParamValueIsAStringArray(
  value: string | (string | null)[]
): string[] {
  if (!value) {
    return []
  } else if (Array.isArray(value)) {
    return value.filter((element): element is string => element !== null)
  } else {
    return [value]
  }
}

function ensureQueryParamValueIsAString(
  value: string | (string | null)[]
): string | undefined {
  if (!value) {
    return undefined
  } else if (Array.isArray(value)) {
    const values = value.filter(
      (element): element is string => element !== null
    )
    return values.length > 0 ? values[0] : undefined
  } else {
    return value
  }
}

@Module({
  name: 'componentFilters',
  stateFactory: true,
  namespaced: true,
})
export default class ComponentFilters extends VuexModule {
  components = [] as Component[]
  environmentIds = [] as string[]
  pluginIds = [] as string[]
  testOutcomes = [] as string[]
  teamIds = [] as string[]
  componentTypes = [] as string[]
  tags = [] as string[]
  platformIds = [] as string[]
  componentId = undefined as string | undefined
  filteredComponents = [] as Component[]
  filteredComponentIds = [] as string[]

  @Mutation
  initialize({ components, route }: { components: Component[]; route: Route }) {
    this.components = components
    const query = route.query
    this.environmentIds = ensureQueryParamValueIsAStringArray(
      query.environmentId
    )
    this.pluginIds = ensureQueryParamValueIsAStringArray(query.pluginId)
    this.testOutcomes = ensureQueryParamValueIsAStringArray(query.testOutcome)
    this.teamIds = ensureQueryParamValueIsAStringArray(query.teamId)
    this.componentTypes = ensureQueryParamValueIsAStringArray(
      query.componentType
    )
    this.tags = ensureQueryParamValueIsAStringArray(query.tag)
    this.platformIds = ensureQueryParamValueIsAStringArray(query.platformId)
    this.componentId = ensureQueryParamValueIsAString(query.componentId)

    const state = {
      components: this.components,
      environmentIds: this.environmentIds,
      pluginIds: this.pluginIds,
      testOutcomes: this.testOutcomes,
      teamIds: this.teamIds,
      componentTypes: this.componentTypes,
      tags: this.tags,
      platformIds: this.platformIds,
      componentId: this.componentId,
    }
    this.filteredComponents = getFilteredComponents(state)
    this.filteredComponentIds = getComponentIds(this.filteredComponents)
  }

  @Mutation
  setEnvironmentIds(value: string[]) {
    this.environmentIds = value
    const state = {
      components: this.components,
      environmentIds: this.environmentIds,
      pluginIds: this.pluginIds,
      testOutcomes: this.testOutcomes,
      teamIds: this.teamIds,
      componentTypes: this.componentTypes,
      tags: this.tags,
      platformIds: this.platformIds,
      componentId: this.componentId,
    }
    updateQuery(state)
    this.filteredComponents = getFilteredComponents(state)
    this.filteredComponentIds = getComponentIds(this.filteredComponents)
  }

  @Mutation
  setPluginIds(value: string[]) {
    this.pluginIds = value
    const state = {
      components: this.components,
      environmentIds: this.environmentIds,
      pluginIds: this.pluginIds,
      testOutcomes: this.testOutcomes,
      teamIds: this.teamIds,
      componentTypes: this.componentTypes,
      tags: this.tags,
      platformIds: this.platformIds,
      componentId: this.componentId,
    }
    updateQuery(state)
    this.filteredComponents = getFilteredComponents(state)
    this.filteredComponentIds = getComponentIds(this.filteredComponents)
  }

  @Mutation
  setTestOutcomes(value: string[]) {
    this.testOutcomes = value
    const state = {
      components: this.components,
      environmentIds: this.environmentIds,
      pluginIds: this.pluginIds,
      testOutcomes: this.testOutcomes,
      teamIds: this.teamIds,
      componentTypes: this.componentTypes,
      tags: this.tags,
      platformIds: this.platformIds,
      componentId: this.componentId,
    }
    updateQuery(state)
    this.filteredComponents = getFilteredComponents(state)
    this.filteredComponentIds = getComponentIds(this.filteredComponents)
  }

  @Mutation
  setTeamIds(value: string[]) {
    this.teamIds = value
    const state = {
      components: this.components,
      environmentIds: this.environmentIds,
      pluginIds: this.pluginIds,
      testOutcomes: this.testOutcomes,
      teamIds: this.teamIds,
      componentTypes: this.componentTypes,
      tags: this.tags,
      platformIds: this.platformIds,
      componentId: this.componentId,
    }
    updateQuery(state)
    this.filteredComponents = getFilteredComponents(state)
    this.filteredComponentIds = getComponentIds(this.filteredComponents)
  }

  @Mutation
  setComponentTypes(value: string[]) {
    this.componentTypes = value
    const state = {
      components: this.components,
      environmentIds: this.environmentIds,
      pluginIds: this.pluginIds,
      testOutcomes: this.testOutcomes,
      teamIds: this.teamIds,
      componentTypes: this.componentTypes,
      tags: this.tags,
      platformIds: this.platformIds,
      componentId: this.componentId,
    }
    updateQuery(state)
    this.filteredComponents = getFilteredComponents(state)
    this.filteredComponentIds = getComponentIds(this.filteredComponents)
  }

  @Mutation
  setTags(value: string[]) {
    this.tags = value
    const state = {
      components: this.components,
      environmentIds: this.environmentIds,
      pluginIds: this.pluginIds,
      testOutcomes: this.testOutcomes,
      teamIds: this.teamIds,
      componentTypes: this.componentTypes,
      tags: this.tags,
      platformIds: this.platformIds,
      componentId: this.componentId,
    }
    updateQuery(state)
    this.filteredComponents = getFilteredComponents(state)
    this.filteredComponentIds = getComponentIds(this.filteredComponents)
  }

  @Mutation
  setPlatformIds(value: string[]) {
    this.platformIds = value
    const state = {
      components: this.components,
      environmentIds: this.environmentIds,
      pluginIds: this.pluginIds,
      testOutcomes: this.testOutcomes,
      teamIds: this.teamIds,
      componentTypes: this.componentTypes,
      tags: this.tags,
      platformIds: this.platformIds,
      componentId: this.componentId,
    }
    updateQuery(state)
    this.filteredComponents = getFilteredComponents(state)
    this.filteredComponentIds = getComponentIds(this.filteredComponents)
  }

  @Mutation
  setComponentId(value: string | undefined) {
    this.componentId = value
    const state = {
      components: this.components,
      environmentIds: this.environmentIds,
      pluginIds: this.pluginIds,
      testOutcomes: this.testOutcomes,
      teamIds: this.teamIds,
      componentTypes: this.componentTypes,
      tags: this.tags,
      platformIds: this.platformIds,
      componentId: this.componentId,
    }
    updateQuery(state)
    this.filteredComponents = getFilteredComponents(state)
    this.filteredComponentIds = getComponentIds(this.filteredComponents)
  }
}
