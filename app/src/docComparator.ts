import { Component, Doc } from '~/types/kronicle-service'
import { compareObjectsWithComponents } from '~/src/objectWithComponentComparator'

interface DocWithComponent extends Doc {
  component?: Component
}

export function compareDocs(a: DocWithComponent, b: DocWithComponent) {
  const result = compareObjectsWithComponents(a, b)

  if (result !== 0) {
    return result
  }

  return a.name.localeCompare(b.name)
}
