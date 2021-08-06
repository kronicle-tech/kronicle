import { Component } from '~/types/component-catalog-service'

export interface ObjectWithComponent {
  component?: Component
}

export function compareObjectsWithComponents(
  a: ObjectWithComponent,
  b: ObjectWithComponent
) {
  if (a.component && b.component) {
    const result = a.component.name.localeCompare(b.component.name)

    if (result !== 0) {
      return result
    }
  }

  return 0
}
