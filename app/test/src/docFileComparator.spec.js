import { compareDocFiles } from '~/src/docFileComparator'

describe('Doc File Comparator', () => {
  test('path that is alphabetically before other path should be -1', () => {
    const a = {
      path: 'test-path-1',
    }
    const b = {
      path: 'test-path-2',
    }
    const returnValue = compareDocFiles(a, b)
    expect(returnValue).toEqual(-1)
  })

  test('path that is alphabetically after other path should be +1', () => {
    const a = {
      path: 'test-path-2',
    }
    const b = {
      path: 'test-path-1',
    }
    const returnValue = compareDocFiles(a, b)
    expect(returnValue).toEqual(1)
  })

  test('path that is alphabetically equal to other path should be zero', () => {
    const a = {
      path: 'test-path-1',
    }
    const b = {
      path: 'test-path-1',
    }
    const returnValue = compareDocFiles(a, b)
    expect(returnValue).toEqual(0)
  })
})
