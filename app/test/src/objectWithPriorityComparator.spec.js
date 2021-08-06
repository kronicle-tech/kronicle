import { compareObjectsWithPriorities } from '~/src/objectWithPriorityComparator'
import { compareObjectsWithComponents } from '~/src/objectWithComponentComparator'

jest.mock('~/src/objectWithComponentComparator')

afterEach(() => {
  jest.clearAllMocks()
})

describe('Priority Comparator', () => {
  const priorityScenarios = [
    { a: 'very-high', b: 'very-high', result: 0 },
    { a: 'very-high', b: 'high', result: -1 },
    { a: 'very-high', b: 'medium', result: -1 },
    { a: 'very-high', b: 'low', result: -1 },
    { a: 'high', b: 'very-high', result: 1 },
    { a: 'high', b: 'high', result: 0 },
    { a: 'high', b: 'medium', result: -1 },
    { a: 'high', b: 'low', result: -1 },
    { a: 'medium', b: 'very-high', result: 1 },
    { a: 'medium', b: 'high', result: 1 },
    { a: 'medium', b: 'medium', result: 0 },
    { a: 'medium', b: 'low', result: -1 },
    { a: 'low', b: 'very-high', result: 1 },
    { a: 'low', b: 'high', result: 1 },
    { a: 'low', b: 'medium', result: 1 },
    { a: 'low', b: 'low', result: 0 },
  ]

  priorityScenarios.forEach((priorityScenario) => {
    if (priorityScenario.a === priorityScenario.b) {
      describe(`when priorities are both ${priorityScenario.a} then`, () => {
        const componentScenarios = [
          { result: -1 },
          { result: 0 },
          { result: 1 },
        ]
        componentScenarios.forEach((componentScenario) => {
          test(`should return ${componentScenario.result} when comparing components returns ${componentScenario.result}`, () => {
            compareObjectsWithComponents.mockReturnValue(
              componentScenario.result
            )
            const a = {
              priority: priorityScenario.a,
              anything: 'a',
            }
            const b = {
              priority: priorityScenario.b,
              anything: 'b',
            }
            const returnValue = compareObjectsWithPriorities(a, b)
            expect(returnValue).toEqual(componentScenario.result)
          })
        })
      })
    } else {
      test(`comparing ${priorityScenario.a} with ${priorityScenario.b} should return ${priorityScenario.result}`, () => {
        const a = {
          priority: priorityScenario.a,
        }
        const b = {
          priority: priorityScenario.b,
        }
        const returnValue = compareObjectsWithPriorities(a, b)
        expect(returnValue).toEqual(priorityScenario.result)
      })
    }
  })
})
