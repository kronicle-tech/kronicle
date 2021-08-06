import {
  getPriorityCountClass,
  getPriorityText,
  getPriorityVariant,
} from '~/src/priorityHelper'

describe('getPriorityVariant', () => {
  const scenarios = [
    { priority: 'very-high', result: 'danger' },
    { priority: 'high', result: 'warning' },
    { priority: 'medium', result: 'info' },
    { priority: 'low', result: 'primary' },
    { priority: 'any other value', result: 'success' },
  ]

  scenarios.forEach((scenario) => {
    test(`when priority is ${scenario.priority}, then return value should be ${scenario.result}`, () => {
      const returnValue = getPriorityVariant(scenario.priority)
      expect(returnValue).toEqual(scenario.result)
    })
  })
})

describe('getPriorityText', () => {
  const scenarios = [
    { priority: 'very-high', result: 'Very High' },
    { priority: 'high', result: 'High' },
    { priority: 'medium', result: 'Medium' },
    { priority: 'low', result: 'Low' },
    { priority: 'any other value', result: 'Missing Priority' },
  ]

  scenarios.forEach((scenario) => {
    test(`when priority is ${scenario.priority}, then return value should be ${scenario.result}`, () => {
      const returnValue = getPriorityText(scenario.priority)
      expect(returnValue).toEqual(scenario.result)
    })
  })
})

describe('getPriorityCountClass', () => {
  const scenarios = [
    { priority: 'very-high', result: 'display-1' },
    { priority: 'high', result: 'display-2' },
    { priority: 'medium', result: 'display-3' },
    { priority: 'low', result: 'display-4' },
    { priority: 'any other value', result: 'display-4' },
  ]

  scenarios.forEach((scenario) => {
    test(`when priority is ${scenario.priority}, then return value should be ${scenario.result}`, () => {
      const returnValue = getPriorityCountClass(scenario.priority)
      expect(returnValue).toEqual(scenario.result)
    })
  })
})
