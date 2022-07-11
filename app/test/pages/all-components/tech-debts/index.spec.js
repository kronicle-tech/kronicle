import Index from '~/pages/all-components/tech-debts/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createComponent,
  createComponentWithTechDebts,
} from '~/test/testDataUtils'

describe('Index', () => {
  let components = []
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      serviceRequests: {
        '/v1/components?fields=components(id,name,type,tags,teams,platformId,techDebts)':
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
    expect(wrapper.vm.$metaInfo.title).toBe('Kronicle - Tech Debts')
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
        createComponentWithTechDebts({ componentNumber: 1 }),
        createComponent({ componentNumber: 2 }),
        createComponentWithTechDebts({ componentNumber: 3 }),
      ]
    })

    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })
})
