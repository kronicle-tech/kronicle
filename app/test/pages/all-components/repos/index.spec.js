import MockDate from 'mockdate'
import Index from '~/pages/all-components/repos/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createComponent,
  createComponentWithGitRepo,
} from '~/test/testDataUtils'

describe('Index', () => {
  let components = []
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      serviceRequests: {
        '/v1/components?stateType=git-repo&fields=components(id,name,type,tags,teams,platformId,states)':
          {
            responseBody: { components },
          },
      },
    })
  }

  beforeEach(() => {
    MockDate.set(Date.parse('1970-01-29T00:00:00.000Z'))
    components = []
  })

  afterEach(() => {
    MockDate.reset()
    wrapper.destroy()
    wrapper = null
  })

  test('has the right page title', async () => {
    await createWrapper()
    expect(wrapper.vm.$metaInfo.title).toBe('Kronicle - Repos')
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
        createComponentWithGitRepo({ componentNumber: 1 }),
        createComponent({ componentNumber: 2 }),
        createComponentWithGitRepo({ componentNumber: 3 }),
      ]
    })

    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })
})
