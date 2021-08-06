import { compareObjectsWithComponents } from '~/src/objectWithComponentComparator'

describe('Object With Component Comparator', () => {
  test('two objects with no components should be equal', () => {
    const a = {
      test: 'test',
    }
    const b = {
      test: 'test',
    }
    const returnValue = compareObjectsWithComponents(a, b)
    expect(returnValue).toEqual(0)
  })

  test('object `a` with a component and object `b` without a component should be equal', () => {
    const a = {
      test: 'test',
      component: {
        name: 'Test Name A',
      },
    }
    const b = {
      test: 'test',
    }
    const returnValue = compareObjectsWithComponents(a, b)
    expect(returnValue).toEqual(0)
  })

  test('object `a` without a component and object `b` with a component should be equal', () => {
    const a = {
      test: 'test',
    }
    const b = {
      test: 'test',
      component: {
        name: 'Test Name B',
      },
    }
    const returnValue = compareObjectsWithComponents(a, b)
    expect(returnValue).toEqual(0)
  })

  test('two objects with the same component name should be equal', () => {
    const a = {
      test: 'test',
      component: {
        name: 'Test Name',
      },
    }
    const b = {
      test: 'test',
      component: {
        name: 'Test Name',
      },
    }
    const returnValue = compareObjectsWithComponents(a, b)
    expect(returnValue).toEqual(0)
  })

  test('component name that is alphabetically before other component name should be -1', () => {
    const a = {
      test: 'test',
      component: {
        name: 'Test Name 1',
      },
    }
    const b = {
      test: 'test',
      component: {
        name: 'Test Name 2',
      },
    }
    const returnValue = compareObjectsWithComponents(a, b)
    expect(returnValue).toEqual(-1)
  })

  test('component name that is alphabetically after other component name should be +1', () => {
    const a = {
      test: 'test',
      component: {
        name: 'Test Name 2',
      },
    }
    const b = {
      test: 'test',
      component: {
        name: 'Test Name 1',
      },
    }
    const returnValue = compareObjectsWithComponents(a, b)
    expect(returnValue).toEqual(1)
  })
})
