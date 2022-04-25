import { mount } from '@vue/test-utils'
import ComponentFilters from '@/components/ComponentFilters.vue'

describe('ComponentFilters', () => {
  let propsData
  let store
  let wrapper

  async function createWrapper() {
    const Store = await import('~/.nuxt/store.js')
    store = Store.createStore()
    store.commit('componentFilters/initialize', {
      components: deepClone(propsData.components),
      route: {
        query: {},
      },
    })
    wrapper = mount(ComponentFilters, { store, propsData })
  }

  function deepClone(value) {
    if (value === undefined) {
      return undefined
    }
    return JSON.parse(JSON.stringify(value))
  }

  function expectCheckboxGroupToHaveValues(wrapper, selector, values) {
    expect(wrapper.findAll(selector).length).toBe(values.length)
    expect(
      wrapper
        .findAll(selector)
        .wrappers.map((inputWrapper) => inputWrapper.attributes().value)
    ).toEqual(values)
  }

  function expectSelectToHaveValues(wrapper, selector, values) {
    expect(wrapper.find(selector).findAll('option').length).toBe(values.length)
    expect(
      wrapper
        .find(selector)
        .findAll('option')
        .wrappers.map((inputWrapper) => inputWrapper.attributes().value)
    ).toEqual(values)
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
    store = null
  })

  describe('when components prop is not set', () => {
    test('renders nothing', async () => {
      await createWrapper()
      expect(wrapper.html()).toEqual(``)
    })
  })

  describe('when components prop is set to multiple components', () => {
    beforeEach(() => {
      propsData.components = [
        {
          id: 'test-component-id-1',
          name: 'Test Component Name 1',
          typeId: 'test-component-type-id-1',
          repo: {
            url: 'https://example.com/repo-1',
          },
        },
        {
          id: 'test-component-id-2',
          name: 'Test Component Name 2',
          typeId: 'test-component-type-id-2',
          repo: {
            url: 'https://example.com/repo-2',
          },
        },
        {
          id: 'test-component-id-3',
          name: 'Test Component Name 3',
          typeId: 'test-component-type-id-3',
          repo: {
            url: 'https://example.com/repo-3',
          },
        },
      ]
    })

    test('renders minimal filters', async () => {
      await createWrapper()
      await wrapper.get('#toggleFilters').trigger('click')
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when components prop is set to multiple components with all filterable fields', () => {
    beforeEach(() => {
      propsData.components = [
        {
          id: 'test-component-id-1',
          name: 'Test Component Name 1',
          typeId: 'test-component-type-id-1',
          tags: [
            {
              key: 'test-tag-1-a',
            },
            {
              key: 'test-tag-1-b',
            },
          ],
          repo: {
            url: 'https://example.com/repo-1',
          },
          teams: [
            {
              teamId: 'test-team-id-1-a',
            },
            {
              teamId: 'test-team-id-1-b',
            },
          ],
          platformId: 'test-platform-id-1',
          testResults: [
            {
              testId: 'test-test-id-1-a',
              outcome: 'fail',
            },
            {
              testId: 'test-test-id-1-b',
              outcome: 'not-applicable',
            },
            {
              testId: 'test-test-id-1-c',
              outcome: 'pass',
            },
          ],
        },
        {
          id: 'test-component-id-2',
          name: 'Test Component Name 2',
          typeId: 'test-component-type-id-2',
          tags: [
            {
              key: 'test-tag-2-a',
            },
            {
              key: 'test-tag-2-b',
            },
          ],
          repo: {
            url: 'https://example.com/repo-2',
          },
          teams: [
            {
              teamId: 'test-team-id-2-a',
            },
            {
              teamId: 'test-team-id-2-b',
            },
          ],
          platformId: 'test-platform-id-2',
          testResults: [
            {
              testId: 'test-test-id-2-a',
              outcome: 'fail',
            },
            {
              testId: 'test-test-id-2-b',
              outcome: 'not-applicable',
            },
            {
              testId: 'test-test-id-2-c',
              outcome: 'pass',
            },
          ],
        },
        {
          id: 'test-component-id-3',
          name: 'Test Component Name 3',
          typeId: 'test-component-type-id-3',
          tags: [
            {
              key: 'test-tag-3-a',
            },
            {
              key: 'test-tag-3-b',
            },
          ],
          repo: {
            url: 'https://example.com/repo-3',
          },
          teams: [
            {
              teamId: 'test-team-id-3-a',
            },
            {
              teamId: 'test-team-id-3-b',
            },
          ],
          platformId: 'test-platform-id-3',
          testResults: [
            {
              testId: 'test-test-id-3-a',
              outcome: 'fail',
            },
            {
              testId: 'test-test-id-3-b',
              outcome: 'not-applicable',
            },
            {
              testId: 'test-test-id-3-c',
              outcome: 'pass',
            },
          ],
        },
      ]
    })

    test('renders all the filters, including component filter but excluding the test outcomes filter', async () => {
      await createWrapper()
      await wrapper.get('#toggleFilters').trigger('click')
      expect(wrapper.html()).toMatchSnapshot()
      expect(wrapper.findAll('#component-filter').exists()).toBe(true)
      expect(wrapper.findAll('input[name="testOutcome"]').exists()).toBe(false)
    })

    test('when test outcomes filter is enabled, renders the test outcomes filter', async () => {
      propsData.testOutcomesFilterEnabled = true
      await createWrapper()
      await wrapper.get('#toggleFilters').trigger('click')
      expect(wrapper.html()).toMatchSnapshot()
      expect(wrapper.findAll('input[name="testOutcome"]').exists()).toBe(true)
    })

    test('when selecting a test outcome, the state is updated', async () => {
      propsData.testOutcomesFilterEnabled = true
      await createWrapper()
      await wrapper.get('#toggleFilters').trigger('click')
      await wrapper.get('input[value="fail"]').setChecked()
      expect(store.state.componentFilters.testOutcomes).toEqual(['fail'])
    })

    test('when selecting multiple test outcomes, the state is updated', async () => {
      propsData.testOutcomesFilterEnabled = true
      await createWrapper()
      await wrapper.get('#toggleFilters').trigger('click')
      await wrapper.get('input[value="fail"]').setChecked()
      await wrapper.get('input[value="pass"]').setChecked()
      await wrapper.get('input[value="not-applicable"]').setChecked()
      expect(store.state.componentFilters.testOutcomes).toEqual([
        'fail',
        'pass',
        'not-applicable',
      ])
    })

    test('when selecting a team, the state is updated', async () => {
      await createWrapper()
      await wrapper.get('#toggleFilters').trigger('click')
      await wrapper.get('input[value="test-team-id-1-a"]').setChecked()
      expect(store.state.componentFilters.teamIds).toEqual(['test-team-id-1-a'])
    })

    test('when selecting multiple teams, the state is updated', async () => {
      await createWrapper()
      await wrapper.get('#toggleFilters').trigger('click')
      await wrapper.get('input[value="test-team-id-1-a"]').setChecked()
      await wrapper.get('input[value="test-team-id-2-a"]').setChecked()
      await wrapper.get('input[value="test-team-id-3-a"]').setChecked()
      expect(store.state.componentFilters.teamIds).toEqual([
        'test-team-id-1-a',
        'test-team-id-2-a',
        'test-team-id-3-a',
      ])
    })

    test('when selecting a tag, the state is updated', async () => {
      await createWrapper()
      await wrapper.get('#toggleFilters').trigger('click')
      await wrapper.get('input[value="test-tag-1-a"]').setChecked()
      expect(store.state.componentFilters.tags).toEqual(['test-tag-1-a'])
    })

    test('when selecting multiple tags, the state is updated', async () => {
      await createWrapper()
      await wrapper.get('#toggleFilters').trigger('click')
      await wrapper.get('input[value="test-tag-1-a"]').setChecked()
      await wrapper.get('input[value="test-tag-2-a"]').setChecked()
      await wrapper.get('input[value="test-tag-3-a"]').setChecked()
      expect(store.state.componentFilters.tags).toEqual([
        'test-tag-1-a',
        'test-tag-2-a',
        'test-tag-3-a',
      ])
    })

    test('when selecting a component type, the state is updated', async () => {
      await createWrapper()
      await wrapper.get('#toggleFilters').trigger('click')
      await wrapper.get('input[value="test-component-type-id-1"]').setChecked()
      expect(store.state.componentFilters.componentTypeIds).toEqual([
        'test-component-type-id-1',
      ])
    })

    test('when selecting multiple component type ids, the state is updated', async () => {
      await createWrapper()
      await wrapper.get('#toggleFilters').trigger('click')
      await wrapper.get('input[value="test-component-type-id-1"]').setChecked()
      await wrapper.get('input[value="test-component-type-id-2"]').setChecked()
      await wrapper.get('input[value="test-component-type-id-3"]').setChecked()
      expect(store.state.componentFilters.componentTypeIds).toEqual([
        'test-component-type-id-1',
        'test-component-type-id-2',
        'test-component-type-id-3',
      ])
    })

    test('when selecting a platform, the state is updated', async () => {
      await createWrapper()
      await wrapper.get('#toggleFilters').trigger('click')
      await wrapper.get('input[value="test-platform-id-1"]').setChecked()
      expect(store.state.componentFilters.platformIds).toEqual([
        'test-platform-id-1',
      ])
    })

    test('when selecting multiple platform ids, the state is updated', async () => {
      await createWrapper()
      await wrapper.get('#toggleFilters').trigger('click')
      await wrapper.get('input[value="test-platform-id-1"]').setChecked()
      await wrapper.get('input[value="test-platform-id-2"]').setChecked()
      await wrapper.get('input[value="test-platform-id-3"]').setChecked()
      expect(store.state.componentFilters.platformIds).toEqual([
        'test-platform-id-1',
        'test-platform-id-2',
        'test-platform-id-3',
      ])
    })

    test('when component filter is not enabled, does not render the component filter', async () => {
      propsData.componentFilterEnabled = false
      await createWrapper()
      await wrapper.get('#toggleFilters').trigger('click')
      expect(wrapper.html()).toMatchSnapshot()
      expect(wrapper.findAll('#component-filter').exists()).toBe(false)
    })

    test('when selecting a component, the state is updated', async () => {
      await createWrapper()
      await wrapper.get('#toggleFilters').trigger('click')
      await wrapper.get('option[value="test-component-id-1"]').setSelected()
      expect(store.state.componentFilters.componentId).toEqual(
        'test-component-id-1'
      )
    })
  })

  describe('when components prop is set to multiple components with duplicate filter values', () => {
    beforeEach(() => {
      propsData.components = [
        {
          id: 'test-component-id-1',
          name: 'Test Component Name 1',
          typeId: 'test-component-type-id-1',
          tags: [
            {
              key: 'test-tag-1-a',
            },
            {
              key: 'test-tag-1-b',
            },
          ],
          repo: {
            url: 'https://example.com/repo-1',
          },
          teams: [
            {
              teamId: 'test-team-id-1-a',
            },
            {
              teamId: 'test-team-id-1-b',
            },
          ],
          platformId: 'test-platform-id-1',
          testResults: [
            {
              testId: 'test-test-id-1-a',
              outcome: 'fail',
            },
            {
              testId: 'test-test-id-1-b',
              outcome: 'not-applicable',
            },
            {
              testId: 'test-test-id-1-c',
              outcome: 'pass',
            },
          ],
        },
        {
          id: 'test-component-id-2',
          name: 'Test Component Name 1',
          typeId: 'test-component-type-id-1',
          tags: [
            {
              key: 'test-tag-1-a',
            },
            {
              key: 'test-tag-1-b',
            },
          ],
          repo: {
            url: 'https://example.com/repo-1',
          },
          teams: [
            {
              teamId: 'test-team-id-1-a',
            },
            {
              teamId: 'test-team-id-1-b',
            },
          ],
          platformId: 'test-platform-id-1',
          testResults: [
            {
              testId: 'test-test-id-1-a',
              outcome: 'fail',
            },
            {
              testId: 'test-test-id-1-b',
              outcome: 'not-applicable',
            },
            {
              testId: 'test-test-id-1-c',
              outcome: 'pass',
            },
          ],
        },
        {
          id: 'test-component-id-3',
          name: 'Test Component Name 1',
          typeId: 'test-component-type-id-1',
          tags: [
            {
              key: 'test-tag-1-a',
            },
            {
              key: 'test-tag-1-b',
            },
          ],
          repo: {
            url: 'https://example.com/repo-1',
          },
          teams: [
            {
              teamId: 'test-team-id-1-a',
            },
            {
              teamId: 'test-team-id-1-b',
            },
          ],
          platformId: 'test-platform-id-1',
          testResults: [
            {
              testId: 'test-test-id-1-a',
              outcome: 'fail',
            },
            {
              testId: 'test-test-id-1-b',
              outcome: 'not-applicable',
            },
            {
              testId: 'test-test-id-1-c',
              outcome: 'pass',
            },
          ],
        },
      ]
    })

    test('dedupes the filter values', async () => {
      propsData.testOutcomesFilterEnabled = true
      await createWrapper()
      await wrapper.get('#toggleFilters').trigger('click')
      expect(wrapper.html()).toMatchSnapshot()
      expectCheckboxGroupToHaveValues(wrapper, 'input[name="testOutcome"]', [
        'fail',
        'not-applicable',
        'pass',
      ])
      expectCheckboxGroupToHaveValues(wrapper, 'input[name="team"]', [
        'test-team-id-1-a',
        'test-team-id-1-b',
      ])
      expectCheckboxGroupToHaveValues(wrapper, 'input[name="tag"]', [
        'test-tag-1-a',
        'test-tag-1-b',
      ])
      expectCheckboxGroupToHaveValues(
        wrapper,
        'input[name="componentTypeId"]',
        ['test-component-type-id-1']
      )
      expectCheckboxGroupToHaveValues(wrapper, 'input[name="platformId"]', [
        'test-platform-id-1',
      ])
      expectSelectToHaveValues(wrapper, '#component-filter', [
        '',
        'test-component-id-1',
        'test-component-id-2',
        'test-component-id-3',
      ])
    })
  })
})
