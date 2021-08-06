import Index from '@/pages/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createArea,
  createComponent,
  createScanner,
  createTeam,
  createTest,
} from '~/test/testDataUtils'
import { expectTextsInItems } from '~/test/pages/wrapperUtils'

describe('Index', () => {
  let areas
  let teams
  let components
  let scanners
  let tests
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      serviceRequests: {
        '/v1/areas?fields=areas(id)': {
          responseBody: { areas },
        },
        '/v1/teams?fields=teams(id)': {
          responseBody: { teams },
        },
        '/v1/components?fields=components(id,typeId,platformId,tags,techDebts(doesNotExist),testResults(doesNotExist))':
          {
            responseBody: { components },
          },
        '/v1/scanners?fields=scanners(id,description)': {
          responseBody: { scanners },
        },
        '/v1/tests?fields=tests(id,description)': {
          responseBody: { tests },
        },
      },
    })
  }

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when the catalog contains no information', () => {
    beforeEach(() => {
      areas = []
      teams = []
      components = []
      scanners = []
      tests = []
    })

    test('renders the page with no information on it', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
      expect(wrapper.get('.area-count').text()).toEqual('0')
      expect(wrapper.get('.team-count').text()).toEqual('0')
      expect(wrapper.get('.component-count').text()).toEqual('0')
      expect(wrapper.get('.tech-debt-count').text()).toEqual('0')
      expect(wrapper.get('.test-result-count').text()).toEqual('0')
    })
  })

  describe('when the catalog contains information', () => {
    beforeEach(() => {
      areas = [
        createArea({ areaNumber: 1 }),
        createArea({ areaNumber: 2 }),
        createArea({ areaNumber: 3 }),
      ]
      teams = [
        createTeam({ teamNumber: 1 }),
        createTeam({ teamNumber: 2 }),
        createTeam({ teamNumber: 3 }),
      ]
      components = [
        createComponent({
          componentNumber: 1,
          platformNumber: 1,
          hasTechDebts: true,
          hasTestResults: true,
        }),
        createComponent({
          componentNumber: 2,
          hasTechDebts: true,
          hasTestResults: true,
        }),
        createComponent({
          componentNumber: 3,
          platformNumber: 3,
          hasTechDebts: true,
          hasTestResults: true,
        }),
      ]
      scanners = [
        createScanner({ scannerNumber: 1 }),
        createScanner({ scannerNumber: 2 }),
        createScanner({ scannerNumber: 3 }),
      ]
      tests = [
        createTest({ testNumber: 1 }),
        createTest({ testNumber: 2 }),
        createTest({ testNumber: 3 }),
      ]
    })

    test('has the right page title', async () => {
      await createWrapper()
      expect(wrapper.vm.$metaInfo.title).toBe('Component Catalog')
    })

    test('renders the page showing the information', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
      expect(wrapper.get('.area-count').text()).toEqual('3')
      expect(wrapper.get('.team-count').text()).toEqual('3')
      expect(wrapper.get('.component-count').text()).toEqual('3')
      expect(wrapper.get('.tech-debt-count').text()).toEqual('6')
      expect(wrapper.get('.test-result-count').text()).toEqual('6')
      expectTextsInItems(wrapper, '.scanner-id', [
        'test-scanner-id-1',
        'test-scanner-id-2',
        'test-scanner-id-3',
      ])
      expectTextsInItems(wrapper, '.test-id', [
        'test-test-id-1',
        'test-test-id-2',
        'test-test-id-3',
      ])
      expectTextsInItems(wrapper, '.platform', [
        'missing',
        'test-platform-id-1',
        'test-platform-id-3',
      ])
      expectTextsInItems(wrapper, '.tag', [
        'test-tag-1-1',
        'test-tag-1-2',
        'test-tag-2-1',
        'test-tag-2-2',
        'test-tag-3-1',
        'test-tag-3-2',
      ])
      expectTextsInItems(wrapper, '.component-type', [
        'test-component-type-id-1',
        'test-component-type-id-2',
        'test-component-type-id-3',
      ])
    })
  })
})
