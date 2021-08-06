import { mount } from '@vue/test-utils'
import ComponentResponseTimesView from '@/components/ComponentResponseTimesView.vue'

describe('ComponentResponseTimesView', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(ComponentResponseTimesView, { propsData })
  }

  beforeEach(() => {
    propsData = {
      componentId: 'test-component-id-1',
      direction: 'upstream',
      allComponents: [],
    }
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when subComponentDependencies prop is not set', () => {
    test('renders a message saying there are no response times available', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when direction prop is set to upstream', () => {
    beforeEach(() => {
      propsData.direction = 'upstream'
    })

    describe('when no sub component dependencies match the componentId prop', () => {
      beforeEach(() => {
        propsData.subComponentDependencies = {
          nodes: [
            {
              componentId: 'test-component-id-2',
              spanName: 'test-component-id-2-span-1',
            },
            {
              componentId: 'test-component-id-3',
              spanName: 'test-component-id-3-span-1',
            },
          ],
          dependencies: [
            {
              sourceIndex: 0,
              targetIndex: 1,
              startTimestamp: '2021-01-01T02:03',
              endTimestamp: '2021-01-02T03:04',
              sampleSize: 123,
              duration: {
                min: 11000,
                max: 12000,
                p50: 13000,
                p90: 14000,
                p99: 15000,
                p99Point9: 16000,
              },
            },
          ],
        }
      })

      test('renders a message saying there are no response times available', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })
    })

    describe('when no target of a sub component dependency matches the componentId prop', () => {
      beforeEach(() => {
        propsData.subComponentDependencies = {
          nodes: [
            {
              componentId: 'test-component-id-1',
              spanName: 'test-component-id-1-span-1',
            },
            {
              componentId: 'test-component-id-2',
              spanName: 'test-component-id-2-span-1',
            },
          ],
          dependencies: [
            {
              sourceIndex: 0,
              targetIndex: 1,
              startTimestamp: '2021-01-01T02:03',
              endTimestamp: '2021-01-02T03:04',
              sampleSize: 123,
              duration: {
                min: 11000,
                max: 12000,
                p50: 13000,
                p90: 14000,
                p99: 15000,
                p99Point9: 16000,
              },
            },
          ],
        }
      })

      test('renders a message saying there are no response times available', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })
    })

    describe('when the target of a sub component dependency matches the componentId prop', () => {
      beforeEach(() => {
        propsData.subComponentDependencies = {
          nodes: [
            {
              componentId: 'test-component-id-1',
              spanName: 'test-component-id-1-span-1',
            },
            {
              componentId: 'test-component-id-2',
              spanName: 'test-component-id-2-span-1',
            },
          ],
          dependencies: [
            {
              sourceIndex: 1,
              targetIndex: 0,
              startTimestamp: '2021-01-01T02:03',
              endTimestamp: '2021-01-02T03:04',
              sampleSize: 123,
              duration: {
                min: 11000,
                max: 12000,
                p50: 13000,
                p90: 14000,
                p99: 15000,
                p99Point9: 16000,
              },
            },
          ],
        }
      })

      test('renders the details of the sub component dependency', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })
    })

    describe('when the targets of multiple sub component dependencies match the componentId prop', () => {
      beforeEach(() => {
        propsData.subComponentDependencies = {
          nodes: [
            {
              componentId: 'test-component-id-1',
              spanName: 'test-component-id-1-span-1',
            },
            {
              componentId: 'test-component-id-2',
              spanName: 'test-component-id-2-span-1',
            },
            {
              componentId: 'test-component-id-3',
              spanName: 'test-component-id-3-span-1',
            },
          ],
          dependencies: [
            {
              sourceIndex: 1,
              targetIndex: 0,
              startTimestamp: '2021-01-01T02:03',
              endTimestamp: '2021-01-02T03:04',
              sampleSize: 123,
              duration: {
                min: 11000,
                max: 12000,
                p50: 13000,
                p90: 14000,
                p99: 15000,
                p99Point9: 16000,
              },
            },
            {
              sourceIndex: 2,
              targetIndex: 0,
              startTimestamp: '2021-01-01T02:03',
              endTimestamp: '2021-01-02T03:04',
              sampleSize: 123,
              duration: {
                min: 11000,
                max: 12000,
                p50: 13000,
                p90: 14000,
                p99: 15000,
                p99Point9: 16000,
              },
            },
          ],
        }
      })

      test('renders the details of the sub component dependencies', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })
    })

    describe('when the source and target of a sub component dependency matches the componentId prop', () => {
      beforeEach(() => {
        propsData.subComponentDependencies = {
          nodes: [
            {
              componentId: 'test-component-id-1',
              spanName: 'test-component-id-1-span-1',
            },
            {
              componentId: 'test-component-id-2',
              spanName: 'test-component-id-2-span-1',
            },
          ],
          dependencies: [
            {
              sourceIndex: 0,
              targetIndex: 0,
              startTimestamp: '2021-01-01T02:03',
              endTimestamp: '2021-01-02T03:04',
              sampleSize: 123,
              duration: {
                min: 11000,
                max: 12000,
                p50: 13000,
                p90: 14000,
                p99: 15000,
                p99Point9: 16000,
              },
            },
          ],
        }
      })

      test('renders a message saying there are no response times available', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })
    })

    describe("when a sub component dependency's source is undefined and target matches the componentId prop", () => {
      beforeEach(() => {
        propsData.subComponentDependencies = {
          nodes: [
            {
              componentId: 'test-component-id-1',
              spanName: 'test-component-id-1-span-1',
            },
            {
              componentId: 'test-component-id-2',
              spanName: 'test-component-id-2-span-1',
            },
          ],
          dependencies: [
            {
              targetIndex: 0,
              startTimestamp: '2021-01-01T02:03',
              endTimestamp: '2021-01-02T03:04',
              sampleSize: 123,
              duration: {
                min: 11000,
                max: 12000,
                p50: 13000,
                p90: 14000,
                p99: 15000,
                p99Point9: 16000,
              },
            },
          ],
        }
      })

      test('renders the details of the sub component dependency', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })
    })
  })

  describe('when direction prop is set to downstream', () => {
    beforeEach(() => {
      propsData.direction = 'downstream'
    })

    describe('when no sub component dependencies match the componentId prop', () => {
      beforeEach(() => {
        propsData.subComponentDependencies = {
          nodes: [
            {
              componentId: 'test-component-id-2',
              spanName: 'test-component-id-2-span-1',
            },
            {
              componentId: 'test-component-id-3',
              spanName: 'test-component-id-3-span-1',
            },
          ],
          dependencies: [
            {
              sourceIndex: 0,
              targetIndex: 1,
              startTimestamp: '2021-01-01T02:03',
              endTimestamp: '2021-01-02T03:04',
              sampleSize: 123,
              duration: {
                min: 11000,
                max: 12000,
                p50: 13000,
                p90: 14000,
                p99: 15000,
                p99Point9: 16000,
              },
            },
          ],
        }
      })

      test('renders a message saying there are no response times available', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })
    })

    describe('when no source of a sub component dependency matches the componentId prop', () => {
      beforeEach(() => {
        propsData.subComponentDependencies = {
          nodes: [
            {
              componentId: 'test-component-id-1',
              spanName: 'test-component-id-1-span-1',
            },
            {
              componentId: 'test-component-id-2',
              spanName: 'test-component-id-2-span-1',
            },
          ],
          dependencies: [
            {
              sourceIndex: 1,
              targetIndex: 0,
              startTimestamp: '2021-01-01T02:03',
              endTimestamp: '2021-01-02T03:04',
              sampleSize: 123,
              duration: {
                min: 11000,
                max: 12000,
                p50: 13000,
                p90: 14000,
                p99: 15000,
                p99Point9: 16000,
              },
            },
          ],
        }
      })

      test('renders a message saying there are no response times available', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })
    })

    describe('when the source of a sub component dependency matches the componentId prop', () => {
      beforeEach(() => {
        propsData.subComponentDependencies = {
          nodes: [
            {
              componentId: 'test-component-id-1',
              spanName: 'test-component-id-1-span-1',
            },
            {
              componentId: 'test-component-id-2',
              spanName: 'test-component-id-2-span-1',
            },
          ],
          dependencies: [
            {
              sourceIndex: 0,
              targetIndex: 1,
              startTimestamp: '2021-01-01T02:03',
              endTimestamp: '2021-01-02T03:04',
              sampleSize: 123,
              duration: {
                min: 11000,
                max: 12000,
                p50: 13000,
                p90: 14000,
                p99: 15000,
                p99Point9: 16000,
              },
            },
          ],
        }
      })

      test('renders the details of the sub component dependency', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })
    })

    describe('when the sources of multiple sub component dependencies match the componentId prop', () => {
      beforeEach(() => {
        propsData.subComponentDependencies = {
          nodes: [
            {
              componentId: 'test-component-id-1',
              spanName: 'test-component-id-1-span-1',
            },
            {
              componentId: 'test-component-id-2',
              spanName: 'test-component-id-2-span-1',
            },
            {
              componentId: 'test-component-id-3',
              spanName: 'test-component-id-3-span-1',
            },
          ],
          dependencies: [
            {
              sourceIndex: 0,
              targetIndex: 1,
              startTimestamp: '2021-01-01T02:03',
              endTimestamp: '2021-01-02T03:04',
              sampleSize: 123,
              duration: {
                min: 11000,
                max: 12000,
                p50: 13000,
                p90: 14000,
                p99: 15000,
                p99Point9: 16000,
              },
            },
            {
              sourceIndex: 0,
              targetIndex: 2,
              startTimestamp: '2021-01-01T02:03',
              endTimestamp: '2021-01-02T03:04',
              sampleSize: 123,
              duration: {
                min: 11000,
                max: 12000,
                p50: 13000,
                p90: 14000,
                p99: 15000,
                p99Point9: 16000,
              },
            },
          ],
        }
      })

      test('renders the details of the sub component dependencies', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })
    })

    describe("when a sub component dependency's source is undefined and target matches the componentId prop", () => {
      beforeEach(() => {
        propsData.subComponentDependencies = {
          nodes: [
            {
              componentId: 'test-component-id-1',
              spanName: 'test-component-id-1-span-1',
            },
            {
              componentId: 'test-component-id-2',
              spanName: 'test-component-id-2-span-1',
            },
          ],
          dependencies: [
            {
              targetIndex: 0,
              startTimestamp: '2021-01-01T02:03',
              endTimestamp: '2021-01-02T03:04',
              sampleSize: 123,
              duration: {
                min: 11000,
                max: 12000,
                p50: 13000,
                p90: 14000,
                p99: 15000,
                p99Point9: 16000,
              },
            },
          ],
        }
      })

      test('renders a message saying there are no response times available', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })
    })
  })

  describe('when a matching sub component dependency has tags', () => {
    beforeEach(() => {
      propsData.subComponentDependencies = {
        nodes: [
          {
            componentId: 'test-component-id-1',
            spanName: 'test-component-id-1-span-1',
            tags: {
              'test-component-id-1-span-1-tag-1': 'test-value-1',
              'test-component-id-1-span-1-tag-2': 'test-value-2',
            },
          },
          {
            componentId: 'test-component-id-2',
            spanName: 'test-component-id-2-span-1',
            tags: {
              'test-component-id-2-span-1-tag-2': 'test-value-2',
              'test-component-id-2-span-1-tag-1': 'test-value-1',
            },
          },
        ],
        dependencies: [
          {
            sourceIndex: 1,
            targetIndex: 0,
            startTimestamp: '2021-01-01T02:03',
            endTimestamp: '2021-01-02T03:04',
            sampleSize: 123,
            duration: {
              min: 11000,
              max: 12000,
              p50: 13000,
              p90: 14000,
              p99: 15000,
              p99Point9: 16000,
            },
          },
        ],
      }
    })

    test('the tags are rendered in alphabetical order', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe("when a sub component dependency's source matches a known component", () => {
    beforeEach(() => {
      propsData.subComponentDependencies = {
        nodes: [
          {
            componentId: 'test-component-id-1',
            spanName: 'test-component-id-1-span-1',
          },
          {
            componentId: 'test-component-id-2',
            spanName: 'test-component-id-2-span-1',
          },
        ],
        dependencies: [
          {
            sourceIndex: 1,
            targetIndex: 0,
            startTimestamp: '2021-01-01T02:03',
            endTimestamp: '2021-01-02T03:04',
            sampleSize: 123,
            duration: {
              min: 11000,
              max: 12000,
              p50: 13000,
              p90: 14000,
              p99: 15000,
              p99Point9: 16000,
            },
          },
        ],
      }
      propsData.allComponents = [
        {
          id: 'test-component-id-2',
          name: 'Test Component Name 2',
        },
      ]
    })

    test("renders the component's name instead of the component's id and an `unrecognised component` badge", () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe("when a sub component dependency's target matches a known component", () => {
    beforeEach(() => {
      propsData.subComponentDependencies = {
        nodes: [
          {
            componentId: 'test-component-id-1',
            spanName: 'test-component-id-1-span-1',
          },
          {
            componentId: 'test-component-id-2',
            spanName: 'test-component-id-2-span-1',
          },
        ],
        dependencies: [
          {
            sourceIndex: 1,
            targetIndex: 0,
            startTimestamp: '2021-01-01T02:03',
            endTimestamp: '2021-01-02T03:04',
            sampleSize: 123,
            duration: {
              min: 11000,
              max: 12000,
              p50: 13000,
              p90: 14000,
              p99: 15000,
              p99Point9: 16000,
            },
          },
        ],
      }
      propsData.allComponents = [
        {
          id: 'test-component-id-1',
          name: 'Test Component Name 1',
        },
      ]
    })

    test("renders the component's name instead of the component's id and an `unrecognised component` badge", () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when there is no duration object for multiple sub component dependencies', () => {
    beforeEach(() => {
      propsData.subComponentDependencies = {
        nodes: [
          {
            componentId: 'test-component-id-1',
            spanName: 'test-component-id-1-span-1',
          },
          {
            componentId: 'test-component-id-2',
            spanName: 'test-component-id-2-span-1',
          },
          {
            componentId: 'test-component-id-3',
            spanName: 'test-component-id-3-span-1',
          },
        ],
        dependencies: [
          {
            sourceIndex: 1,
            targetIndex: 0,
            startTimestamp: '2021-01-01T02:03',
            endTimestamp: '2021-01-02T03:04',
            sampleSize: 123,
          },
          {
            sourceIndex: 2,
            targetIndex: 0,
            startTimestamp: '2021-01-01T02:03',
            endTimestamp: '2021-01-02T03:04',
            sampleSize: 123,
          },
        ],
      }
    })

    test('renders the word `unknown` for each duration stat', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })
})
