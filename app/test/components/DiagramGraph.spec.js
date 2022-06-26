import { mount } from '@vue/test-utils'
import DiagramGraph from '~/components/DiagramGraph.vue'

describe('DiagramGraph', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(DiagramGraph, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when diagram prop is not set', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when diagram prop is set to multiple nodes and dependencies', () => {
    beforeEach(() => {
      propsData.diagram = {
        states: [
          {
            type: 'graph',
            nodes: [
              {
                componentId: 'test-id-1',
              },
              {
                componentId: 'test-id-2',
              },
              {
                componentId: 'test-id-3',
              },
            ],
            edges: [
              {
                sourceIndex: 0,
                targetIndex: 1,
                relatedIndexes: [],
              },
              {
                sourceIndex: 1,
                targetIndex: 2,
                relatedIndexes: [],
              },
            ],
          },
        ],
      }
    })

    test('renders edges, dependency markers, nodes and node labels', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when there are multiple nodes at the same depth', () => {
    beforeEach(() => {
      propsData.diagram = {
        states: [
          {
            type: 'graph',
            nodes: [
              {
                componentId: 'test-id-1',
              },
              {
                componentId: 'test-id-2',
              },
              {
                componentId: 'test-id-3',
              },
              {
                componentId: 'test-id-4',
              },
              {
                componentId: 'test-id-5',
              },
              {
                componentId: 'test-id-6',
              },
            ],
            edges: [
              {
                sourceIndex: 0,
                targetIndex: 1,
                relatedIndexes: [],
              },
              {
                sourceIndex: 1,
                targetIndex: 2,
                relatedIndexes: [],
              },
              {
                sourceIndex: 0,
                targetIndex: 3,
                relatedIndexes: [],
              },
              {
                sourceIndex: 3,
                targetIndex: 4,
                relatedIndexes: [],
              },
              {
                sourceIndex: 4,
                targetIndex: 5,
                relatedIndexes: [],
              },
            ],
          },
        ],
      }
    })

    test('sorts the nodes by their number of dependencies', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when the selectedComponentId is specified', () => {
    beforeEach(() => {
      propsData.diagram = {
        states: [
          {
            type: 'graph',
            nodes: [
              {
                componentId: 'test-id-1',
              },
              {
                componentId: 'test-id-2',
              },
              {
                componentId: 'test-id-3',
              },
              {
                componentId: 'test-id-4',
              },
            ],
            edges: [
              {
                sourceIndex: 0,
                targetIndex: 1,
                relatedIndexes: [],
              },
              {
                sourceIndex: 1,
                targetIndex: 2,
                relatedIndexes: [],
              },
              {
                sourceIndex: 2,
                targetIndex: 3,
                relatedIndexes: [],
              },
            ],
          },
        ],
      }
      propsData.scopedComponentIds = ['test-id-2']
    })

    test('renders the scope related dependencies as scope related dependencies', async () => {
      await createWrapper()
      await wrapper.get('#graph-node-1').trigger('click')
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when a node is clicked', () => {
    beforeEach(() => {
      propsData.diagram = {
        states: [
          {
            type: 'graph',
            nodes: [
              {
                componentId: 'test-id-1',
              },
              {
                componentId: 'test-id-2',
              },
              {
                componentId: 'test-id-3',
              },
              {
                componentId: 'test-id-4',
              },
              {
                componentId: 'test-id-5',
              },
              {
                componentId: 'test-id-6',
              },
            ],
            edges: [
              {
                sourceIndex: 0,
                targetIndex: 1,
                relatedIndexes: [],
              },
              {
                sourceIndex: 1,
                targetIndex: 2,
                relatedIndexes: [],
              },
              {
                sourceIndex: 2,
                targetIndex: 3,
                relatedIndexes: [],
              },
              {
                sourceIndex: 3,
                targetIndex: 4,
                relatedIndexes: [],
              },
              {
                sourceIndex: 4,
                targetIndex: 5,
                relatedIndexes: [],
              },
            ],
          },
        ],
      }
    })

    test('renders the direct dependencies as direct dependencies', async () => {
      await createWrapper()
      await wrapper.get('#graph-node-1').trigger('click')
      expect(wrapper.html()).toMatchSnapshot()
    })

    describe('when the node is clicked a second time', () => {
      test('the node is unselected', async () => {
        await createWrapper()
        await wrapper.get('#graph-node-1').trigger('click')
        await wrapper.get('#graph-node-1').trigger('click')
        expect(wrapper.html()).toMatchSnapshot()
      })
    })

    describe('when the background is clicked', () => {
      test('the node is unselected', async () => {
        await createWrapper()
        await wrapper.get('#graph-node-1').trigger('click')
        await wrapper.get('.graph-background').trigger('click')
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when a none direct edge is a related edge', () => {
      beforeEach(() => {
        propsData.diagram.states[0].edges[2].relatedIndexes = [1]
      })

      test('renders the related dependency as a related dependency', async () => {
        await createWrapper()
        await wrapper.get('#graph-node-1').trigger('click')
        expect(wrapper.html()).toMatchSnapshot()
      })
    })
  })

  describe('when the mouse hovers over a node', () => {
    beforeEach(() => {
      propsData.diagram = {
        states: [
          {
            type: 'graph',
            nodes: [
              {
                componentId: 'test-id-1',
              },
              {
                componentId: 'test-id-2',
              },
              {
                componentId: 'test-id-3',
              },
              {
                componentId: 'test-id-4',
              },
              {
                componentId: 'test-id-5',
              },
              {
                componentId: 'test-id-6',
              },
            ],
            edges: [
              {
                sourceIndex: 0,
                targetIndex: 1,
                relatedIndexes: [],
              },
              {
                sourceIndex: 1,
                targetIndex: 2,
                relatedIndexes: [],
              },
              {
                sourceIndex: 2,
                targetIndex: 3,
                relatedIndexes: [],
              },
              {
                sourceIndex: 3,
                targetIndex: 4,
                relatedIndexes: [],
              },
              {
                sourceIndex: 4,
                targetIndex: 5,
                relatedIndexes: [],
              },
            ],
          },
        ],
      }
    })

    test('renders the direct dependencies as direct dependencies', async () => {
      await createWrapper()
      await wrapper.get('#graph-node-1').trigger('mouseover')
      expect(wrapper.html()).toMatchSnapshot()
    })

    describe('when the mouse moves off the node', () => {
      test('the node is unselected', async () => {
        await createWrapper()
        await wrapper.get('#graph-node-1').trigger('mouseover')
        await wrapper.get('#graph-node-1').trigger('mouseout')
        expect(wrapper.html()).toMatchSnapshot()
      })
    })
  })

  describe('when edgeType prop is set to related', () => {
    beforeEach(() => {
      propsData.diagram = {
        states: [
          {
            type: 'graph',
            nodes: [
              {
                componentId: 'test-id-1',
              },
              {
                componentId: 'test-id-2',
              },
              {
                componentId: 'test-id-3',
              },
              {
                componentId: 'test-id-4',
              },
              {
                componentId: 'test-id-5',
              },
              {
                componentId: 'test-id-6',
              },
            ],
            edges: [
              {
                sourceIndex: 0,
                targetIndex: 1,
                relatedIndexes: [],
              },
              {
                sourceIndex: 1,
                targetIndex: 2,
                relatedIndexes: [],
              },
              {
                sourceIndex: 2,
                targetIndex: 3,
                relatedIndexes: [1],
              },
              {
                sourceIndex: 3,
                targetIndex: 4,
                relatedIndexes: [],
              },
              {
                sourceIndex: 4,
                targetIndex: 5,
                relatedIndexes: [],
              },
            ],
          },
        ],
      }
      propsData.edgeType = 'related'
    })

    describe('when a node has not been clicked', () => {
      test('renders everything', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })
    })

    describe('when a node has been clicked', () => {
      test('renders only the related and direct dependencies', async () => {
        createWrapper()
        await wrapper.get('#graph-node-1').trigger('click')
        expect(wrapper.html()).toMatchSnapshot()
      })
    })
  })

  describe('when edgeType prop is set to direct', () => {
    beforeEach(() => {
      propsData.diagram = {
        states: [
          {
            type: 'graph',
            nodes: [
              {
                componentId: 'test-id-1',
              },
              {
                componentId: 'test-id-2',
              },
              {
                componentId: 'test-id-3',
              },
              {
                componentId: 'test-id-4',
              },
              {
                componentId: 'test-id-5',
              },
              {
                componentId: 'test-id-6',
              },
            ],
            edges: [
              {
                sourceIndex: 0,
                targetIndex: 1,
                relatedIndexes: [],
              },
              {
                sourceIndex: 1,
                targetIndex: 2,
                relatedIndexes: [],
              },
              {
                sourceIndex: 2,
                targetIndex: 3,
                relatedIndexes: [1],
              },
              {
                sourceIndex: 3,
                targetIndex: 4,
                relatedIndexes: [],
              },
              {
                sourceIndex: 4,
                targetIndex: 5,
                relatedIndexes: [],
              },
            ],
          },
        ],
      }
      propsData.edgeType = 'direct'
    })

    describe('when a node has not been clicked', () => {
      test('renders everything', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })
    })

    describe('when a node has been clicked', () => {
      test('renders only the direct dependencies', async () => {
        createWrapper()
        await wrapper.get('#graph-node-1').trigger('click')
        expect(wrapper.html()).toMatchSnapshot()
      })
    })
  })
})
