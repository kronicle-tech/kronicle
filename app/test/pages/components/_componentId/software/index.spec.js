import Index from '@/pages/components/_componentId/software/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createComponent,
  createComponentAvailableDataRequests,
  createComponentWithSoftwareItems,
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
        '/v1/components/test-component-id-1?stateType=softwares&fields=component(id,name,teams,states)':
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
        'Kronicle - Test Component Name 1 - Software'
      )
    })

    describe('when the component has no software', () => {
      test('renders no software', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the component has software', () => {
      beforeEach(() => {
        component = createComponentWithSoftwareItems({ componentNumber: 1 })
      })

      test('renders the details of the software', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the component has software that are not sorted', () => {
      beforeEach(() => {
        component = createComponentWithSoftwareItems({ componentNumber: 1 })
        component.states[0].softwares.reverse()
      })

      test('sorts the software before rending the software', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
