import Index from '~/pages/all-components/tests/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createComponent,
  createComponentWithTestResults,
} from '~/test/testDataUtils'

describe('Index', () => {
  let components = []
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      serviceRequests: {
        '/v1/components?testOutcome=fail&fields=components(id,name,typeId,tags,teams,platformId,testResults)':
          {
            responseBody: { components },
          },
      },
    })
  }

  beforeEach(() => {
    components = []
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  test('has the right page title', async () => {
    await createWrapper()
    expect(wrapper.vm.$metaInfo.title).toBe(
      'Kronicle - All Components - Tests'
    )
  })

  describe('when Get Components service endpoint returns an empty array', () => {
    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })

  describe('when Get Components service endpoint returns an array of multiple components', () => {
    beforeEach(() => {
      components = [
        createComponentWithTestResults({ componentNumber: 1 }),
        createComponent({ componentNumber: 2 }),
        createComponentWithTestResults({ componentNumber: 3 }),
      ]
    })

    test('renders the test results, including test failure counts', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
      expect(wrapper.findAll('table tbody tr')).toHaveLength(4)
    })
  })
})
