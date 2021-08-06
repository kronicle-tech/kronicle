import { Component } from '~/types/component-catalog-service'

export function compareComponents(a: Component, b: Component) {
  return a.name.localeCompare(b.name)
}
