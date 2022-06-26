import { compareDocs } from '~/src/docComparator'

describe('Doc Comparator', () => {
  test('two objects with no components should be equal', () => {
    const a = {
      name: 'Test Name',
    }
    const b = {
      name: 'Test Name',
    }
    const returnValue = compareDocs(a, b)
    expect(returnValue).toEqual(0)
  })

  test('object `a` with a component and object `b` without a component should be equal', () => {
    const a = {
      name: 'Test Name',
      component: {
        name: 'Test Name A',
      },
    }
    const b = {
      name: 'Test Name',
    }
    const returnValue = compareDocs(a, b)
    expect(returnValue).toEqual(0)
  })

  test('object `a` without a component and object `b` with a component should be equal', () => {
    const a = {
      name: 'Test Name',
    }
    const b = {
      name: 'Test Name',
      component: {
        name: 'Test Name B',
      },
    }
    const returnValue = compareDocs(a, b)
    expect(returnValue).toEqual(0)
  })

  test('two objects with the same component name should be equal', () => {
    const a = {
      name: 'Test Name',
      component: {
        name: 'Test Name',
      },
    }
    const b = {
      name: 'Test Name',
      component: {
        name: 'Test Name',
      },
    }
    const returnValue = compareDocs(a, b)
    expect(returnValue).toEqual(0)
  })

  test('component name that is alphabetically before other component name should be -1', () => {
    const a = {
      name: 'Test Name',
      component: {
        name: 'Test Name 1',
      },
    }
    const b = {
      name: 'Test Name',
      component: {
        name: 'Test Name 2',
      },
    }
    const returnValue = compareDocs(a, b)
    expect(returnValue).toEqual(-1)
  })

  test('component name that is alphabetically after other component name should be +1', () => {
    const a = {
      name: 'Test Name',
      component: {
        name: 'Test Name 2',
      },
    }
    const b = {
      name: 'Test Name',
      component: {
        name: 'Test Name 1',
      },
    }
    const returnValue = compareDocs(a, b)
    expect(returnValue).toEqual(1)
  })

  test('objects should be compared by component name over doc name', () => {
    const a = {
      name: 'Test Doc Name 2',
      component: {
        name: 'Test Name 1',
      },
    }
    const b = {
      name: 'Test Doc Name 1',
      component: {
        name: 'Test Name 2',
      },
    }
    const returnValue = compareDocs(a, b)
    expect(returnValue).toEqual(-1)
  })

  test('objects should be equal when they have the same component name and name', () => {
    const a = {
      name: 'Test Doc Name 1',
      component: {
        name: 'Test Name 1',
      },
    }
    const b = {
      name: 'Test Doc Name 1',
      component: {
        name: 'Test Name 1',
      },
    }
    const returnValue = compareDocs(a, b)
    expect(returnValue).toEqual(0)
  })

  test('object `a` should be less than object `b` when they have the same component name but name of object `a` is alphabetically before name of object `b`', () => {
    const a = {
      name: 'Test Doc Name 1',
      component: {
        name: 'Test Name 1',
      },
    }
    const b = {
      name: 'Test Doc Name 2',
      component: {
        name: 'Test Name 1',
      },
    }
    const returnValue = compareDocs(a, b)
    expect(returnValue).toEqual(-1)
  })

  test('object `a` should be greater than object `b` when they have the same component name but name of object `a` is alphabetically after name of object `b`', () => {
    const a = {
      name: 'Test Doc Name 2',
      component: {
        name: 'Test Name 1',
      },
    }
    const b = {
      name: 'Test Doc Name 1',
      component: {
        name: 'Test Name 1',
      },
    }
    const returnValue = compareDocs(a, b)
    expect(returnValue).toEqual(1)
  })
})
