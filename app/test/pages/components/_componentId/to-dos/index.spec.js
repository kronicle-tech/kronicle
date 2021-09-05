import Index from '@/pages/components/_componentId/to-dos/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import { createComponent, createComponentWithToDos } from '~/test/testDataUtils'

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
        '/v1/components/test-component-id-1?fields=component(id,name,toDos)': {
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
        'Kronicle - Test Component Name 1 - To Dos'
      )
    })

    describe('when the component has no to dos', () => {
      test('renders no to dos', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the component has to dos', () => {
      beforeEach(() => {
        component = createComponentWithToDos({ componentNumber: 1 })
      })

      test('renders the details of the to dos', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
