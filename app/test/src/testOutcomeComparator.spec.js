import { compareTestOutcomes } from '~/src/testOutcomeComparator'

describe('Test Outcome Comparator', () => {
  const scenarios = [
    { a: 'fail', b: 'fail', result: 0 },
    { a: 'fail', b: 'pass', result: -1 },
    { a: 'fail', b: 'not-applicable', result: -1 },
    { a: 'pass', b: 'fail', result: 1 },
    { a: 'pass', b: 'pass', result: 0 },
    { a: 'pass', b: 'not-applicable', result: -1 },
    { a: 'not-applicable', b: 'fail', result: 1 },
    { a: 'not-applicable', b: 'pass', result: 1 },
    { a: 'not-applicable', b: 'not-applicable', result: 0 },
  ]

  scenarios.forEach((scenario) => {
    test(`comparing ${scenario.a} with ${scenario.b} should return ${scenario.result}`, () => {
      const a = {
        outcome: scenario.a,
      }
      const b = {
        outcome: scenario.b,
      }
      const returnValue = compareTestOutcomes(a, b)
      expect(returnValue).toEqual(scenario.result)
    })
  })
})
