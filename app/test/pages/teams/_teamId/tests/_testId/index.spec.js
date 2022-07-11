import { createLocalVue } from '@vue/test-utils'
import VueMeta from 'vue-meta'
import Index from '@/pages/teams/_teamId/tests/_testId/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createTeam,
  createComponent,
  createComponentWithTestResults,
  createTest,
} from '~/test/testDataUtils'

const localVue = createLocalVue()
localVue.use(VueMeta, { keyName: 'head' })

describe('Index', () => {
  const route = {
    params: {
      teamId: 'test-team-id-1',
      testId: 'test-test-id-1',
    },
  }
  let testObject
  let team
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      route,
      serviceRequests: {
        '/v1/tests/test-test-id-1?fields=test(id,description,priority)': {
          responseBody: { test: testObject },
        },
        '/v1/teams/test-team-id-1?fields=team(id,name,components(id,name,type,tags,teams,platformId,testResults))':
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

  describe('when Get Test service endpoint returns a test and the Get Team service endpoint returns an team', () => {
    beforeEach(() => {
      testObject = createTest({ testNumber: 1 })
      team = createTeam({ teamNumber: 1 })
    })

    test('has the right page title', async () => {
      await createWrapper()
      expect(wrapper.vm.$metaInfo.title).toBe(
        'Kronicle - Test Team Name 1 - test-test-id-1 Test'
      )
    })

    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })

    describe('when there are test results for the test', () => {
      beforeEach(() => {
        team.components = [
          createComponentWithTestResults({ componentNumber: 1 }),
          createComponent({ componentNumber: 2 }),
          createComponentWithTestResults({ componentNumber: 3 }),
        ]
      })

      test('renders the test results', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
