import { mount } from '@vue/test-utils'
import TestResultTable from '@/components/TestResultTable.vue'
import {
  expectHrefsInTableRows,
  expectSubTextsInTableRows,
} from '~/test/components/tableUtils'

describe('TestResultTable', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(TestResultTable, { propsData })
  }

  function createComponentsWithTestResults() {
    return [
      {
        id: 'test-component-id-1',
        name: 'Test Component Name 1',
        teams: [
          {
            teamId: 'test-team-id-1-a',
          },
          {
            teamId: 'test-team-id-1-b',
          },
        ],
        testResults: [
          {
            testId: 'test-test-id-1',
            priority: 'low',
            outcome: 'pass',
            message: 'Test Message 1',
          },
          {
            testId: 'test-test-id-2',
            priority: 'medium',
            outcome: 'fail',
            message: 'Test Message 2',
          },
        ],
      },
      {
        id: 'test-component-id-3',
        name: 'Test Component Name 3',
        teams: [
          {
            teamId: 'test-team-id-3-a',
          },
          {
            teamId: 'test-team-id-3-b',
          },
        ],
        testResults: [
          {
            testId: 'test-test-id-3',
            priority: 'low',
            outcome: 'pass',
            message: 'Test Message 3',
          },
          {
            testId: 'test-test-id-4',
            priority: 'medium',
            outcome: 'fail',
            message: 'Test Message 4',
          },
        ],
      },
    ]
  }

  function expectTestIds(testIds) {
    expectSubTextsInTableRows(wrapper, 'td.test-id a', testIds)
  }

  function expectTestUrls(testUrls) {
    expectHrefsInTableRows(wrapper, 'td.test-id a', testUrls)
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when testResults prop is set to an empty array', () => {
    beforeEach(() => {
      propsData.components = []
    })

    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when components prop is set to multiple components with multiple test results', () => {
    beforeEach(() => {
      propsData.components = createComponentsWithTestResults()
    })

    test('renders a table showing the test results and the test results are sorted by outcome, then testId', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTestIds([
        'test-test-id-2',
        'test-test-id-4',
        'test-test-id-1',
        'test-test-id-3',
      ])
    })
  })

  describe('when testId prop is set', () => {
    beforeEach(() => {
      propsData.testId = 'test-test-id-1'
      propsData.components = createComponentsWithTestResults()
      propsData.components[0].testResults[0].testId = 'test-test-id-1'
      propsData.components[0].testResults[1].testId = 'test-test-id-2'
      propsData.components[1].testResults[0].testId = 'test-test-id-1'
      propsData.components[1].testResults[1].testId = 'test-test-id-2'
    })

    test('renders only test results with matching testId', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTestIds(['test-test-id-1', 'test-test-id-1'])
    })
  })

  describe('when teamId prop is set', () => {
    beforeEach(() => {
      propsData.teamId = 'test-team-id-0'
      propsData.components = createComponentsWithTestResults()
    })

    test('renders links to tests that include teamId prop value in the URL', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTestUrls([
        '/teams/test-team-id-0/tests/test-test-id-2',
        '/teams/test-team-id-0/tests/test-test-id-4',
        '/teams/test-team-id-0/tests/test-test-id-1',
        '/teams/test-team-id-0/tests/test-test-id-3',
      ])
    })
  })

  describe('when areaId prop is set', () => {
    beforeEach(() => {
      propsData.areaId = 'test-area-id-0'
      propsData.components = createComponentsWithTestResults()
    })

    test('renders links to tests that include areaId prop value in the URL', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTestUrls([
        '/areas/test-area-id-0/tests/test-test-id-2',
        '/areas/test-area-id-0/tests/test-test-id-4',
        '/areas/test-area-id-0/tests/test-test-id-1',
        '/areas/test-area-id-0/tests/test-test-id-3',
      ])
    })
  })
})
