import { mount } from '@vue/test-utils'
import ComponentDependencyGraph from '@/components/ComponentDependencyGraph.vue'

describe('ComponentDependencyGraph', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(ComponentDependencyGraph, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when dependencies prop is not set', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(``)
    })
  })

  describe('when dependencies prop is set to multiple nodes and dependencies for component dependencies', () => {
    beforeEach(() => {
      propsData.dependencies = {
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
        dependencies: [
          {
            sourceIndex: 0,
            targetIndex: 1,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 1,
            targetIndex: 2,
            relatedIndexes: [],
            manual: false,
          },
        ],
      }
    })

    test('renders dependencies, dependency markers, nodes and node labels', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })

    describe('when there is a manual dependency', () => {
      beforeEach(() => {
        propsData.dependencies.dependencies[0].manual = true
      })

      test('renders the manual dependency as a manual dependency, the none shared node of the manual dependency as a manual node and the other dependency as a normal dependency', async () => {
        await createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })
    })
  })

  describe('when dependencies prop is set to multiple nodes and dependencies for sub component dependencies', () => {
    beforeEach(() => {
      propsData.dependencies = {
        nodes: [
          {
            componentId: 'test-id-1',
            spanName: 'test span 1',
            tags: {
              'test.tag.1': 'test-tag-value-1',
              'test.tag.2': 'test-tag-value-2',
            },
          },
          {
            componentId: 'test-id-2',
            spanName: 'test span 2',
            tags: {},
          },
          {
            componentId: 'test-id-3',
            spanName: 'test span 3',
            tags: {},
          },
        ],
        dependencies: [
          {
            sourceIndex: 0,
            targetIndex: 1,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 1,
            targetIndex: 2,
            relatedIndexes: [],
            manual: false,
          },
        ],
      }
    })

    test('renders dependencies, dependency markers, nodes and node labels', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when there are multiple nodes at the same depth', () => {
    beforeEach(() => {
      propsData.dependencies = {
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
        dependencies: [
          {
            sourceIndex: 0,
            targetIndex: 1,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 1,
            targetIndex: 2,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 0,
            targetIndex: 3,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 3,
            targetIndex: 4,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 4,
            targetIndex: 5,
            relatedIndexes: [],
            manual: false,
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
      propsData.dependencies = {
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
        dependencies: [
          {
            sourceIndex: 0,
            targetIndex: 1,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 1,
            targetIndex: 2,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 2,
            targetIndex: 3,
            relatedIndexes: [],
            manual: false,
          },
        ],
      }
      propsData.scopedComponentIds = ['test-id-2']
    })

    test('renders the scope related dependencies as scope related dependencies', async () => {
      await createWrapper()
      await wrapper.get('#component-dependency-graph-node-1').trigger('click')
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when a node is clicked', () => {
    beforeEach(() => {
      propsData.dependencies = {
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
        dependencies: [
          {
            sourceIndex: 0,
            targetIndex: 1,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 1,
            targetIndex: 2,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 2,
            targetIndex: 3,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 3,
            targetIndex: 4,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 4,
            targetIndex: 5,
            relatedIndexes: [],
            manual: false,
          },
        ],
      }
    })

    test('renders the direct dependencies as direct dependencies', async () => {
      await createWrapper()
      await wrapper.get('#component-dependency-graph-node-1').trigger('click')
      expect(wrapper.html()).toMatchSnapshot()
    })

    describe('when the node is clicked a second time', () => {
      test('the node is unselected', async () => {
        await createWrapper()
        await wrapper.get('#component-dependency-graph-node-1').trigger('click')
        await wrapper.get('#component-dependency-graph-node-1').trigger('click')
        expect(wrapper.html()).toMatchSnapshot()
      })
    })

    describe('when the background is clicked', () => {
      test('the node is unselected', async () => {
        await createWrapper()
        await wrapper.get('#component-dependency-graph-node-1').trigger('click')
        await wrapper
          .get('.component-dependency-graph-background')
          .trigger('click')
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when one of the direct dependencies is also a manual dependency', () => {
      beforeEach(() => {
        propsData.dependencies.dependencies[1].manual = true
      })

      test('still renders the direct dependency as a direct dependency', async () => {
        await createWrapper()
        await wrapper.get('#component-dependency-graph-node-1').trigger('click')
        expect(wrapper.html()).toMatchSnapshot()
      })
    })

    describe('when a none direct dependency is a related dependency', () => {
      beforeEach(() => {
        propsData.dependencies.dependencies[2].relatedIndexes = [1]
      })

      test('renders the related dependency as a related dependency', async () => {
        await createWrapper()
        await wrapper.get('#component-dependency-graph-node-1').trigger('click')
        expect(wrapper.html()).toMatchSnapshot()
      })

      describe('when the related dependencies is also a manual dependency', () => {
        beforeEach(() => {
          propsData.dependencies.dependencies[2].manual = true
        })

        test('still renders the related dependency as a related dependency', async () => {
          await createWrapper()
          await wrapper
            .get('#component-dependency-graph-node-1')
            .trigger('click')
          expect(wrapper.html()).toMatchSnapshot()
        })
      })

      describe('when a none direct and none related dependency is a manual dependency', () => {
        beforeEach(() => {
          propsData.dependencies.dependencies[4].manual = true
        })

        test('renders the manual dependency as a manual dependency', async () => {
          await createWrapper()
          await wrapper
            .get('#component-dependency-graph-node-1')
            .trigger('click')
          expect(wrapper.html()).toMatchSnapshot()
        })
      })
    })
  })

  describe('when the mouse hovers over a node', () => {
    beforeEach(() => {
      propsData.dependencies = {
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
        dependencies: [
          {
            sourceIndex: 0,
            targetIndex: 1,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 1,
            targetIndex: 2,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 2,
            targetIndex: 3,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 3,
            targetIndex: 4,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 4,
            targetIndex: 5,
            relatedIndexes: [],
            manual: false,
          },
        ],
      }
    })

    test('renders the direct dependencies as direct dependencies', async () => {
      await createWrapper()
      await wrapper
        .get('#component-dependency-graph-node-1')
        .trigger('mouseover')
      expect(wrapper.html()).toMatchSnapshot()
    })

    describe('when the mouse moves off the node', () => {
      test('the node is unselected', async () => {
        await createWrapper()
        await wrapper
          .get('#component-dependency-graph-node-1')
          .trigger('mouseover')
        await wrapper
          .get('#component-dependency-graph-node-1')
          .trigger('mouseout')
        expect(wrapper.html()).toMatchSnapshot()
      })
    })
  })

  describe('when dependencyType prop is set to related', () => {
    beforeEach(() => {
      propsData.dependencies = {
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
        dependencies: [
          {
            sourceIndex: 0,
            targetIndex: 1,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 1,
            targetIndex: 2,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 2,
            targetIndex: 3,
            relatedIndexes: [1],
            manual: false,
          },
          {
            sourceIndex: 3,
            targetIndex: 4,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 4,
            targetIndex: 5,
            relatedIndexes: [],
            manual: true,
          },
        ],
      }
      propsData.dependencyType = 'related'
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
        await wrapper.get('#component-dependency-graph-node-1').trigger('click')
        expect(wrapper.html()).toMatchSnapshot()
      })
    })
  })

  describe('when dependencyType prop is set to direct', () => {
    beforeEach(() => {
      propsData.dependencies = {
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
        dependencies: [
          {
            sourceIndex: 0,
            targetIndex: 1,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 1,
            targetIndex: 2,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 2,
            targetIndex: 3,
            relatedIndexes: [1],
            manual: false,
          },
          {
            sourceIndex: 3,
            targetIndex: 4,
            relatedIndexes: [],
            manual: false,
          },
          {
            sourceIndex: 4,
            targetIndex: 5,
            relatedIndexes: [],
            manual: true,
          },
        ],
      }
      propsData.dependencyType = 'direct'
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
        await wrapper.get('#component-dependency-graph-node-1').trigger('click')
        expect(wrapper.html()).toMatchSnapshot()
      })
    })
  })
})
