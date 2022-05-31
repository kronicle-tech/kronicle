import Index from '@/pages/components/_componentId/lines-of-code/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createComponent,
  createComponentWithLinesOfCode,
  createComponentResponseWithStateTypes,
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
        '/v1/components/test-component-id-1?fields=component(id,name,states(type))': createComponentResponseWithStateTypes(1),
        '/v1/components/test-component-id-1?stateType=lines-of-code&fields=component(id,name,states)':
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
        'Kronicle - Test Component Name 1 - Lines of Code'
      )
    })

    describe('when the component has no lines of codes', () => {
      test('renders no lines of codes', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the component has lines of codes', () => {
      beforeEach(() => {
        component = createComponentWithLinesOfCode({ componentNumber: 1 })
      })

      test('renders the details of the lines of codes', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
