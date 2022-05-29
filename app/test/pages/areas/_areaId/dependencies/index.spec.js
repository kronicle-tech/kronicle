import Index from '~/pages/areas/_areaId/dependencies/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createArea,
  createComponent,
  createComponentDependencies,
  createSubComponentDependencies,
  createSummaryWithEmptyComponentAndSubComponentDependencies,
} from '~/test/testDataUtils'

describe('Index', () => {
  const route = {
    params: {
      areaId: 'test-area-id-1',
    },
  }
  let area
  let allComponents
  let summary
  let wrapperActions
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      route,
      serviceRequests: {
        '/v1/areas/test-area-id-1?fields=area(id,name,components(id,name,typeId,tags,description,notes,responsibilities,teams,platformId,states(environmentId,pluginId)))':
          {
            responseBody: { area },
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
    area = createArea({ areaNumber: 1 })
    area.components = [
      createComponent({ componentNumber: 1 }),
      createComponent({ componentNumber: 2 }),
      createComponent({ componentNumber: 3 }),
    ]
    allComponents = [].concat(area.components, [
      createComponent({ componentNumber: 4 }),
      createComponent({ componentNumber: 5 }),
      createComponent({ componentNumber: 6 }),
    ])
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
      'Kronicle - Test Area Name 1 Area - Visualizations'
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
