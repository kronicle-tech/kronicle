import { createLocalVue } from '@vue/test-utils'
import VueMeta from 'vue-meta'
import Index from '@/pages/tests/_testId/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'

const localVue = createLocalVue()
localVue.use(VueMeta, { keyName: 'head' })

describe('Index', () => {
  const route = {
    params: {
      testId: 'test-test-id-1',
    },
  }
  let testObject
  let components
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      route,
      serviceRequests: {
        '/v1/tests/test-test-id-1?fields=test(id,description,priority)': {
          responseBody: { test: testObject },
        },
        '/v1/components?fields=components(id,name,typeId,tags,teams,platformId,testResults)':
          {
            responseBody: { components },
          },
      },
    })
  }

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when Get Test service endpoint returns a test', () => {
    beforeEach(() => {
      testObject = {
        id: 'test-test-id-1',
        description: 'Test Test Description 1',
        notes: 'Test Test Notes 1',
        priority: 'very-high',
      }
      components = [
        {
          id: 'test-component-id-1',
          typeId: 'test-component-type-id-1',
          name: 'Test Component Name 1',
        },
      ]
    })

    test('has the right page title', async () => {
      await createWrapper()
      expect(wrapper.vm.$metaInfo.title).toBe('Kronicle - test-test-id-1 Test')
    })

    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })

    describe('when there are test results for the test', () => {
      beforeEach(() => {
        components = [
          {
            id: 'test-component-id-1',
            typeId: 'test-component-type-id-1',
            name: 'Test Component Name 1',
            testResults: [
              {
                testId: 'test-test-id-1',
                priority: 'very-high',
                outcome: 'pass',
                message: 'Test Test Message 1a',
              },
              {
                testId: 'test-test-id-2',
                priority: 'very-high',
                outcome: 'fail',
                message: 'Test Test Message 1b',
              },
            ],
          },
          {
            id: 'test-component-id-2',
            type: 'test-component-type-id-2',
            name: 'Test Component Name 2',
            testResults: [
              {
                testId: 'test-test-id-1',
                priority: 'very-high',
                outcome: 'fail',
                message: 'Test Test Message 2a',
              },
              {
                testId: 'test-test-id-2',
                priority: 'very-high',
                outcome: 'not-applicable',
                message: 'Test Test Message 2b',
              },
            ],
          },
        ]
      })

      test('renders the test results', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
