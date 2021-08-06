import { Component, OpenApiSpec } from '~/types/component-catalog-service'
import { compareObjectsWithComponents } from '~/src/objectWithComponentComparator'

interface OpenApiSpecWithComponent extends OpenApiSpec {
  component?: Component
}

export function compareOpenApiSpecs(
  a: OpenApiSpecWithComponent,
  b: OpenApiSpecWithComponent
) {
  const result = compareObjectsWithComponents(a, b)

  if (result !== 0) {
    return result
  }

  const locationA = getLocation(a)
  const locationB = getLocation(b)

  if (!locationA && !locationB) {
    return 0
  } else if (!locationA) {
    return 1
  } else if (!locationB) {
    return -1
  } else {
    return locationA.localeCompare(locationB)
  }
}

function getLocation(openApiSpec: OpenApiSpec) {
  return openApiSpec.url ?? openApiSpec.file
}
