import Index from '@/pages/components/_componentId/readme/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createComponent,
  createComponentAvailableDataRequests,
  createComponentWithReadme,
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
        '/v1/components/test-component-id-1?stateType=readme&fields=component(id,name,teams,states)':
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
        'Kronicle - Test Component Name 1 - README'
      )
    })

    describe('when the component has no readme', () => {
      test('renders a message saying there is no readme', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
        expect(wrapper.text()).toEqual(
          expect.stringContaining("This component's repo has no README")
        )
      })
    })

    describe('when the component has a readme', () => {
      beforeEach(() => {
        component = createComponentWithReadme({ componentNumber: 1 })
      })

      test('renders the content of the readme', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
        expect(wrapper.text()).toEqual(
          expect.stringContaining('Test Readme Content 1')
        )
      })
    })
  })
})
