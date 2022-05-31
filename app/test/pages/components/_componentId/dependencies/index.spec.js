import Index from '~/pages/components/_componentId/dependencies/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createComponent,
  createComponentDependencies,
  createComponentResponseWithStateTypes,
  createSubComponentDependencies,
  createSummaryWithEmptyComponentAndSubComponentDependencies,
} from '~/test/testDataUtils'

describe('Index', () => {
  const route = {
    params: {
      componentId: 'test-component-id-1',
    },
  }
  let component
  let allComponents
  let summary
  let wrapperActions
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      route,
      serviceRequests: {
        '/v1/components/test-component-id-1?fields=component(id,name,states(type))': createComponentResponseWithStateTypes(1),
        '/v1/components/test-component-id-1?fields=component(id,name,typeId,tags,description,notes,responsibilities,teams,platformId,states(environmentId,pluginId))':
          {
            responseBody: { component },
          },
        '/v1/components?fields=components(id,name,typeId,tags,description,notes,responsibilities,teams,platformId)':
          {
            responseBody: { components: allComponents },
          },
        '/v1/summary?fields=summary(componentDependencies,subComponentDependencies)':
          {
            responseBody: { summary },
          },
      },
    })
    for (const wrapperAction of wrapperActions) {
      await wrapperAction(wrapper)
    }
  }

  beforeEach(() => {
    component = createComponent({ componentNumber: 1 })
    allComponents = [
      createComponent({ componentNumber: 1 }),
      createComponent({ componentNumber: 2 }),
      createComponent({ componentNumber: 3 }),
      createComponent({ componentNumber: 4 }),
      createComponent({ componentNumber: 5 }),
      createComponent({ componentNumber: 6 }),
    ]
    summary = createSummaryWithEmptyComponentAndSubComponentDependencies()
    wrapperActions = []
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  test('has the right page title', async () => {
    await createWrapper()
    expect(wrapper.vm.$metaInfo.title).toBe(
      'Kronicle - Test Component Name 1 - Visualizations'
    )
  })

  describe('when Get Summary service endpoint returns no dependencies', () => {
    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })

  describe('when Get Summary service endpoint returns an array of multiple component dependencies', () => {
    beforeEach(() => {
      summary.componentDependencies = createComponentDependencies()
      summary.subComponentDependencies = createSubComponentDependencies()
    })

    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })

    describe('when the detailed checkbox is checked', () => {
      beforeEach(() => {
        wrapperActions.push(async (wrapper) => {
          await wrapper.get('#detailed-dependencies').trigger('click')
        })
      })

      test('shows detailed dependencies in graph', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
