import Index from '@/pages/teams/_teamId/tests/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createTeam,
  createComponent,
  createComponentWithTestResults,
} from '~/test/testDataUtils'

describe('Index', () => {
  const route = {
    params: {
      teamId: 'test-team-id-1',
    },
  }
  let team
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      route,
      serviceRequests: {
        '/v1/teams/test-team-id-1?testOutcome=fail&fields=team(id,name,components(id,name,type,tags,teams,platformId,testResults))':
          {
            responseBody: { team },
          },
      },
    })
  }

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when Get Team service endpoint returns a team', () => {
    beforeEach(() => {
      team = createTeam({ teamNumber: 1 })
    })

    test('has the right page title', async () => {
      await createWrapper()
      expect(wrapper.vm.$metaInfo.title).toBe(
        'Kronicle - Test Team Name 1 - Tests'
      )
    })

    describe('when the team has no components', () => {
      test('renders no test results', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the team has components but the components have no test results', () => {
      beforeEach(() => {
        team.components = [
          createComponent({ componentNumber: 1 }),
          createComponent({ componentNumber: 2 }),
          createComponent({ componentNumber: 3 }),
        ]
      })

      test('renders no test results', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the team has components and the components have test results', () => {
      beforeEach(() => {
        team.components = [
          createComponentWithTestResults({
            componentNumber: 1,
          }),
          createComponent({ componentNumber: 2 }),
          createComponentWithTestResults({
            componentNumber: 3,
          }),
        ]
      })

      test('renders the details of the test results', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
