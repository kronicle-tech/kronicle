import { Priority } from '~/types/component-catalog-service'

export function getPriorityVariant(priority: Priority) {
  switch (priority) {
    case 'very-high':
      return 'danger'
    case 'high':
      return 'warning'
    case 'medium':
      return 'info'
    case 'low':
      return 'primary'
    default:
      return 'success'
  }
}

export function getPriorityText(priority: Priority) {
  switch (priority) {
    case 'very-high':
      return 'Very High'
    case 'high':
      return 'High'
    case 'medium':
      return 'Medium'
    case 'low':
      return 'Low'
    default:
      return 'Missing Priority'
  }
}

export function getPriorityCountClass(priority: Priority) {
  switch (priority) {
    case 'very-high':
      return 'display-1'
    case 'high':
      return 'display-2'
    case 'medium':
      return 'display-3'
    case 'low':
      return 'display-4'
    default:
      return 'display-4'
  }
}
