import { createLocalVue } from '@vue/test-utils'
import VueMeta from 'vue-meta'
import Index from '@/pages/areas/_areaId/tests/_testId/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createArea,
  createComponent,
  createComponentWithTestResults,
  createTest,
} from '~/test/testDataUtils'

const localVue = createLocalVue()
localVue.use(VueMeta, { keyName: 'head' })

describe('Index', () => {
  const route = {
    params: {
      areaId: 'test-area-id-1',
      testId: 'test-test-id-1',
    },
  }
  let testObject
  let area
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      route,
      serviceRequests: {
        '/v1/tests/test-test-id-1?fields=test(id,description,priority)': {
          responseBody: { test: testObject },
        },
        '/v1/areas/test-area-id-1?fields=area(id,name,components(id,name,typeId,tags,teams,platformId,testResults))':
          {
            responseBody: { area },
          },
      },
    })
  }

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when Get Test service endpoint returns a test and the Get Area service endpoint returns an area', () => {
    beforeEach(() => {
      testObject = createTest({ testNumber: 1 })
      area = createArea({ areaNumber: 1 })
    })

    test('has the right page title', async () => {
      await createWrapper()
      expect(wrapper.vm.$metaInfo.title).toBe(
        'Kronicle - Test Area Name 1 Area - test-test-id-1 Test'
      )
    })

    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })

    describe('when there are test results for the test', () => {
      beforeEach(() => {
        area.components = [
          createComponentWithTestResults({ componentNumber: 1 }),
          createComponent({ componentNumber: 2 }),
          createComponentWithTestResults({ componentNumber: 3 }),
        ]
      })

      test('renders the test results', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
