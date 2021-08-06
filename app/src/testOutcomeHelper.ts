import { TestOutcome } from '~/types/component-catalog-service'

export function getTestOutcomeVariant(testOutcome: TestOutcome) {
  switch (testOutcome) {
    case 'fail':
      return 'danger'
    case 'pass':
      return 'success'
    case 'not-applicable':
      return 'light'
    default:
      return 'success'
  }
}

export function getTestOutcomeText(testOutcome: TestOutcome) {
  switch (testOutcome) {
    case 'fail':
      return 'failing'
    case 'pass':
      return 'passing'
    case 'not-applicable':
      return 'not applicable'
  }
}

export function getTestOutcomeCountClass(testOutcome: TestOutcome) {
  switch (testOutcome) {
    case 'fail':
      return 'display-1'
    case 'pass':
      return 'display-4'
    case 'not-applicable':
      return 'display-4'
  }
}
