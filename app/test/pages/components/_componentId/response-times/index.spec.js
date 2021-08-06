import Index from '@/pages/components/_componentId/response-times/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'

describe('Index', () => {
  const route = {
    params: {
      componentId: 'test-component-id-1',
    },
  }
  let component
  const components = [
    {
      id: 'test-component-id-1',
      name: 'Test Component Name 1',
    },
    {
      id: 'test-component-id-2',
      name: 'Test Component Name 2',
    },
    {
      id: 'test-component-id-3',
      name: 'Test Component Name 3',
    },
  ]
  const summary = {}
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      route,
      serviceRequests: {
        '/v1/components/test-component-id-1?fields=component(id,name)': {
          responseBody: { component },
        },
        '/v1/components?fields=components(id,name)': {
          responseBody: { components },
        },
        '/v1/summary?fields=summary(subComponentDependencies)': {
          responseBody: { summary },
        },
      },
    })
  }

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when Get Component service endpoint returns a component', () => {
    beforeEach(() => {
      component = {
        id: 'test-component-id-1',
        name: 'Test Component Name 1',
      }
    })

    test('has the right page title', async () => {
      await createWrapper()
      expect(wrapper.vm.$metaInfo.title).toBe(
        'Component Catalog - Test Component Name 1 - Response Times'
      )
    })

    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })

    describe('when Get Summary service endpoint returns a summary with sub component dependencies', () => {
      beforeEach(() => {
        summary.subComponentDependencies = {
          nodes: [
            {
              componentId: 'test-component-id-1',
              spanName: 'test-component-1-span-id-1',
              tags: {
                'test-component-1-tag-name-1': 'test-tag-value-1',
                'test-component-1-tag-name-2': 'test-tag-value-2',
              },
            },
            {
              componentId: 'test-component-id-1',
              spanName: 'test-component-1-span-id-2',
              tags: {
                'test-component-1-tag-name-1': 'test-tag-value-1',
                'test-component-1-tag-name-2': 'test-tag-value-2',
              },
            },
            {
              componentId: 'test-component-id-2',
              spanName: 'test-component-2-span-id-1',
              tags: {
                'test-component-2-tag-name-1': 'test-tag-value-1',
                'test-component-2-tag-name-2': 'test-tag-value-2',
              },
            },
            {
              componentId: 'test-component-id-2',
              spanName: 'test-component-2-span-id-2',
              tags: {
                'test-component-2-tag-name-1': 'test-tag-value-1',
                'test-component-2-tag-name-2': 'test-tag-value-2',
              },
            },
          ],
          dependencies: [
            {
              sourceIndex: 2,
              targetIndex: 0,
              sampleSize: 1,
              duration: {
                min: 1001,
                max: 1006,
                p50: 1002,
                p90: 1003,
                p99: 1004,
                p99Point9: 1005,
              },
            },
            {
              sourceIndex: 3,
              targetIndex: 1,
              sampleSize: 2,
              duration: {
                min: 2001,
                max: 2006,
                p50: 2002,
                p90: 2003,
                p99: 2004,
                p99Point9: 2005,
              },
            },
          ],
        }
      })

      describe('when the sub component dependencies are for a different component', () => {
        beforeEach(() => {
          component.id = 'test-component-id-3'
          component.name = 'Test Component Name 3'
        })

        test('renders no response times', async () => {
          await createWrapper()
          expect(wrapper.element).toMatchSnapshot()
        })
      })

      describe('when the sub component dependencies are for the same component', () => {
        test('renders the response times', async () => {
          await createWrapper()
          expect(wrapper.element).toMatchSnapshot()
        })
      })
    })
  })
})
