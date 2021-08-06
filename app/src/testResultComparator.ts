import { Priority, TestOutcome } from '~/types/component-catalog-service'
import { compareObjectsWithPriorities } from '~/src/objectWithPriorityComparator'
import { compareTestOutcomes } from '~/src/testOutcomeComparator'

export interface ComparableTestResult {
  testId: string
  priority: Priority
  outcome: TestOutcome
}

export function compareTestResults(
  a: ComparableTestResult,
  b: ComparableTestResult
) {
  const comparers = [
    compareTestOutcomes,
    compareObjectsWithPriorities,
    (a: ComparableTestResult, b: ComparableTestResult) =>
      a.testId.localeCompare(b.testId),
  ]

  let comparer = comparers.shift()

  while (comparer !== undefined) {
    const result = comparer(a, b)

    if (result !== 0) {
      return result
    }

    comparer = comparers.shift()
  }

  return 0
}
