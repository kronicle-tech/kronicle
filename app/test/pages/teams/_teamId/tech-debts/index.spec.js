import Index from '@/pages/teams/_teamId/tech-debts/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createTeam,
  createComponent,
  createComponentWithTechDebts,
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
        '/v1/teams/test-team-id-1?fields=team(id,name,components(id,name,type,tags,teams,platformId,techDebts))':
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
        'Kronicle - Test Team Name 1 - Tech Debts'
      )
    })

    describe('when the team has no components', () => {
      test('renders no tech debts', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the team has components but the components have no tech debts', () => {
      beforeEach(() => {
        team.components = [
          createComponent({ componentNumber: 1 }),
          createComponent({ componentNumber: 2 }),
          createComponent({ componentNumber: 3 }),
        ]
      })

      test('renders no tech debts', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the team has components and the components have tech debts', () => {
      beforeEach(() => {
        team.components = [
          createComponentWithTechDebts({
            componentNumber: 1,
          }),
          createComponent({ componentNumber: 2 }),
          createComponentWithTechDebts({
            componentNumber: 3,
          }),
        ]
      })

      test('renders the details of the tech debts', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
