import Index from '@/pages/components/_componentId/cross-functional-requirements/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createComponent,
    createComponentAvailableDataRequests,
  createComponentWithCrossFunctionalRequirements,
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
        '/v1/components/test-component-id-1?fields=component(id,name,typeId,tags,teams,platformId,crossFunctionalRequirements)':
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
        'Kronicle - Test Component Name 1 - Cross Functional Requirements'
      )
    })

    describe('when the component has no cross functional requirements', () => {
      test('renders no cross functional requirements', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the component has cross functional requirements', () => {
      beforeEach(() => {
        component = createComponentWithCrossFunctionalRequirements({
          componentNumber: 1,
        })
      })

      test('renders the details of the cross functional requirements', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
