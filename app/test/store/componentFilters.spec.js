jest.spyOn(window.history, 'pushState')

afterEach(() => {
  jest.clearAllMocks()
})

describe('Component Filters Store', () => {
  let store

  beforeEach(async () => {
    const Store = await import('~/.nuxt/store.js')
    store = Store.createStore()
  })

  function createRoute(query) {
    return {
      query: query || {},
    }
  }

  function getComponentIds(components) {
    return components.map((component) => component.id)
  }

  function deepClone(value) {
    if (value === undefined) {
      return undefined
    }
    return JSON.parse(JSON.stringify(value))
  }

  function expectFilteredComponentsToEqual(expectedFilteredComponents) {
    expect(store.state.componentFilters.filteredComponents).toEqual(
      expectedFilteredComponents
    )
    expect(store.state.componentFilters.filteredComponentIds).toEqual(
      getComponentIds(expectedFilteredComponents)
    )
  }

  function expectFiltersToEqual(expectedFilters) {
    const filterNames = [
      'testOutcomes',
      'teamIds',
      'componentTypeIds',
      'tags',
      'platformIds',
      'componentId',
    ]
    Object.keys(expectedFilters).forEach((name) => {
      if (!filterNames.includes(name)) {
        throw new Error(`Unknown filter name ${name}`)
      }
    })
    filterNames.forEach((name) =>
      expect(store.state.componentFilters[name]).toEqual(
        expectedFilters[name] || (name === 'componentId' ? undefined : [])
      )
    )
  }

  function whenInitializing({
    givenComponents,
    givenQuery,
    expectedFilteredComponents,
    expectedFilters,
  }) {
    store.commit('componentFilters/initialize', {
      components: deepClone(givenComponents),
      route: createRoute(givenQuery),
    })
    if (givenQuery !== undefined) {
      expectFiltersToEqual(expectedFilters)
    }
    expectFilteredComponentsToEqual(expectedFilteredComponents)
  }

  function capitaliseFirstLetter(value) {
    return value[0].toUpperCase() + value.slice(1)
  }

  function whenSettingFilters({
    givenComponents,
    givenFilters,
    expectedFilteredComponents,
    expectedQueryStrings,
  }) {
    whenInitializing({
      givenComponents,
      expectedFilteredComponents: givenComponents,
    })
    givenFilters.forEach((givenFilter) => {
      store.commit(
        `componentFilters/set${capitaliseFirstLetter(givenFilter.name)}`,
        deepClone(givenFilter.value)
      )
      expect(store.state.componentFilters[givenFilter.name]).toEqual(
        givenFilter.value
      )
    })
    expectFilteredComponentsToEqual(expectedFilteredComponents)
    expect(global.history.pushState.mock.calls).toEqual(
      expectedQueryStrings.map((expectedQueryString) => [
        undefined,
        '',
        `http://localhost/${
          expectedQueryString ? '?' + expectedQueryString : ''
        }`,
      ])
    )
  }

  function whenSettingAFilter({
    givenComponents,
    name,
    givenValue,
    expectedFilteredComponents,
    expectedQueryString,
  }) {
    whenSettingFilters({
      givenComponents,
      givenFilters: [
        {
          name,
          value: givenValue,
        },
      ],
      expectedFilteredComponents,
      expectedQueryStrings: [expectedQueryString],
    })
  }

  function whenSettingTestOutcomes(options) {
    whenSettingAFilter({ ...options, name: 'testOutcomes' })
  }

  function whenSettingTeamIds(options) {
    whenSettingAFilter({ ...options, name: 'teamIds' })
  }

  function whenSettingComponentTypeIds(options) {
    whenSettingAFilter({ ...options, name: 'componentTypeIds' })
  }

  function whenSettingTags(options) {
    whenSettingAFilter({ ...options, name: 'tags' })
  }

  function whenSettingPlatformIds(options) {
    whenSettingAFilter({ ...options, name: 'platformIds' })
  }

  function whenSettingComponentId(options) {
    whenSettingAFilter({ ...options, name: 'componentId' })
  }

  test('when components is set to undefined, filteredComponents and filteredComponentIds should return no components', () => {
    whenInitializing({
      givenComponents: undefined,
      expectedFilteredComponents: [],
      expectedQueryString: undefined,
    })
  })

  test('when components is set to an empty array, filteredComponents and filteredComponentIds should return no components', () => {
    whenInitializing({
      givenComponents: [],
      expectedFilteredComponents: [],
      expectedQueryString: undefined,
    })
  })

  test('when components is set to an array of components, filteredComponents and filteredComponentIds should return components', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
    }
    whenInitializing({
      givenComponents: [component1, component2],
      expectedFilteredComponents: [component1, component2],
      expectedQueryString: undefined,
    })
  })

  test('when testOutcomes is set to an empty array, filteredComponents and filteredComponentIds return all components', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      testResults: [
        {
          testId: 'test-test-id-1',
          outcome: 'fail',
        },
      ],
    }
    whenSettingTestOutcomes({
      givenComponents: [component1, component2],
      givenValue: [],
      expectedFilteredComponents: [component1, component2],
      expectedQueryString: undefined,
    })
  })

  test('when testOutcomes is set to a test outcome, filteredComponents and filteredComponentIds return all components with their testResults filtered to only include that test outcome', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      testResults: [
        {
          testId: 'test-test-id-1',
          outcome: 'fail',
        },
        {
          testId: 'test-test-id-2',
          outcome: 'pass',
        },
      ],
    }
    const component3 = {
      id: 'test-component-id-3',
      testResults: [
        {
          testId: 'test-test-id-3',
          outcome: 'pass',
        },
      ],
    }
    whenSettingTestOutcomes({
      givenComponents: [component1, component2, component3],
      givenValue: ['fail'],
      expectedFilteredComponents: [
        component1,
        {
          id: 'test-component-id-2',
          testResults: [
            {
              testId: 'test-test-id-1',
              outcome: 'fail',
            },
          ],
        },
        {
          id: 'test-component-id-3',
          testResults: [],
        },
      ],
      expectedQueryString: 'testOutcome=fail',
    })
  })

  test('when testOutcomes is set to multiple test outcomes, filteredComponents and filteredComponentIds return all components with their testResults filtered to only include those test outcomes', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      testResults: [
        {
          testId: 'test-test-id-1',
          outcome: 'fail',
        },
        {
          testId: 'test-test-id-2',
          outcome: 'pass',
        },
        {
          testId: 'test-test-id-3',
          outcome: 'not-applicable',
        },
      ],
    }
    const component3 = {
      id: 'test-component-id-3',
      testResults: [
        {
          testId: 'test-test-id-4',
          outcome: 'pass',
        },
      ],
    }
    whenSettingTestOutcomes({
      givenComponents: [component1, component2, component3],
      givenValue: ['fail', 'pass'],
      expectedFilteredComponents: [
        component1,
        {
          id: 'test-component-id-2',
          testResults: [
            {
              testId: 'test-test-id-1',
              outcome: 'fail',
            },
            {
              testId: 'test-test-id-2',
              outcome: 'pass',
            },
          ],
        },
        component3,
      ],
      expectedQueryString: 'testOutcome=fail&testOutcome=pass',
    })
  })

  test('when testOutcomes is set once via the query string, filteredComponents and filteredComponentIds return all components with their testResults filtered to only include that test outcome', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      testResults: [
        {
          testId: 'test-test-id-1',
          outcome: 'fail',
        },
        {
          testId: 'test-test-id-2',
          outcome: 'pass',
        },
      ],
    }
    const component3 = {
      id: 'test-component-id-3',
      testResults: [
        {
          testId: 'test-test-id-3',
          outcome: 'pass',
        },
      ],
    }
    whenInitializing({
      givenComponents: [component1, component2, component3],
      givenQuery: { testOutcome: ['fail'] },
      expectedFilteredComponents: [
        component1,
        {
          id: 'test-component-id-2',
          testResults: [
            {
              testId: 'test-test-id-1',
              outcome: 'fail',
            },
          ],
        },
        {
          id: 'test-component-id-3',
          testResults: [],
        },
      ],
      expectedFilters: {
        testOutcomes: ['fail'],
      },
    })
  })

  test('when testOutcomes is set to multiple test outcomes, filteredComponents and filteredComponentIds return all components with their testResults filtered to only include those test outcomes', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      testResults: [
        {
          testId: 'test-test-id-1',
          outcome: 'fail',
        },
        {
          testId: 'test-test-id-2',
          outcome: 'pass',
        },
        {
          testId: 'test-test-id-3',
          outcome: 'not-applicable',
        },
      ],
    }
    const component3 = {
      id: 'test-component-id-3',
      testResults: [
        {
          testId: 'test-test-id-4',
          outcome: 'pass',
        },
      ],
    }
    whenInitializing({
      givenComponents: [component1, component2, component3],
      givenQuery: { testOutcome: ['fail', 'pass'] },
      expectedFilteredComponents: [
        component1,
        {
          id: 'test-component-id-2',
          testResults: [
            {
              testId: 'test-test-id-1',
              outcome: 'fail',
            },
            {
              testId: 'test-test-id-2',
              outcome: 'pass',
            },
          ],
        },
        component3,
      ],
      expectedFilters: {
        testOutcomes: ['fail', 'pass'],
      },
    })
  })

  test('when teamIds is set to an empty array, filteredComponents and filteredComponentIds return all components', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      teams: [
        {
          teamId: 'test-team-id-1',
          name: 'Test Team Name 1',
        },
        {
          teamId: 'test-team-id-2',
          name: 'Test Team Name 2',
        },
      ],
    }
    whenSettingTeamIds({
      givenComponents: [component1, component2],
      givenValue: [],
      expectedFilteredComponents: [component1, component2],
      expectedQueryString: undefined,
    })
  })

  test('when teamIds is set to an array of team ids, filteredComponents and filteredComponentIds return only components with any of those team ids', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      teams: [
        {
          teamId: 'test-team-id-1',
          name: 'Test Team Name 1',
        },
        {
          teamId: 'test-team-id-2',
          name: 'Test Team Name 2',
        },
        {
          teamId: 'test-team-id-3',
          name: 'Test Team Name 3',
        },
      ],
    }
    const component3 = {
      id: 'test-component-id-3',
      teams: [
        {
          teamId: 'test-team-id-3',
          name: 'Test Team Name 3',
        },
      ],
    }
    whenSettingTeamIds({
      givenComponents: [component1, component2, component3],
      givenValue: ['test-team-id-2', 'test-team-id-3'],
      expectedFilteredComponents: [component2, component3],
      expectedQueryString: 'teamId=test-team-id-2&teamId=test-team-id-3',
    })
  })

  test('when teamIds is set  once via the query string, filteredComponents and filteredComponentIds return only components with that team id', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      teams: [
        {
          teamId: 'test-team-id-1',
          name: 'Test Team Name 1',
        },
        {
          teamId: 'test-team-id-2',
          name: 'Test Team Name 2',
        },
        {
          teamId: 'test-team-id-3',
          name: 'Test Team Name 3',
        },
      ],
    }
    const component3 = {
      id: 'test-component-id-3',
      teams: [
        {
          teamId: 'test-team-id-3',
          name: 'Test Team Name 3',
        },
      ],
    }
    whenInitializing({
      givenComponents: [component1, component2, component3],
      givenQuery: { teamId: 'test-team-id-2' },
      expectedFilteredComponents: [component2],
      expectedFilters: {
        teamIds: ['test-team-id-2'],
      },
    })
  })

  test('when teamIds is set multiple times via the query string, filteredComponents and filteredComponentIds return only components with any of those team ids', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      teams: [
        {
          teamId: 'test-team-id-1',
          name: 'Test Team Name 1',
        },
        {
          teamId: 'test-team-id-2',
          name: 'Test Team Name 2',
        },
        {
          teamId: 'test-team-id-3',
          name: 'Test Team Name 3',
        },
      ],
    }
    const component3 = {
      id: 'test-component-id-3',
      teams: [
        {
          teamId: 'test-team-id-3',
          name: 'Test Team Name 3',
        },
      ],
    }
    whenInitializing({
      givenComponents: [component1, component2, component3],
      givenQuery: { teamId: ['test-team-id-2', 'test-team-id-3'] },
      expectedFilteredComponents: [component2, component3],
      expectedFilters: {
        teamIds: ['test-team-id-2', 'test-team-id-3'],
      },
    })
  })

  test('when componentTypeIds is set to an empty array, filteredComponents and filteredComponentIds return all components', () => {
    const component1 = {
      id: 'test-component-id-1',
      typeId: 'test-component-type-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      type: 'test-component-type-id-2',
    }
    whenSettingComponentTypeIds({
      givenComponents: [component1, component2],
      givenValue: [],
      expectedFilteredComponents: [component1, component2],
      expectedQueryString: undefined,
    })
  })

  test('when componentTypeIds is set to an array of component type ids, filteredComponents and filteredComponentIds return only components with any of those component type ids', () => {
    const component1 = {
      id: 'test-component-id-1',
      typeId: 'test-component-type-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      typeId: 'test-component-type-id-2',
    }
    const component3 = {
      id: 'test-component-id-3',
      typeId: 'test-component-type-id-3',
    }
    whenSettingComponentTypeIds({
      givenComponents: [component1, component2, component3],
      givenValue: ['test-component-type-id-2', 'test-component-type-id-3'],
      expectedFilteredComponents: [component2, component3],
      expectedQueryString:
        'componentTypeId=test-component-type-id-2&componentTypeId=test-component-type-id-3',
    })
  })

  test('when componentTypeIds is set  once via the query string, filteredComponents and filteredComponentIds return only components with that component type', () => {
    const component1 = {
      id: 'test-component-id-1',
      typeId: 'test-component-type-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      typeId: 'test-component-type-id-2',
    }
    whenInitializing({
      givenComponents: [component1, component2],
      givenQuery: {
        componentTypeId: 'test-component-type-id-2',
      },
      expectedFilteredComponents: [component2],
      expectedFilters: {
        componentTypeIds: ['test-component-type-id-2'],
      },
    })
  })

  test('when componentTypeIds is set multiple times via the query string, filteredComponents and filteredComponentIds return only components with any of those component type ids', () => {
    const component1 = {
      id: 'test-component-id-1',
      typeId: 'test-component-type-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      typeId: 'test-component-type-id-2',
    }
    const component3 = {
      id: 'test-component-id-3',
      typeId: 'test-component-type-id-3',
    }
    whenInitializing({
      givenComponents: [component1, component2, component3],
      givenQuery: {
        componentTypeId: [
          'test-component-type-id-2',
          'test-component-type-id-3',
        ],
      },
      expectedFilteredComponents: [component2, component3],
      expectedFilters: {
        componentTypeIds: [
          'test-component-type-id-2',
          'test-component-type-id-3',
        ],
      },
    })
  })

  test('when tags is set to an empty array, filteredComponents and filteredComponentIds return all components', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      tags: ['test-tag-1', 'test-tag-2'],
    }
    whenSettingTags({
      givenComponents: [component1, component2],
      givenValue: [],
      expectedFilteredComponents: [component1, component2],
      expectedQueryString: undefined,
    })
  })

  test('when tags is set to an array of tags, filteredComponents and filteredComponentIds return only components with any of those tags', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      tags: ['test-tag-1', 'test-tag-2', 'test-tag-3'],
    }
    const component3 = {
      id: 'test-component-id-3',
      tags: ['test-tag-3'],
    }
    whenSettingTags({
      givenComponents: [component1, component2, component3],
      givenValue: ['test-tag-2', 'test-tag-3'],
      expectedFilteredComponents: [component2, component3],
      expectedQueryString: 'tag=test-tag-2&tag=test-tag-3',
    })
  })

  test('when tags is set  once via the query string, filteredComponents and filteredComponentIds return only components with that tag', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      tags: ['test-tag-1', 'test-tag-2', 'test-tag-3'],
    }
    const component3 = {
      id: 'test-component-id-3',
      tags: ['test-tag-3'],
    }
    whenInitializing({
      givenComponents: [component1, component2, component3],
      givenQuery: { tag: 'test-tag-2' },
      expectedFilteredComponents: [component2],
      expectedFilters: {
        tags: ['test-tag-2'],
      },
    })
  })

  test('when tags is set multiple times via the query string, filteredComponents and filteredComponentIds return only components with any of those tags', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      tags: ['test-tag-1', 'test-tag-2', 'test-tag-3'],
    }
    const component3 = {
      id: 'test-component-id-3',
      tags: ['test-tag-3'],
    }
    whenInitializing({
      givenComponents: [component1, component2, component3],
      givenQuery: { tag: ['test-tag-2', 'test-tag-3'] },
      expectedFilteredComponents: [component2, component3],
      expectedFilters: {
        tags: ['test-tag-2', 'test-tag-3'],
      },
    })
  })

  test('when platformIds is set to an empty array, filteredComponents and filteredComponentIds return all components', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      platformId: 'test-platform-id-1',
    }
    whenSettingPlatformIds({
      givenComponents: [component1, component2],
      givenValue: [],
      expectedFilteredComponents: [component1, component2],
      expectedQueryString: undefined,
    })
  })

  test('when platformIds is set to an array of platform ids, filteredComponents and filteredComponentIds return only components with any of those platform ids', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      platformId: 'test-platform-id-1',
    }
    const component3 = {
      id: 'test-component-id-3',
      platformId: 'test-platform-id-2',
    }
    const component4 = {
      id: 'test-component-id-3',
      platformId: 'test-platform-id-3',
    }
    whenSettingPlatformIds({
      givenComponents: [component1, component2, component3, component4],
      givenValue: ['test-platform-id-1', 'test-platform-id-2'],
      expectedFilteredComponents: [component2, component3],
      expectedQueryString:
        'platformId=test-platform-id-1&platformId=test-platform-id-2',
    })
  })

  test('when platformIds is set to an array of platform ids that includes the word `undefined`, filteredComponents and filteredComponentIds return components with platform id not set', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      platformId: 'test-platform-id-1',
    }
    const component3 = {
      id: 'test-component-id-3',
      platformId: 'test-platform-id-2',
    }
    whenSettingPlatformIds({
      givenComponents: [component1, component2, component3],
      givenValue: ['undefined', 'test-platform-id-1'],
      expectedFilteredComponents: [component1, component2],
      expectedQueryString: 'platformId=undefined&platformId=test-platform-id-1',
    })
  })

  test('when platformIds is set once via the query string, filteredComponents and filteredComponentIds return only components with that platform id', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      platformId: 'test-platform-id-1',
    }
    const component3 = {
      id: 'test-component-id-3',
      platformId: 'test-platform-id-2',
    }
    whenInitializing({
      givenComponents: [component1, component2, component3],
      givenQuery: {
        platformId: 'test-platform-id-1',
      },
      expectedFilteredComponents: [component2],
      expectedFilters: {
        platformIds: ['test-platform-id-1'],
      },
    })
  })

  test('when platformIds is set multiple times via the query string, filteredComponents and filteredComponentIds return only components with any of those platform ids', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      platformId: 'test-platform-id-1',
    }
    const component3 = {
      id: 'test-component-id-3',
      platformId: 'test-platform-id-2',
    }
    const component4 = {
      id: 'test-component-id-3',
      platformId: 'test-platform-id-3',
    }
    whenInitializing({
      givenComponents: [component1, component2, component3, component4],
      givenQuery: {
        platformId: ['test-platform-id-1', 'test-platform-id-2'],
      },
      expectedFilteredComponents: [component2, component3],
      expectedFilters: {
        platformIds: ['test-platform-id-1', 'test-platform-id-2'],
      },
    })
  })

  test('when platformIds is set multiple times via the query string and one value is the word `undefined`, filteredComponents and filteredComponentIds return only components with any of those platform ids', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      platformId: 'test-platform-id-1',
    }
    const component3 = {
      id: 'test-component-id-3',
      platformId: 'test-platform-id-2',
    }
    whenInitializing({
      givenComponents: [component1, component2, component3],
      givenQuery: {
        platformId: ['undefined', 'test-platform-id-1'],
      },
      expectedFilteredComponents: [component1, component2],
      expectedFilters: {
        platformIds: ['undefined', 'test-platform-id-1'],
      },
    })
  })

  test('when componentId is set to undefined, filteredComponents and filteredComponentIds return all components', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
    }
    whenSettingComponentId({
      givenComponents: [component1, component2],
      givenValue: undefined,
      expectedFilteredComponents: [component1, component2],
      expectedQueryString: undefined,
    })
  })

  test('when componentId is set to a component id, filteredComponents and filteredComponentIds return the component with matching id', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
    }
    const component3 = {
      id: 'test-component-id-3',
    }
    whenSettingComponentId({
      givenComponents: [component1, component2, component3],
      givenValue: 'test-component-id-2',
      expectedFilteredComponents: [component2],
      expectedQueryString: 'componentId=test-component-id-2',
    })
  })

  test('when componentId is set via the query string, filteredComponents and filteredComponentIds return the component with matching id', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
    }
    const component3 = {
      id: 'test-component-id-3',
    }
    whenInitializing({
      givenComponents: [component1, component2, component3],
      givenQuery: { componentId: 'test-component-id-2' },
      expectedFilteredComponents: [component2],
      expectedFilters: {
        componentId: 'test-component-id-2',
      },
    })
  })

  test('when componentId is set multiple times via the query string, only first value is used', () => {
    const component1 = {
      id: 'test-component-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
    }
    const component3 = {
      id: 'test-component-id-3',
    }
    whenInitializing({
      givenComponents: [component1, component2, component3],
      givenQuery: {
        componentId: ['test-component-id-2', 'test-component-id-3'],
      },
      expectedFilteredComponents: [component2],
      expectedFilters: {
        componentId: 'test-component-id-2',
      },
    })
  })

  test('when componentId is set to a component id, other filters are ignored', () => {
    const component1 = {
      id: 'test-component-id-1',
      platformId: 'test-platform-id-1',
    }
    const component2 = {
      id: 'test-component-id-2',
      platformId: 'test-platform-id-2',
    }
    whenSettingFilters({
      givenComponents: [component1, component2],
      givenFilters: [
        {
          name: 'componentId',
          value: 'test-component-id-1',
        },
        {
          name: 'platformIds',
          value: ['test-platform-id-2'],
        },
      ],
      expectedFilteredComponents: [component1],
      expectedQueryStrings: [
        'componentId=test-component-id-1',
        'platformId=test-platform-id-2&componentId=test-component-id-1',
      ],
    })
  })
})
