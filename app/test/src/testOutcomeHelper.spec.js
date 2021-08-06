import {
  getTestOutcomeCountClass,
  getTestOutcomeText,
  getTestOutcomeVariant,
} from '~/src/testOutcomeHelper'

describe('getTestOutcomeVariant', () => {
  const scenarios = [
    { testOutcome: 'fail', result: 'danger' },
    { testOutcome: 'pass', result: 'success' },
    { testOutcome: 'not-applicable', result: 'light' },
    { testOutcome: 'any other value', result: 'success' },
  ]

  scenarios.forEach((scenario) => {
    test(`when testOutcome is ${scenario.testOutcome}, then return value should be ${scenario.result}`, () => {
      const returnValue = getTestOutcomeVariant(scenario.testOutcome)
      expect(returnValue).toEqual(scenario.result)
    })
  })
})

describe('getTestOutcomeText', () => {
  const scenarios = [
    { testOutcome: 'fail', result: 'failing' },
    { testOutcome: 'pass', result: 'passing' },
    { testOutcome: 'not-applicable', result: 'not applicable' },
  ]

  scenarios.forEach((scenario) => {
    test(`when testOutcome is ${scenario.testOutcome}, then return value should be ${scenario.result}`, () => {
      const returnValue = getTestOutcomeText(scenario.testOutcome)
      expect(returnValue).toEqual(scenario.result)
    })
  })
})

describe('getTestOutcomeCountClass', () => {
  const scenarios = [
    { testOutcome: 'fail', result: 'display-1' },
    { testOutcome: 'pass', result: 'display-4' },
    { testOutcome: 'not-applicable', result: 'display-4' },
  ]

  scenarios.forEach((scenario) => {
    test(`when testOutcome is ${scenario.testOutcome}, then return value should be ${scenario.result}`, () => {
      const returnValue = getTestOutcomeCountClass(scenario.testOutcome)
      expect(returnValue).toEqual(scenario.result)
    })
  })
})
