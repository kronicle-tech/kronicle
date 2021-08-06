import { compareObjectsWithPriorities } from '~/src/objectWithPriorityComparator'
import { compareTestOutcomes } from '~/src/testOutcomeComparator'
import { compareTestResults } from '~/src/testResultComparator'

jest.mock('~/src/objectWithPriorityComparator')
jest.mock('~/src/testOutcomeComparator')

afterEach(() => {
  jest.clearAllMocks()
})

describe('Test Result Comparator', () => {
  test('should return result of comparing test outcomes when result is minus 1', () => {
    compareTestOutcomes.mockReturnValue(-1)

    const a = {
      anything: 'a',
    }
    const b = {
      anything: 'b',
    }

    const returnValue = compareTestResults(a, b)
    expect(returnValue).toEqual(-1)
    expectComparerCalledOnce(compareTestOutcomes, a, b)
    expectComparerNeverCalled(compareObjectsWithPriorities)
  })

  test('should return result of comparing test outcomes when result is plus 1', () => {
    compareTestOutcomes.mockReturnValue(1)

    const a = {
      anything: 'a',
    }
    const b = {
      anything: 'b',
    }

    const returnValue = compareTestResults(a, b)
    expect(returnValue).toEqual(1)
    expectComparerCalledOnce(compareTestOutcomes, a, b)
    expectComparerNeverCalled(compareObjectsWithPriorities)
  })

  test('should return result of comparing priorities when outcomes are the same and result of comparing priorities is minus 1', () => {
    compareTestOutcomes.mockReturnValue(0)
    compareObjectsWithPriorities.mockReturnValue(-1)

    const a = {
      anything: 'a',
    }
    const b = {
      anything: 'b',
    }

    const returnValue = compareTestResults(a, b)
    expect(returnValue).toEqual(-1)
    expectComparerCalledOnce(compareTestOutcomes, a, b)
    expectComparerCalledOnce(compareObjectsWithPriorities, a, b)
  })

  test('should return result of comparing priorities when outcomes are the same and result of comparing priorities is plus 1', () => {
    compareTestOutcomes.mockReturnValue(0)
    compareObjectsWithPriorities.mockReturnValue(1)

    const a = {
      anything: 'a',
    }
    const b = {
      anything: 'b',
    }

    const returnValue = compareTestResults(a, b)
    expect(returnValue).toEqual(1)
    expectComparerCalledOnce(compareTestOutcomes, a, b)
    expectComparerCalledOnce(compareObjectsWithPriorities, a, b)
  })

  test('should return result of comparing testIds when outcomes are the same and priorities are the same and result of comparing testIds is minus 1', () => {
    compareTestOutcomes.mockReturnValue(0)
    compareObjectsWithPriorities.mockReturnValue(0)

    const a = {
      testId: '1',
    }
    const b = {
      testId: '2',
    }

    const returnValue = compareTestResults(a, b)
    expect(returnValue).toEqual(-1)
    expectComparerCalledOnce(compareTestOutcomes, a, b)
    expectComparerCalledOnce(compareObjectsWithPriorities, a, b)
  })

  test('should return result of comparing testIds when outcomes are the same and priorities are the same and result of comparing testIds is plus 1', () => {
    compareTestOutcomes.mockReturnValue(0)
    compareObjectsWithPriorities.mockReturnValue(0)

    const a = {
      testId: '2',
    }
    const b = {
      testId: '1',
    }

    const returnValue = compareTestResults(a, b)
    expect(returnValue).toEqual(1)
    expectComparerCalledOnce(compareTestOutcomes, a, b)
    expectComparerCalledOnce(compareObjectsWithPriorities, a, b)
  })

  test('should return 0 when outcomes are the same and priorities are the same and testIds are the same', () => {
    compareTestOutcomes.mockReturnValue(0)
    compareObjectsWithPriorities.mockReturnValue(0)

    const a = {
      testId: '1',
    }
    const b = {
      testId: '1',
    }

    const returnValue = compareTestResults(a, b)
    expect(returnValue).toEqual(0)
    expectComparerCalledOnce(compareTestOutcomes, a, b)
    expectComparerCalledOnce(compareObjectsWithPriorities, a, b)
  })
})

function expectComparerCalledOnce(mockComparer, a, b) {
  expect(mockComparer.mock.calls).toHaveLength(1)
  expect(mockComparer.mock.calls[0][0]).toBe(a)
  expect(mockComparer.mock.calls[0][1]).toBe(b)
}

function expectComparerNeverCalled(mockComparer) {
  expect(mockComparer.mock.calls).toHaveLength(0)
}
