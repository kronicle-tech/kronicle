import { mount } from '@vue/test-utils'
import ComponentCallGraphsView from '@/components/ComponentCallGraphsView.vue'
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

describe('ComponentCallGraphsView', () => {
  let propsData
  let wrapper

  const createWrapper = () => {
    wrapper = mount(ComponentCallGraphsView, { propsData })
  }

  beforeEach(() => {
    propsData = {
      component: createComponent({ componentNumber: 1 }),
      callGraphs: [],
    }
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when diagrams prop is set to an empty array', () => {
    beforeEach(() => {
      propsData.diagrams = []
    })

    test('renders a message saying there are no call graphs available', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expect(wrapper.text()).toEqual(
        expect.stringContaining(
          'No call graphs are available for this component'
        )
      )
      expectCallGraphCount(wrapper, 0)
      expectNodeCount(wrapper, 0)
    })
  })

  describe('when there is 1 diagram', () => {
    beforeEach(() => {
      propsData.diagrams = [
        {
          states: {
            nodes: [
              createSubComponentNode({ componentNodeNumber: 1 }),
              createSubComponentNode({ componentNodeNumber: 2 }),
            ],
            edges: [createDependency({ sourceIndex: 0, targetIndex: 1 })],
            sampleSize: 1,
          },
        },
      ]
    })

    test('renders the page with 1 call graph on the left and 1 node on the right and the node should be selected', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectCallGraphCount(wrapper, 1)
      expectNodeCount(wrapper, 1)
      expectNodeVariants(wrapper, ['success'])
    })
  })

  describe('when there are 2 nodes and the first node has 1 call graph and the second node has 2 call graphs', () => {
    beforeEach(() => {
      propsData.diagrams = [
        {
          states: {
            nodes: [
              createSubComponentNode({
                componentNodeNumber: 1,
                subComponentNodeNumber: 1,
              }),
              createSubComponentNode({ componentNodeNumber: 2 }),
            ],
            edges: [createDependency({ sourceIndex: 0, targetIndex: 1 })],
            sampleSize: 1,
          },
        },
        {
          states: {
            nodes: [
              createSubComponentNode({
                componentNodeNumber: 1,
                subComponentNodeNumber: 2,
              }),
              createSubComponentNode({ componentNodeNumber: 2 }),
            ],
            edges: [createDependency({ sourceIndex: 0, targetIndex: 1 })],
            sampleSize: 1,
          },
        },
        {
          states: {
            nodes: [
              createSubComponentNode({
                componentNodeNumber: 1,
                subComponentNodeNumber: 2,
              }),
              createSubComponentNode({ componentNodeNumber: 3 }),
            ],
            edges: [createDependency({ sourceIndex: 0, targetIndex: 1 })],
            sampleSize: 1,
          },
        },
      ]
    })

    test('renders the page with 1 call graph on the left and 2 nodes on the right and the first node should be selected', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectCallGraphCount(wrapper, 1)
      expectNodeCount(wrapper, 2)
      expectNodeVariants(wrapper, ['success', 'secondary'])
    })

    describe('when the second node is clicked', () => {
      beforeEach(async () => {
        createWrapper()
        await wrapper.findAll('.nodes .node').at(1).trigger('click')
      })

      test('changes the selected node to be the second node and shows the call graphs for the second node', () => {
        expect(wrapper.html()).toMatchSnapshot()
        expectCallGraphCount(wrapper, 2)
        expectNodeCount(wrapper, 2)
        expectNodeVariants(wrapper, ['secondary', 'success'])
      })
    })
  })

  describe("when a call graph's node has a different number of tags to the selected node", () => {
    beforeEach(() => {
      propsData.diagrams = [
        {
          states: {
            nodes: [
              {
                componentId: 'test-component-id-1',
                spanName: 'test-span-name-1-1',
                tags: {
                  testName1: 'test-value-1',
                },
              },
              {
                componentId: 'test-component-id-2',
                spanName: 'test-span-name-2-1',
                tags: {},
              },
            ],
            edges: [createDependency({ sourceIndex: 0, targetIndex: 1 })],
            sampleSize: 1,
          },
        },
        {
          states: {
            nodes: [
              {
                componentId: 'test-component-id-1',
                spanName: 'test-span-name-1-1',
                tags: {
                  testName1: 'test-value-1',
                  testName2: 'test-value-2',
                },
              },
              {
                componentId: 'test-component-id-2',
                spanName: 'test-span-name-2-1',
                tags: {},
              },
            ],
            edges: [createDependency({ sourceIndex: 0, targetIndex: 1 })],
            sampleSize: 1,
          },
        },
      ]
    })

    test('the call graph is not shown for the selected node', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectCallGraphCount(wrapper, 1)
      expectNodeCount(wrapper, 2)
      expectNodeVariants(wrapper, ['success', 'secondary'])
    })
  })

  describe("when a call graph's node has the same number of tags to the selected node but different values", () => {
    beforeEach(() => {
      propsData.diagrams = [
        {
          states: {
            nodes: [
              {
                componentId: 'test-component-id-1',
                spanName: 'test-span-name-1-1',
                tags: {
                  testName1: 'test-value-1',
                  testName2: 'test-value-2',
                },
              },
              {
                componentId: 'test-component-id-2',
                spanName: 'test-span-name-2-1',
                tags: {},
              },
            ],
            edges: [createDependency({ sourceIndex: 0, targetIndex: 1 })],
            sampleSize: 1,
          },
        },
        {
          states: {
            nodes: [
              {
                componentId: 'test-component-id-1',
                spanName: 'test-span-name-1-1',
                tags: {
                  testName1: 'different-value-1',
                  testName2: 'different-value-2',
                },
              },
              {
                componentId: 'test-component-id-2',
                spanName: 'test-span-name-2-1',
                tags: {},
              },
            ],
            edges: [createDependency({ sourceIndex: 0, targetIndex: 1 })],
            sampleSize: 1,
          },
        },
      ]
    })

    test('the call graph is not shown for the selected node', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectCallGraphCount(wrapper, 1)
      expectNodeCount(wrapper, 2)
      expectNodeVariants(wrapper, ['success', 'secondary'])
    })
  })
})
