import { createLocalVue } from '@vue/test-utils'
import VueMeta from 'vue-meta'
import Index from '@/pages/teams/_teamId/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import { createTeam } from '~/test/testDataUtils'

const localVue = createLocalVue()
localVue.use(VueMeta, { keyName: 'head' })

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
        '/v1/teams/test-team-id-1?fields=team(id,areaId,name,emailAddress,description,notes,links)':
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
      team = createTeam({ teamNumber: 1, hasMainDetails: true })
    })

    test('has the right page title', async () => {
      await createWrapper()
      expect(wrapper.vm.$metaInfo.title).toBe('Kronicle - Test Team Name 1')
    })

    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })
})
