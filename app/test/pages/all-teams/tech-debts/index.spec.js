import Index from '~/pages/all-teams/tech-debts/index.vue'
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
        '/v1/components?fields=components(id,name,teams,techDebts)': {
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
    expect(wrapper.vm.$metaInfo.title).toBe(
      'Component Catalog - All Teams - Tech Debts'
    )
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
          id: 'test-team-1',
        },
        {
          id: 'test-team-2',
        },
      ]
      components = [
        {
          id: 'test-id-1',
          name: 'Test Name 1',
          teams: [
            {
              teamId: 'test-team-1',
            },
          ],
          techDebts: [
            {
              description: 'Test Description 1a',
            },
            {
              description: 'Test Description 1b',
            },
          ],
        },
        {
          id: 'test-id-2',
          name: 'Test Name 2',
          teams: [
            {
              teamId: 'test-team-1',
            },
          ],
          techDebts: [
            {
              description: 'Test Description 2a',
            },
            {
              description: 'Test Description 2b',
            },
          ],
        },
        {
          id: 'test-id-3',
          name: 'Test Name 3',
          teams: [
            {
              teamId: 'test-team-2',
            },
          ],
          techDebts: [
            {
              description: 'Test Description 3a',
            },
            {
              description: 'Test Description 3b',
            },
          ],
        },
        {
          id: 'test-id-4',
          name: 'Test Name 4',
          teams: [
            {
              teamId: 'test-team-2',
            },
          ],
          techDebts: [
            {
              description: 'Test Description 4a',
            },
            {
              description: 'Test Description 4b',
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

  describe('when Get Teams service endpoint returns a component with no tech debt', () => {
    beforeEach(() => {
      teams = [
        {
          id: 'test-team-1',
        },
      ]
      components = [
        {
          id: 'test-id-1',
          name: 'Test Name 1',
          teams: [
            {
              teamId: 'test-team-1',
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
          id: 'test-team-1',
        },
        {
          id: 'test-team-2',
        },
        {
          id: 'test-team-3',
        },
        {
          id: 'test-team-4',
        },
      ]
      components = [
        {
          id: 'test-id-1',
          name: 'Test Name 1',
          teams: [
            {
              teamId: 'test-team-1',
            },
          ],
        },
        {
          id: 'test-id-2',
          name: 'Test Name 2',
          teams: [
            {
              teamId: 'test-team-2',
            },
          ],
        },
        {
          id: 'test-id-3',
          name: 'Test Name 3',
          teams: [
            {
              teamId: 'test-team-3',
            },
          ],
        },
        {
          id: 'test-id-4',
          name: 'Test Name 4',
          teams: [
            {
              teamId: 'test-team-4',
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

  describe('when Get Teams service endpoint returns tech debts for all priorities, including undefined', () => {
    beforeEach(() => {
      teams = [
        {
          id: 'test-team-1',
        },
      ]
      components = [
        {
          id: 'test-id-1',
          name: 'Test Name 1',
          teams: [
            {
              teamId: 'test-team-1',
            },
          ],
          techDebts: [
            {
              description: 'Test Description 1a',
              priority: 'very-high',
            },
            {
              description: 'Test Description 1b',
              priority: 'high',
            },
            {
              description: 'Test Description 1c',
              priority: 'medium',
            },
            {
              description: 'Test Description 1d',
              priority: 'low',
            },
            {
              description: 'Test Description 1e',
            },
          ],
        },
      ]
    })

    test('renders all priorities, including undefined', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })
})
