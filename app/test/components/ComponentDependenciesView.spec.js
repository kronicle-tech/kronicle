import ComponentDependenciesView from '@/components/ComponentDependenciesView'
import { createViewComponentWrapper } from '~/test/components/viewUtils'
import { createComponent } from '~/test/testDataUtils'

function createEdge(sourceIndex, targetIndex) {
  return {
    sourceIndex,
    targetIndex,
    relatedIndexes: [],
    manual: false,
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

describe('ComponentDependenciesView', () => {
  let propsData
  let wrapper

  async function createWrapper() {
    wrapper = await createViewComponentWrapper(ComponentDependenciesView, {
      propsData,
    })
  }

  function expectNodeCount(count) {
    expect(wrapper.findAll('.node')).toHaveLength(count)
  }

  function expectEdgeCount(count) {
    expect(wrapper.findAll('.edge')).toHaveLength(count)
  }

  function expectNodeClasses(dependencies) {
    expect(wrapper.findAll('.node')).toHaveLength(dependencies.length)
    dependencies.forEach((node) =>
      expect(
        wrapper
          .get(`#component-dependency-graph-node-${node.nodeIndex}`)
          .classes()
      ).toContain(node.nodeClass)
    )
  }

  function expectEdgeClasses(dependencies) {
    expect(wrapper.findAll('.edge')).toHaveLength(dependencies.length)
    dependencies.forEach((edge) =>
      expect(
        wrapper
          .get(`#component-dependency-graph-edge-${edge.edgeIndex}`)
          .classes()
      ).toContain(edge.edgeClass)
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

    test('renders no dependencies', async () => {
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
              pluginId: 'test-plugin-id',
              nodes: [
                createComponentNode(1),
                createComponentNode(2),
                createComponentNode(3),
                createComponentNode(4),
              ],
              edges: [
                createEdge(undefined, 0),
                createEdge(0, 1),
                createEdge(1, 2),
                createEdge(1, 3),
              ],
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
          expectNodeCount(2)
          expectEdgeCount(1)
        })
      })

      describe('when a node is clicked', () => {
        beforeEach(async () => {
          await createWrapper()
          await wrapper
            .get('#component-dependency-graph-node-0')
            .trigger('click')
        })

        test('renders the connention directly related to the first node with the style of a direct edge', () => {
          expect(wrapper.html()).toMatchSnapshot()
          expectNodeClasses([
            { nodeIndex: 0, nodeClass: 'selected-node' },
            { nodeIndex: 1, nodeClass: 'scoped-node' },
            { nodeIndex: 2, nodeClass: 'scoped-node' },
            { nodeIndex: 3, nodeClass: 'scoped-node' },
          ])
          expectEdgeClasses([
            { edgeIndex: 1, edgeClass: 'direct-edge' },
            { edgeIndex: 2, edgeClass: 'scoped-edge' },
            { edgeIndex: 3, edgeClass: 'scoped-edge' },
          ])
        })
      })

      describe('and the mouse hovers over the first node', () => {
        beforeEach(async () => {
          await createWrapper()
          await wrapper
            .get('#component-dependency-graph-node-0')
            .trigger('mouseover')
        })

        test('renders the connection directly related to the first node with the style of a direct connection', () => {
          expect(wrapper.html()).toMatchSnapshot()
          expectNodeClasses([
            { nodeIndex: 0, nodeClass: 'selected-node' },
            { nodeIndex: 1, nodeClass: 'scoped-node' },
            { nodeIndex: 2, nodeClass: 'scoped-node' },
            { nodeIndex: 3, nodeClass: 'scoped-node' },
          ])
          expectEdgeClasses([
            { edgeIndex: 1, edgeClass: 'direct-edge' },
            { edgeIndex: 2, edgeClass: 'scoped-edge' },
            { edgeIndex: 3, edgeClass: 'scoped-edge' },
          ])
        })

        describe('and the mouse hovers away from the first node', () => {
          beforeEach(async () => {
            await createWrapper()
            await wrapper
              .get('#component-dependency-graph-node-0')
              .trigger('mouseout')
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
              { edgeIndex: 1, edgeClass: 'scoped-edge' },
              { edgeIndex: 2, edgeClass: 'scoped-edge' },
              { edgeIndex: 3, edgeClass: 'scoped-edge' },
            ])
          })
        })
      })
    })
  })
})
