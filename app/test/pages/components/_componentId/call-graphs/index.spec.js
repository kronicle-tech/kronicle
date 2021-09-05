import Index from '~/pages/components/_componentId/call-graphs/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createComponent,
  createDependency,
  createSubComponentNode,
} from '~/test/testDataUtils'
import {
  expectCallGraphCount,
  expectNodeCount,
  expectNodeVariants,
} from '~/test/callGraphUtils'

describe('Index', () => {
  const route = {
    params: {
      componentId: 'test-component-id-1',
    },
  }
  let component
  let nodes
  let callGraphs
  let wrapperActions
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      route,
      serviceRequests: {
        '/v1/components/test-component-id-1?fields=component(id,name)': {
          responseBody: { component },
        },
        '/v1/components/test-component-id-1/nodes': {
          responseBody: { nodes },
        },
        '/v1/components/test-component-id-1/call-graphs': {
          responseBody: { callGraphs },
        },
      },
    })
    for (const wrapperAction of wrapperActions) {
      await wrapperAction(wrapper)
    }
  }

  beforeEach(() => {
    component = createComponent({ componentNumber: 1 })
    nodes = []
    callGraphs = []
    wrapperActions = []
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  test('has the right page title', async () => {
    await createWrapper()
    expect(wrapper.vm.$metaInfo.title).toBe(
      'Kronicle - Test Component Name 1 - Call Graphs'
    )
  })

  describe('when Get Component Nodes service endpoint returns no nodes', () => {
    test('renders a message saying there are no call graphs available', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
      expect(wrapper.text()).toEqual(
        expect.stringContaining(
          'No call graphs are available for this component'
        )
      )
    })
  })

  describe('when Get Component Nodes service endpoints returns nodes and Get Component Call Graphs service endpoint returns call graphs', () => {
    beforeEach(() => {
      nodes = [
        createSubComponentNode({
          componentNodeNumber: 1,
          subComponentNodeNumber: 1,
        }),
        createSubComponentNode({
          componentNodeNumber: 1,
          subComponentNodeNumber: 2,
        }),
      ]
      callGraphs = [
        {
          nodes: [
            createSubComponentNode({
              componentNodeNumber: 1,
              subComponentNodeNumber: 1,
            }),
            createSubComponentNode({ componentNodeNumber: 2 }),
          ],
          dependencies: [createDependency({ sourceIndex: 0, targetIndex: 1 })],
          traceCount: 1,
        },
        {
          nodes: [
            createSubComponentNode({
              componentNodeNumber: 1,
              subComponentNodeNumber: 2,
            }),
            createSubComponentNode({ componentNodeNumber: 2 }),
          ],
          dependencies: [createDependency({ sourceIndex: 0, targetIndex: 1 })],
          traceCount: 1,
        },
        {
          nodes: [
            createSubComponentNode({
              componentNodeNumber: 1,
              subComponentNodeNumber: 2,
            }),
            createSubComponentNode({ componentNodeNumber: 3 }),
          ],
          dependencies: [createDependency({ sourceIndex: 0, targetIndex: 1 })],
          traceCount: 1,
        },
      ]
    })

    test('renders the nodes and call graphs', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
      expectCallGraphCount(wrapper, 1)
      expectNodeCount(wrapper, 2)
      expectNodeVariants(wrapper, ['success', 'secondary'])
    })
  })
})
