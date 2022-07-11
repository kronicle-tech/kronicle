import Index from '@/pages/teams/_teamId/graphql-schemas/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createTeam,
  createComponent,
  createComponentWithGraphQlSchemas,
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
        '/v1/teams/test-team-id-1?stateType=graphql-schemas&fields=team(id,name,components(id,name,type,tags,teams,platformId,states))':
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

  describe('when Get Team service endpoint returns an team', () => {
    beforeEach(() => {
      team = createTeam({ teamNumber: 1 })
    })

    test('has the right page title', async () => {
      await createWrapper()
      expect(wrapper.vm.$metaInfo.title).toBe(
        'Kronicle - Test Team Name 1 - GraphQL Schemas'
      )
    })

    describe('when the team has no components', () => {
      test('renders no GraphQL Schemas', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the team has components but the components have no GraphQL Schemas', () => {
      beforeEach(() => {
        team.components = [
          createComponent({ componentNumber: 1 }),
          createComponent({ componentNumber: 2 }),
          createComponent({ componentNumber: 3 }),
        ]
      })

      test('renders no GraphQL Schemas', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the team has components and the components have GraphQL Schemas', () => {
      beforeEach(() => {
        team.components = [
          createComponentWithGraphQlSchemas({ componentNumber: 1 }),
          createComponent({ componentNumber: 2 }),
          createComponentWithGraphQlSchemas({ componentNumber: 3 }),
        ]
      })

      test('renders the details of the GraphQL Schemas', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
