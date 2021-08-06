import { distinctArrayElements, intRange, itemCounts } from '~/src/arrayUtils'

describe('distinctArrayElements', () => {
  test('when an array is empty, then an empty array is returned', () => {
    const returnValue = distinctArrayElements([])
    expect(returnValue).toEqual([])
  })

  test('when an array does not contain any duplicate elements, then the array is sorted and returned with all the existing elements', () => {
    const returnValue = distinctArrayElements(['b', 'a', 'c'])
    expect(returnValue).toEqual(['a', 'b', 'c'])
  })

  test('when an array does contain duplicate elements, then the array elements are de-duplicated, sorted and returned', () => {
    const returnValue = distinctArrayElements(['b', 'b', 'c', 'a', 'c'])
    expect(returnValue).toEqual(['a', 'b', 'c'])
  })

  test('the array is copied and not mutated', () => {
    const originalArray = ['b', 'a', 'c']
    const returnValue = distinctArrayElements(originalArray)
    expect(returnValue).toEqual(['a', 'b', 'c'])
    // Test that originalArray has not been modified
    expect(originalArray).toEqual(['b', 'a', 'c'])
  })
})

describe('intRange', () => {
  test('returns an array of integers from startInclusive (inclusive) to endExclusive (exclusive)', () => {
    const returnValue = intRange(0, 3)
    expect(returnValue).toEqual([0, 1, 2])
  })

  test('when startInclusive and endExclusive are equal, returns an empty array', () => {
    const returnValue = intRange(0, 0)
    expect(returnValue).toEqual([])
  })

  test('when endExclusive is less than startInclusive, returns an empty array', () => {
    const returnValue = intRange(1, 0)
    expect(returnValue).toEqual([])
  })
})

describe('itemCounts', () => {
  test('when array is empty, returns an empty array', () => {
    const returnValue = itemCounts([])
    expect(returnValue).toEqual([])
  })

  test('when array contains a single item, returns an array with single item with a count of 1', () => {
    const returnValue = itemCounts(['value1'])
    expect(returnValue).toEqual([{ item: 'value1', count: 1 }])
  })

  test('when array contains the same item twice, returns an array with single item with a count of 2', () => {
    const returnValue = itemCounts(['value1', 'value1'])
    expect(returnValue).toEqual([{ item: 'value1', count: 2 }])
  })

  test('when array contains the multiple different items, some of which are duplicates, returns an array with appropriate counts, sorted by count in descending order', () => {
    const returnValue = itemCounts([
      'value1',
      'value1',
      'value2',
      'value2',
      'value2',
      'value3',
    ])
    expect(returnValue).toEqual([
      { item: 'value2', count: 3 },
      { item: 'value1', count: 2 },
      { item: 'value3', count: 1 },
    ])
  })

  test('item counts are sorted by count in descending order, then by item in ascending order', () => {
    const returnValue = itemCounts([
      'value1b',
      'value1a',
      'value2',
      'value2',
      'value2',
    ])
    expect(returnValue).toEqual([
      { item: 'value2', count: 3 },
      { item: 'value1a', count: 1 },
      { item: 'value1b', count: 1 },
    ])
  })
})
