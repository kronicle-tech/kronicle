import { NuxtRuntimeConfig } from '@nuxt/types/config/runtime'
import { Route } from 'vue-router'
import {
  GetComponentCallGraphsResponse,
  GetComponentNodesResponse,
  GetComponentResponse,
} from '~/types/kronicle-service'

export interface ComponentAvailableData {
  readonly metadataTypes: string[]
  readonly stateTypes: string[]
  readonly hasCallGraphs: boolean
  readonly hasNodes: boolean
}

export async function fetchComponentAvailableData(
  $config: NuxtRuntimeConfig,
  route: Route
): Promise<ComponentAvailableData> {
  const component = await fetch(
    `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,crossFunctionalRequirements(fake),techDebts(fake),states(type),scannerErrors(fake),testResults(fake))`
  )
    .then((res) => res.json())
    .then((json) => (json as GetComponentResponse).component)

  const callGraphs = await fetch(
    `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}/call-graphs?fields=callGraphs(fake)`
  )
    .then((res) => res.json())
    .then((json) => (json as GetComponentCallGraphsResponse).callGraphs)

  const nodes = await fetch(
    `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}/nodes?fields=nodes(fake)`
  )
    .then((res) => res.json())
    .then((json) => (json as GetComponentNodesResponse).nodes)

  const metadataTypes: string[] = []
  checkMetadataType(
    metadataTypes,
    component.crossFunctionalRequirements,
    'cross-functional-requirement'
  )
  checkMetadataType(metadataTypes, component.techDebts, 'tech-debt')
  checkMetadataType(metadataTypes, component.scannerErrors, 'scanner-error')
  checkMetadataType(metadataTypes, component.testResults, 'test-result')

  const stateTypes = component.states.map((state) => state.type)

  return {
    metadataTypes,
    stateTypes,
    hasCallGraphs: callGraphs.length > 0,
    hasNodes: nodes.length > 0,
  }
}

function checkMetadataType(
  metadataTypes: string[],
  items: unknown[],
  metadataType: string
): void {
  if (items.length > 0) {
    metadataTypes.push(metadataType)
  }
}
