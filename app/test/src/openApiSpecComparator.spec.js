import { compareOpenApiSpecs } from '~/src/openApiSpecComparator'

describe('OpenAPI Spec Comparator', () => {
  test('two objects with no components should be equal', () => {
    const a = {
      test: 'test',
    }
    const b = {
      test: 'test',
    }
    const returnValue = compareOpenApiSpecs(a, b)
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
    const returnValue = compareOpenApiSpecs(a, b)
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
    const returnValue = compareOpenApiSpecs(a, b)
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
    const returnValue = compareOpenApiSpecs(a, b)
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
    const returnValue = compareOpenApiSpecs(a, b)
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
    const returnValue = compareOpenApiSpecs(a, b)
    expect(returnValue).toEqual(1)
  })

  test('objects should be compared by component name over OpenAPI spec location', () => {
    const a = {
      url: 'https://example.com/openapi-spec-2',
      component: {
        name: 'Test Name 1',
      },
    }
    const b = {
      url: 'https://example.com/openapi-spec-1',
      component: {
        name: 'Test Name 2',
      },
    }
    const returnValue = compareOpenApiSpecs(a, b)
    expect(returnValue).toEqual(-1)
  })

  test('objects with same component name and no location should be equal', () => {
    const a = {
      component: {
        name: 'Test Name 1',
      },
    }
    const b = {
      component: {
        name: 'Test Name 1',
      },
    }
    const returnValue = compareOpenApiSpecs(a, b)
    expect(returnValue).toEqual(0)
  })

  test('object `a` should be greater than object `b` when object `a` has no location and object `b` does have a location', () => {
    const a = {
      component: {
        name: 'Test Name 1',
      },
    }
    const b = {
      url: 'https://example.com/openapi-spec-1',
      component: {
        name: 'Test Name 1',
      },
    }
    const returnValue = compareOpenApiSpecs(a, b)
    expect(returnValue).toEqual(1)
  })

  test('object `a` should be less than object `b` when object `a` has a location and object `b` does not have a location', () => {
    const a = {
      url: 'https://example.com/openapi-spec-1',
      component: {
        name: 'Test Name 1',
      },
    }
    const b = {
      component: {
        name: 'Test Name 1',
      },
    }
    const returnValue = compareOpenApiSpecs(a, b)
    expect(returnValue).toEqual(-1)
  })

  test('objects should be equal when they have the same component name and location', () => {
    const a = {
      url: 'https://example.com/openapi-spec-1',
      component: {
        name: 'Test Name 1',
      },
    }
    const b = {
      url: 'https://example.com/openapi-spec-1',
      component: {
        name: 'Test Name 1',
      },
    }
    const returnValue = compareOpenApiSpecs(a, b)
    expect(returnValue).toEqual(0)
  })

  test('object `a` should be less than object `b` when they have the same component name but location of object `a` is alphabetically before location of object `b`', () => {
    const a = {
      url: 'https://example.com/openapi-spec-1',
      component: {
        name: 'Test Name 1',
      },
    }
    const b = {
      url: 'https://example.com/openapi-spec-2',
      component: {
        name: 'Test Name 1',
      },
    }
    const returnValue = compareOpenApiSpecs(a, b)
    expect(returnValue).toEqual(-1)
  })

  test('object `a` should be greater than object `b` when they have the same component name but location of object `a` is alphabetically after location of object `b`', () => {
    const a = {
      url: 'https://example.com/openapi-spec-2',
      component: {
        name: 'Test Name 1',
      },
    }
    const b = {
      url: 'https://example.com/openapi-spec-1',
      component: {
        name: 'Test Name 1',
      },
    }
    const returnValue = compareOpenApiSpecs(a, b)
    expect(returnValue).toEqual(1)
  })

  test('url and file locations should be treated as the same', () => {
    const a = {
      url: 'https://example.com/openapi-spec-1',
      component: {
        name: 'Test Name 1',
      },
    }
    const b = {
      file: 'openapi-spec-1.yaml',
      component: {
        name: 'Test Name 1',
      },
    }
    const returnValue = compareOpenApiSpecs(a, b)
    expect(returnValue).toEqual(-1)
  })
})
