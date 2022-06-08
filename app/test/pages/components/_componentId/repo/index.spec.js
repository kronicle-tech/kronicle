import MockDate from 'mockdate'
import Index from '@/pages/components/_componentId/repo/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createComponent,
  createComponentAvailableDataRequests,
  createComponentWithGitRepo,
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
        '/v1/components/test-component-id-1?stateType=git-repo&fields=component(id,name,teams,states)':
          {
            responseBody: { component },
          },
      },
    })
  }

  beforeEach(() => {
    MockDate.set(Date.parse('2020-01-01T00:00:00.000Z'))
  })

  afterEach(() => {
    MockDate.reset()
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
        'Kronicle - Test Component Name 1 - Repo'
      )
    })

    describe('when the component has no git repo', () => {
      test('renders a message saying there is no repo', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
        expect(wrapper.text()).toEqual(
          expect.stringContaining(
            'Not git-based information is available for this component'
          )
        )
      })
    })

    describe('when the component has a git repo', () => {
      beforeEach(() => {
        component = createComponentWithGitRepo({ componentNumber: 1 })
      })

      test('renders information about the repo', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
        expect(wrapper.text()).toEqual(
          expect.stringContaining('Test Identity Name 1 1 1')
        )
      })
    })
  })
})
