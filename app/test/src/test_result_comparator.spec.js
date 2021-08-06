import { compareTestResults } from '~/src/testResultComparator'

describe('Test Result Comparator', () => {
  test('fail should be equal to fail', () => {
    const a = {
      testId: 'test-test-id-1',
      outcome: 'fail',
    }
    const b = {
      testId: 'test-test-id-1',
      outcome: 'fail',
    }
    const returnValue = compareTestResults(a, b)
    expect(returnValue).toEqual(0)
  })

  test('fail should be less than pass', () => {
    const a = {
      testId: 'test-test-id-1',
      outcome: 'fail',
    }
    const b = {
      testId: 'test-test-id-1',
      outcome: 'pass',
    }
    const returnValue = compareTestResults(a, b)
    expect(returnValue).toEqual(-1)
  })

  test('fail should be less than not-applicable', () => {
    const a = {
      testId: 'test-test-id-1',
      outcome: 'fail',
    }
    const b = {
      testId: 'test-test-id-1',
      outcome: 'not-applicable',
    }
    const returnValue = compareTestResults(a, b)
    expect(returnValue).toEqual(-1)
  })

  test('pass should be greater than fail', () => {
    const a = {
      testId: 'test-test-id-1',
      outcome: 'pass',
    }
    const b = {
      testId: 'test-test-id-1',
      outcome: 'fail',
    }
    const returnValue = compareTestResults(a, b)
    expect(returnValue).toEqual(1)
  })

  test('not-applicable should be greater than fail', () => {
    const a = {
      testId: 'test-test-id-1',
      outcome: 'not-applicable',
    }
    const b = {
      testId: 'test-test-id-1',
      outcome: 'fail',
    }
    const returnValue = compareTestResults(a, b)
    expect(returnValue).toEqual(1)
  })

  test('pass should be equal to pass', () => {
    const a = {
      testId: 'test-test-id-1',
      outcome: 'pass',
    }
    const b = {
      testId: 'test-test-id-1',
      outcome: 'pass',
    }
    const returnValue = compareTestResults(a, b)
    expect(returnValue).toEqual(0)
  })

  test('pass should be less than not-applicable', () => {
    const a = {
      testId: 'test-test-id-1',
      outcome: 'pass',
    }
    const b = {
      testId: 'test-test-id-1',
      outcome: 'not-applicable',
    }
    const returnValue = compareTestResults(a, b)
    expect(returnValue).toEqual(-1)
  })

  test('not-applicable should be greater than pass', () => {
    const a = {
      testId: 'test-test-id-1',
      outcome: 'not-applicable',
    }
    const b = {
      testId: 'test-test-id-1',
      outcome: 'pass',
    }
    const returnValue = compareTestResults(a, b)
    expect(returnValue).toEqual(1)
  })

  test('not-applicable should be equal to not-applicable', () => {
    const a = {
      testId: 'test-test-id-1',
      outcome: 'not-applicable',
    }
    const b = {
      testId: 'test-test-id-1',
      outcome: 'not-applicable',
    }
    const returnValue = compareTestResults(a, b)
    expect(returnValue).toEqual(0)
  })

  test('testId should be compared when outcome is equal', () => {
    const a = {
      testId: 'test-test-id-1',
      outcome: 'fail',
    }
    const b = {
      testId: 'test-test-id-2',
      outcome: 'fail',
    }
    const returnValue = compareTestResults(a, b)
    expect(returnValue).toEqual(-1)
  })
})
