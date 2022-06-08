import Index from '~/pages/all-teams/tests/index.vue'
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
        '/v1/components?fields=components(id,name,teams,testResults)': {
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
    expect(wrapper.vm.$metaInfo.title).toBe('Kronicle - All Teams - Tests')
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
          testResults: [
            {
              testId: 'test-test-id-1a',
              priority: 'low',
              outcome: 'fail',
              message: 'Test Message 1a',
            },
            {
              testId: 'test-test-id-1b',
              priority: 'low',
              outcome: 'fail',
              message: 'Test Message 1b',
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
          testResults: [
            {
              testId: 'test-test-id-2a',
              priority: 'low',
              outcome: 'fail',
              message: 'Test Message 2a',
            },
            {
              testId: 'test-test-id-2b',
              priority: 'low',
              outcome: 'fail',
              message: 'Test Message 2b',
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
          testResults: [
            {
              testId: 'test-test-id-3a',
              priority: 'low',
              outcome: 'fail',
              message: 'Test Message 3a',
            },
            {
              testId: 'test-test-id-3b',
              priority: 'low',
              outcome: 'fail',
              message: 'Test Message 3b',
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
          testResults: [
            {
              testId: 'test-test-id-4a',
              priority: 'low',
              outcome: 'fail',
              message: 'Test Message 4a',
            },
            {
              testId: 'test-test-id-4b',
              priority: 'low',
              outcome: 'fail',
              message: 'Test Message 4b',
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

  describe('when there are test results for each outcome', () => {
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
          testResults: [
            {
              testId: 'test-test-id-1a',
              priority: 'low',
              outcome: 'pass',
              message: 'Test Message 1a',
            },
            {
              testId: 'test-test-id-1b',
              priority: 'low',
              outcome: 'fail',
              message: 'Test Message 1b',
            },
            {
              testId: 'test-test-id-1c',
              priority: 'low',
              outcome: 'not-applicable',
              message: 'Test Message 1c',
            },
          ],
        },
      ]
    })

    test('renders only failed test results', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })

  describe('when Get Teams service endpoint returns a component with no test results', () => {
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

  describe('when Get Teams service endpoint returns failed test results for all priorities', () => {
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
          testResults: [
            {
              testId: 'test-test-id-1a',
              priority: 'very-high',
              outcome: 'fail',
              message: 'Test Message 1a',
            },
            {
              testId: 'test-test-id-1b',
              priority: 'high',
              outcome: 'fail',
              message: 'Test Message 1b',
            },
            {
              testId: 'test-test-id-1c',
              priority: 'medium',
              outcome: 'fail',
              message: 'Test Message 1c',
            },
            {
              testId: 'test-test-id-1d',
              priority: 'low',
              outcome: 'fail',
              message: 'Test Message 1d',
            },
          ],
        },
      ]
    })

    test('renders all priorities', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })
})
