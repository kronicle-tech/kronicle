import { NuxtRuntimeConfig } from '@nuxt/types/config/runtime'
import { Route } from 'vue-router'
import { NuxtError } from '@nuxt/types'
import { GetComponentResponse } from '~/types/kronicle-service'

export interface ComponentAvailableData {
  readonly metadataTypes: string[]
  readonly stateTypes: string[]
  readonly hasCallGraphs: boolean
  readonly hasNodes: boolean
}

export async function fetchComponentAvailableData(
  $config: NuxtRuntimeConfig,
  route: Route,
  error: (params: NuxtError) => void
): Promise<ComponentAvailableData | undefined> {
  const component = await fetch(
    `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,crossFunctionalRequirements(fake),techDebts(fake),states(type),scannerErrors(fake),testResults(fake))`
  )
    .then((res) => res.json())
    .then((json) => (json as GetComponentResponse).component)

  if (!component) {
    error({
      message: 'Component not found',
      statusCode: 404,
    })
    return undefined
  }

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
    hasCallGraphs: false,
    hasNodes: false,
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
