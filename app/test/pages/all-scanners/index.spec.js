import Index from '~/pages/all-scanners/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'

describe('Index', () => {
  let scanners
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      serviceRequests: {
        '/v1/scanners?fields=scanners(id,description,notes)': {
          responseBody: { scanners },
        },
      },
    })
  }

  beforeEach(() => {
    scanners = []
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  test('has the right page title', async () => {
    await createWrapper()
    expect(wrapper.vm.$metaInfo.title).toBe('Component Catalog - All Scanners')
  })

  describe('when Get Scanners service endpoint returns an empty array', () => {
    test('renders the page with an empty table', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })

  describe('when Get Scanners service endpoint returns an array of multiple scanners', () => {
    beforeEach(() => {
      scanners.push(
        {
          id: 'test-scanner-id-1',
          description: 'Test Scanner Description 1',
          notes: 'Test Scanner Notes 1',
        },
        {
          id: 'test-scanner-id-2',
          description: 'Test Scanner Description 2',
        }
      )
    })

    test('renders the page with a table showing the details of the scanners', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })
})
