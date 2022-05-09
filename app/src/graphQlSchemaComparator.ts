import { Component, GraphQlSchema } from '~/types/kronicle-service'
import { compareObjectsWithComponents } from '~/src/objectWithComponentComparator'

interface GraphQlSchemaWithComponent extends GraphQlSchema {
  component?: Component
}

export function compareGraphQlSchemas(
  a: GraphQlSchemaWithComponent,
  b: GraphQlSchemaWithComponent
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

function getLocation(graphQlSchema: GraphQlSchema) {
  return graphQlSchema.url ?? graphQlSchema.file
}
