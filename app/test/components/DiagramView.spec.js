import DiagramView from '~/components/DiagramView'
import { createViewComponentWrapper } from '~/test/components/viewUtils'
import { createComponent } from '~/test/testDataUtils'

function createEdge(sourceIndex, targetIndex) {
  return {
    sourceIndex,
    targetIndex,
    relatedIndexes: [],
    sampleSize: 1,
    startTimestamp: '2021-01-01T00:00:00.000Z',
    endTimestamp: '2021-01-01T00:00:01.000Z',
    duration: {
      min: 1_000_000,
      max: 1_000_000,
      p50: 1_000_000,
      p90: 1_000_000,
      p99: 1_000_000,
      p99Point9: 1_000_000,
    },
  }
}

function createComponentNode(nodeNumber) {
  return {
    componentId: `test-component-id-${nodeNumber}`,
  }
}

describe('DiagramView', () => {
  let propsData
  let wrapper

  async function createWrapper() {
    wrapper = await createViewComponentWrapper(DiagramView, {
      propsData,
    })
  }

  function expectNodeCount(count) {
    expect(wrapper.findAll('.node')).toHaveLength(count)
  }

  function expectEdgeCount(count) {
    expect(wrapper.findAll('.edge')).toHaveLength(count)
  }

  function expectNodeClasses(edges) {
    expect(wrapper.findAll('.node')).toHaveLength(edges.length)
    edges.forEach((node) =>
      expect(wrapper.get(`#graph-node-${node.nodeIndex}`).classes()).toContain(
        node.nodeClass
      )
    )
  }

  function expectEdgeClasses(edges) {
    expect(wrapper.findAll('.edge')).toHaveLength(edges.length)
    edges.forEach((edge) =>
      expect(wrapper.get(`#graph-edge-${edge.edgeIndex}`).classes()).toContain(
        edge.edgeClass
      )
    )
  }

  beforeEach(() => {
    propsData = {
      diagram: {
        states: [],
      },
      allComponents: [],
    }
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when components prop is set to an empty array', () => {
    beforeEach(() => {
      propsData.components = []
    })

    test('renders no edges', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when components prop contains multiple components', () => {
    beforeEach(() => {
      propsData.components = [
        createComponent({ componentNumber: 1, platformNumber: 1 }),
        createComponent({ componentNumber: 2, platformNumber: 2 }),
        createComponent({ componentNumber: 3, platformNumber: 3 }),
        createComponent({ componentNumber: 4, platformNumber: 4 }),
      ]
      propsData.allComponents.push(propsData.components)
      propsData.allComponents.push(
        createComponent({ componentNumber: 5, platformNumber: 5 })
      )
    })

    describe('and diagram prop contains an empty edges array', () => {
      test('renders no connections', async () => {
        await createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
        expectNodeCount(0)
        expectEdgeCount(0)
      })
    })

    describe('and diagram prop contains multiple edges', () => {
      beforeEach(() => {
        propsData.diagram = {
          states: [
            {
              type: 'graph',
              nodes: [
                createComponentNode(1),
                createComponentNode(2),
                createComponentNode(3),
                createComponentNode(4),
              ],
              edges: [createEdge(0, 1), createEdge(1, 2), createEdge(1, 3)],
            },
          ],
        }
      })

      test('renders the connections', async () => {
        await createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
        expectNodeCount(4)
        expectEdgeCount(3)
      })

      describe('and first platform is checked in platform filter', () => {
        beforeEach(async () => {
          await createWrapper()
          await wrapper.get('input[value="test-platform-id-1"]').setChecked()
        })

        test('renders the connections directly related to the first component using the first platform', () => {
          expect(wrapper.html()).toMatchSnapshot()
          expectNodeCount(4)
          expectEdgeCount(3)
        })
      })

      describe('when a node is clicked', () => {
        beforeEach(async () => {
          await createWrapper()
          await wrapper.get('#graph-node-0').trigger('click')
        })

        test('renders the connection directly related to the first node with the style of a direct edge', () => {
          expect(wrapper.html()).toMatchSnapshot()
          expectNodeClasses([
            { nodeIndex: 0, nodeClass: 'selected-node' },
            { nodeIndex: 1, nodeClass: 'direct-node' },
            { nodeIndex: 2, nodeClass: 'scoped-node' },
            { nodeIndex: 3, nodeClass: 'scoped-node' },
          ])
          expectEdgeClasses([
            { edgeIndex: 0, edgeClass: 'direct-edge' },
            { edgeIndex: 1, edgeClass: 'scoped-edge' },
            { edgeIndex: 2, edgeClass: 'scoped-edge' },
          ])
        })
      })

      describe('and the mouse hovers over the first node', () => {
        beforeEach(async () => {
          await createWrapper()
          await wrapper.get('#graph-node-0').trigger('mouseover')
        })

        test('renders the connection directly related to the first node with the style of a direct connection', () => {
          expect(wrapper.html()).toMatchSnapshot()
          expectNodeClasses([
            { nodeIndex: 0, nodeClass: 'selected-node' },
            { nodeIndex: 1, nodeClass: 'direct-node' },
            { nodeIndex: 2, nodeClass: 'scoped-node' },
            { nodeIndex: 3, nodeClass: 'scoped-node' },
          ])
          expectEdgeClasses([
            { edgeIndex: 0, edgeClass: 'direct-edge' },
            { edgeIndex: 1, edgeClass: 'scoped-edge' },
            { edgeIndex: 2, edgeClass: 'scoped-edge' },
          ])
        })

        describe('and the mouse hovers away from the first node', () => {
          beforeEach(async () => {
            await createWrapper()
            await wrapper.get('#graph-node-0').trigger('mouseout')
          })

          test('renders all the connections as scoped connections', () => {
            expect(wrapper.html()).toMatchSnapshot()
            expectNodeClasses([
              { nodeIndex: 0, nodeClass: 'scoped-node' },
              { nodeIndex: 1, nodeClass: 'scoped-node' },
              { nodeIndex: 2, nodeClass: 'scoped-node' },
              { nodeIndex: 3, nodeClass: 'scoped-node' },
            ])
            expectEdgeClasses([
              { edgeIndex: 0, edgeClass: 'scoped-edge' },
              { edgeIndex: 1, edgeClass: 'scoped-edge' },
              { edgeIndex: 2, edgeClass: 'scoped-edge' },
            ])
          })
        })
      })
    })
  })
})
