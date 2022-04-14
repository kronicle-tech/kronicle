import Index from '~/pages/all-components/dependencies/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createComponent,
  createComponentDependencies,
  createSubComponentDependencies,
  createSummaryWithEmptyComponentAndSubComponentDependencies,
} from '~/test/testDataUtils'

describe('Index', () => {
  let components
  let summary
  let wrapperActions
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      serviceRequests: {
        '/v1/components?fields=components(id,name,typeId,tags,description,notes,responsibilities,teams,platformId)':
          {
            responseBody: { components },
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
    components = [
      createComponent({ componentNumber: 1 }),
      createComponent({ componentNumber: 2 }),
      createComponent({ componentNumber: 3 }),
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
      'Kronicle - All Components - Dependencies'
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

    describe('when a node is clicked', () => {
      describe('when the node has only downstream dependencies', () => {
        test('shows downstream dependencies in panel', async () => {
          await createWrapper()
          await wrapper
            .get('#component-dependency-graph-node-0')
            .trigger('click')
          expect(wrapper.element).toMatchSnapshot()
        })
      })

      describe('when the node has upstream and downstream dependencies', () => {
        test('shows both upstream and downstream dependencies in panel', async () => {
          await createWrapper()
          await wrapper
            .get('#component-dependency-graph-node-1')
            .trigger('click')
          expect(wrapper.element).toMatchSnapshot()
        })
      })

      describe('when the node has only upstream dependencies', () => {
        test('shows upstream dependencies in panel', async () => {
          await createWrapper()
          await wrapper
            .get('#component-dependency-graph-node-2')
            .trigger('click')
          expect(wrapper.element).toMatchSnapshot()
        })
      })
    })

    describe('when the detailed checkbox is checked', () => {
      beforeEach(() => {
        wrapperActions.push(
          async (wrapper) => {
            await wrapper.get('#toggle-filters').trigger('click')
            await wrapper.get('#detailed-dependencies').trigger('click')
          }
        )
      })

      test('shows detailed dependencies in graph', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })

      describe('when a node is clicked', () => {
        beforeEach(() => {
          wrapperActions.push(
            async (wrapper) =>
              await wrapper
                .get('#component-dependency-graph-node-1')
                .trigger('click')
          )
        })

        test('shows dependencies and component details in panel', async () => {
          await createWrapper()
          expect(wrapper.element).toMatchSnapshot()
        })
      })
    })
  })
})
