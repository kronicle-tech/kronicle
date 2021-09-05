import { TestOutcome } from '~/types/kronicle-service'

export interface ObjectWithTestOutcome {
  outcome: TestOutcome
}

export function compareTestOutcomes(
  a: ObjectWithTestOutcome,
  b: ObjectWithTestOutcome
) {
  if (a.outcome === 'fail' && b.outcome !== 'fail') {
    return -1
  } else if (a.outcome !== 'fail' && b.outcome === 'fail') {
    return 1
  } else if (a.outcome !== 'fail' && b.outcome !== 'fail') {
    if (a.outcome === 'pass' && b.outcome !== 'pass') {
      return -1
    } else if (a.outcome !== 'pass' && b.outcome === 'pass') {
      return 1
    }
  }

  return 0
}
