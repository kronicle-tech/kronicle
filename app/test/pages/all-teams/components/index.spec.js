import Index from '~/pages/all-teams/components/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'

describe('Index', () => {
  let teams
  let components
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      serviceRequests: {
        '/v1/teams?fields=teams(id,name)': {
          responseBody: { teams },
        },
        '/v1/components?fields=components(id,name,teams,tags,description,notes,responsibilities,techDebts,openApiSpecs,links)':
          {
            responseBody: { components },
          },
      },
    })
  }

  beforeEach(() => {
    teams = []
    components = []
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  test('has the right page title', async () => {
    await createWrapper()
    expect(wrapper.vm.$metaInfo.title).toBe('Kronicle - All Teams')
  })

  describe('when Get Teams service endpoint returns an empty array', () => {
    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })

  describe('when Get Teams service endpoint returns an array of multiple teams', () => {
    beforeEach(() => {
      teams = [
        {
          id: 'test-team-id-1',
        },
        {
          id: 'test-team-id-2',
        },
      ]
      components = [
        {
          id: 'test-id-1',
          name: 'Test Name 1',
          teams: [
            {
              teamId: 'test-team-id-1',
            },
          ],
        },
        {
          id: 'test-id-2',
          name: 'Test Name 2',
          teams: [
            {
              teamId: 'test-team-id-1',
            },
          ],
        },
        {
          id: 'test-id-3',
          name: 'Test Name 3',
          teams: [
            {
              teamId: 'test-team-id-2',
            },
          ],
        },
        {
          id: 'test-id-4',
          name: 'Test Name 4',
          teams: [
            {
              teamId: 'test-team-id-2',
            },
          ],
        },
      ]
    })

    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })

  describe('when Get Teams service endpoint returns more than 3 teams', () => {
    beforeEach(() => {
      teams = [
        {
          id: 'test-team-id-1',
        },
        {
          id: 'test-team-id-2',
        },
        {
          id: 'test-team-id-3',
        },
        {
          id: 'test-team-id-4',
        },
      ]
      components = [
        {
          id: 'test-id-1',
          name: 'Test Name 1',
          teams: [
            {
              teamId: 'test-team-id-1',
            },
          ],
        },
        {
          id: 'test-id-2',
          name: 'Test Name 2',
          teams: [
            {
              teamId: 'test-team-id-2',
            },
          ],
        },
        {
          id: 'test-id-3',
          name: 'Test Name 3',
          teams: [
            {
              teamId: 'test-team-id-3',
            },
          ],
        },
        {
          id: 'test-id-4',
          name: 'Test Name 4',
          teams: [
            {
              teamId: 'test-team-id-4',
            },
          ],
        },
      ]
    })

    test('renders 3 teams per row', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })
})
