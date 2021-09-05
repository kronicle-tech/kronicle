import { Component } from '~/types/kronicle-service'

export function compareComponents(a: Component, b: Component) {
  return a.name.localeCompare(b.name)
}
