import Index from '~/pages/all-teams/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'

describe('Index', () => {
  let teams
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      serviceRequests: {
        '/v1/teams?fields=teams(id,name,emailAddress,description)': {
          responseBody: { teams },
        },
      },
    })
  }

  beforeEach(() => {
    teams = []
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  test('has the right page title', async () => {
    await createWrapper()
    expect(wrapper.vm.$metaInfo.title).toBe('Component Catalog - All Teams')
  })

  describe('when Get Teams service endpoint returns an empty array', () => {
    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })

  describe('when Get Teams service endpoint returns an array of multiple teams', () => {
    beforeEach(() => {
      teams.push(
        {
          id: 'test-team-id-1',
          name: 'Test Team Name 1',
        },
        {
          id: 'test-team-id-2',
          name: 'Test Team Name 2',
          emailAddress: 'example@example.com',
          description: 'Test Team Description 2',
        }
      )
    })

    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })
})
