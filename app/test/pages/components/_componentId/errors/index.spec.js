import Index from '@/pages/components/_componentId/errors/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createComponent,
  createComponentAvailableDataRequests,
  createComponentWithScannerErrors,
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
        ...createComponentAvailableDataRequests(),
        '/v1/components/test-component-id-1?fields=component(id,name,typeId,tags,teams,platformId,scannerErrors)':
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
        'Kronicle - Test Component Name 1 - Errors'
      )
    })

    describe('when the component has no Scanner errors', () => {
      test('renders no Scanner errors', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the component has scanner errors', () => {
      beforeEach(() => {
        component = createComponentWithScannerErrors({ componentNumber: 1 })
      })

      test('renders the details of the scanner errors', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
