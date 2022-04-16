import Index from '~/pages/teams/_teamId/dependencies/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createTeam,
  createComponent,
  createSummaryWithEmptyComponentAndSubComponentDependencies,
  createComponentDependencies,
  createSubComponentDependencies,
} from '~/test/testDataUtils'

describe('Index', () => {
  const route = {
    params: {
      teamId: 'test-team-id-1',
    },
  }
  let team
  let allComponents
  let summary
  let wrapperActions
  let wrapper

  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      route,
      serviceRequests: {
        '/v1/teams/test-team-id-1?fields=team(id,name,components(id,name,typeId,tags,description,notes,responsibilities,teams,platformId,state(environments(id))))':
          {
            responseBody: { team },
          },
        '/v1/components?fields=components(id,name,typeId,tags,description,notes,responsibilities,teams,platformId)':
          {
            responseBody: { components: allComponents },
          },
        '/v1/summary?fields=summary(componentDependencies,subComponentDependencies)':
          {
            responseBody: { summary },
          },
      },
    })
    for (const wrapperAction of wrapperActions) {
      await wrapperAction(wrapper)
    }
  }

  beforeEach(() => {
    team = createTeam({ teamNumber: 1 })
    team.components = [
      createComponent({ componentNumber: 1 }),
      createComponent({ componentNumber: 2 }),
      createComponent({ componentNumber: 3 }),
    ]
    allComponents = [].concat(team.components, [
      createComponent({ componentNumber: 4 }),
      createComponent({ componentNumber: 5 }),
      createComponent({ componentNumber: 6 }),
    ])
    summary = createSummaryWithEmptyComponentAndSubComponentDependencies()
    wrapperActions = []
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  test('has the right page title', async () => {
    await createWrapper()
    expect(wrapper.vm.$metaInfo.title).toBe(
      'Kronicle - Test Team Name 1 Team - Dependencies'
    )
  })

  describe('when Get Summary service endpoint returns no dependencies', () => {
    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })

  describe('when Get Summary service endpoint returns an array of multiple component dependencies', () => {
    beforeEach(() => {
      summary.componentDependencies = createComponentDependencies()
      summary.subComponentDependencies = createSubComponentDependencies()
    })

    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })

    describe('when the detailed checkbox is checked', () => {
      beforeEach(() => {
        wrapperActions.push(async (wrapper) => {
          await wrapper.get('#detailed-dependencies').trigger('click')
        })
      })

      test('shows detailed dependencies in graph', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
