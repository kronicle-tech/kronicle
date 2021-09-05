import Index from '~/pages/all-areas/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'

describe('Index', () => {
  let areas
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      serviceRequests: {
        '/v1/areas?fields=areas(id,name,description)': {
          responseBody: { areas },
        },
      },
    })
  }

  beforeEach(() => {
    areas = []
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  test('has the right page title', async () => {
    await createWrapper()
    expect(wrapper.vm.$metaInfo.title).toBe('Kronicle - All Areas')
  })

  describe('when Get Areas service endpoint returns an empty array', () => {
    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })

  describe('when Get Areas service endpoint returns an array of multiple areas', () => {
    beforeEach(() => {
      areas.push(
        {
          id: 'test-area-id-1',
          name: 'Test Area Name 1',
        },
        {
          id: 'test-area-id-2',
          name: 'Test Area Name 2',
          description: 'Test Area Description 2',
        }
      )
    })

    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })
})
