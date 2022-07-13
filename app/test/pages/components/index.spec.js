import Index from '~/pages/components'
import { createPageWrapper } from '~/test/pages/pageUtils'
import { createComponent } from '~/test/testDataUtils'

describe('Index', () => {
  let components
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      serviceRequests: {
        '/v1/components?fields=components(id,name,discovered,type,tags,description,notes,responsibilities,teams,platformId)':
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
    expect(wrapper.vm.$metaInfo.title).toBe('Kronicle - All Components')
  })

  describe('when Get Components service endpoint returns an empty array', () => {
    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })

  describe('when Get Components service endpoint returns an array of multiple components', () => {
    beforeEach(() => {
      components.push(
        createComponent({ componentNumber: 1, hasMainDetails: true }),
        createComponent({ componentNumber: 2 }),
        createComponent({ componentNumber: 3, hasMainDetails: true })
      )
    })

    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })
})
