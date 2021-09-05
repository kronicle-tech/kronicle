import Index from '@/pages/components/_componentId/openapi-specs/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createComponent,
  createComponentWithOpenApiSpecs,
} from '~/test/testDataUtils'

describe('Index', () => {
  const route = {
    params: {
      componentId: 'test-component-id-1',
    },
  }
  let component
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      route,
      serviceRequests: {
        '/v1/components/test-component-id-1?fields=component(id,name,teams,openApiSpecs)':
          {
            responseBody: { component },
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
      component = createComponent({ componentNumber: 1 })
    })

    test('has the right page title', async () => {
      await createWrapper()
      expect(wrapper.vm.$metaInfo.title).toBe(
        'Kronicle - Test Component Name 1 - OpenAPI Specs'
      )
    })

    describe('when the component has no OpenAPI specs', () => {
      test('renders no OpenAPI specs', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the component has OpenAPI specs', () => {
      beforeEach(() => {
        component = createComponentWithOpenApiSpecs({ componentNumber: 1 })
      })

      test('renders the details of the OpenAPI specs', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
