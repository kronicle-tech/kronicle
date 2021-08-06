import { Component, Priority } from '~/types/component-catalog-service'
import { compareObjectsWithComponents } from '~/src/objectWithComponentComparator'

export interface ObjectWithPriorityAndComponent {
  priority: Priority
  component?: Component
}

export function compareObjectsWithPriorities(
  a: ObjectWithPriorityAndComponent,
  b: ObjectWithPriorityAndComponent
) {
  if (a.priority === 'very-high' && b.priority !== 'very-high') {
    return -1
  } else if (a.priority !== 'very-high' && b.priority === 'very-high') {
    return 1
  } else if (a.priority === 'high' && b.priority !== 'high') {
    return -1
  } else if (a.priority !== 'high' && b.priority === 'high') {
    return 1
  } else if (a.priority === 'medium' && b.priority !== 'medium') {
    return -1
  } else if (a.priority !== 'medium' && b.priority === 'medium') {
    return 1
  }

  return compareObjectsWithComponents(a, b)
}
