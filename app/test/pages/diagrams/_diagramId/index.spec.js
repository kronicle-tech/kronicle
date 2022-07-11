import Index from '~/pages/diagrams/_diagramId'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createComponent,
  createComponentAvailableDataRequests,
  createDiagramWithEmptyGraph,
  createGraph,
} from '~/test/testDataUtils'

describe('Index', () => {
  const route = {
    params: {
      diagramId: 'test-diagram-id-1',
    },
  }
  let components
  let diagram
  let wrapperActions
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      route,
      serviceRequests: {
        ...createComponentAvailableDataRequests(),
        '/v1/components?fields=components(id,name,type,tags,description,notes,responsibilities,teams,platformId,states(environmentId,pluginId))':
          {
            responseBody: { components },
          },
        '/v1/diagrams/test-diagram-id-1': {
          responseBody: { diagram },
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
      createComponent({ componentNumber: 4 }),
      createComponent({ componentNumber: 5 }),
      createComponent({ componentNumber: 6 }),
    ]
    diagram = createDiagramWithEmptyGraph()
    wrapperActions = []
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  test('has the right page title', async () => {
    await createWrapper()
    expect(wrapper.vm.$metaInfo.title).toBe(
      'Kronicle - Diagrams - Test Diagram Name 1'
    )
  })

  describe('when Get Diagram service endpoint returns no edges', () => {
    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })

  describe('when Get Diagram service endpoint returns an array of multiple component edges', () => {
    beforeEach(() => {
      diagram.states[0] = createGraph()
    })

    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })
})
