import Index from '@/pages/teams/_teamId/cross-functional-requirements/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createTeam,
  createComponent,
  createComponentWithCrossFunctionalRequirements,
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
        '/v1/teams/test-team-id-1?fields=team(id,name,components(id,name,typeId,tags,teams,platformId,crossFunctionalRequirements))':
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
        'Component Catalog - Test Team Name 1 Team - Cross Functional Requirements'
      )
    })

    describe('when the team has no components', () => {
      test('renders no cross functional requirements', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the team has components but the components have no cross functional requirements', () => {
      beforeEach(() => {
        team.components = [
          createComponent({ componentNumber: 1 }),
          createComponent({ componentNumber: 2 }),
          createComponent({ componentNumber: 3 }),
        ]
      })

      test('renders no cross functional requirements', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the team has components and the components have cross functional requirements', () => {
      beforeEach(() => {
        team.components = [
          createComponentWithCrossFunctionalRequirements({
            componentNumber: 1,
          }),
          createComponent({ componentNumber: 2 }),
          createComponentWithCrossFunctionalRequirements({
            componentNumber: 3,
          }),
        ]
      })

      test('renders the details of the cross functional requirements', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
