import Index from '~/pages/openapi-specs'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createComponent,
  createComponentWithOpenApiSpecs,
} from '~/test/testDataUtils'

describe('Index', () => {
  let components = []
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      serviceRequests: {
        '/v1/components?stateType=openapi-specs&fields=components(id,name,type,tags,teams,platformId,states)':
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
    expect(wrapper.vm.$metaInfo.title).toBe('Kronicle - OpenAPI Specs')
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
        createComponentWithOpenApiSpecs({ componentNumber: 1 }),
        createComponent({ componentNumber: 2 }),
        createComponentWithOpenApiSpecs({ componentNumber: 3 }),
      ]
    })

    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })
})
