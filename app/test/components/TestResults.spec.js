import { mount } from '@vue/test-utils'
import TestResults from '@/components/TestResults.vue'
import {
  expectClassesInTableRows,
  expectSubTextsInTableRows,
} from '~/test/components/tableUtils'

describe('TestResults', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(TestResults, { propsData })
  }

  function expectTestOutcomeClasses(testOutcomeClasses) {
    expectClassesInTableRows(wrapper, 'td.test-outcome', testOutcomeClasses)
  }

  function expectTestIds(testIds) {
    expectSubTextsInTableRows(wrapper, 'td.test-id a', testIds)
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when testResults prop is not set', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when testResults prop is set to an empty array', () => {
    beforeEach(() => {
      propsData.testResults = []
    })

    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when testResults prop is set to one test result', () => {
    beforeEach(() => {
      propsData.testResults = [
        {
          testId: 'test-test-id',
          priority: 'low',
          outcome: 'pass',
          message: 'Test Message 1',
        },
      ]
    })

    test('renders a table showing the test result', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when a test result has a `pass` outcome', () => {
    beforeEach(() => {
      propsData.testResults = [
        {
          testId: 'test-test-id',
          priority: 'low',
          outcome: 'pass',
          message: 'Test Message 1',
        },
      ]
    })

    test('renders the outcome table cell with a `primary` background colour', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTestOutcomeClasses(['test-outcome table-primary'])
    })
  })

  describe('when a test result has a `fail` outcome', () => {
    beforeEach(() => {
      propsData.testResults = [
        {
          testId: 'test-test-id',
          priority: 'low',
          outcome: 'fail',
          message: 'Test Message 1',
        },
      ]
    })

    test('renders the outcome table cell with a `danger` background colour', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTestOutcomeClasses(['test-outcome table-danger'])
    })
  })

  describe('when a test result has a `not-applicable` outcome', () => {
    beforeEach(() => {
      propsData.testResults = [
        {
          testId: 'test-test-id',
          priority: 'low',
          outcome: 'not-applicable',
          message: 'Test Message 1',
        },
      ]
    })

    test('renders the outcome table cell with a `light` background colour', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTestOutcomeClasses(['test-outcome table-light'])
    })
  })

  describe('when a test result has a `message` containing Markdown', () => {
    beforeEach(() => {
      propsData.testResults = [
        {
          testId: 'test-test-id',
          priority: 'low',
          outcome: 'pass',
          message: 'Text with **bold** formatting',
        },
      ]
    })

    test('renders the test result with the Markdown converted to HTML', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expect(wrapper.find('td.test-message div').html()).toEqual(
        expect.stringContaining('<strong>bold</strong>')
      )
    })
  })

  describe('when testResults prop is set to multiple test results', () => {
    beforeEach(() => {
      propsData.testResults = [
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
        {
          testId: 'test-test-id-3',
          priority: 'high',
          outcome: 'not-applicable',
          message: 'Test Message 3',
        },
      ]
    })

    test('renders a table showing the test results', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTestIds(['test-test-id-2', 'test-test-id-1', 'test-test-id-3'])
    })
  })

  describe('when there are test results that have different outcomes', () => {
    beforeEach(() => {
      propsData.testResults = [
        {
          testId: 'test-test-id-1',
          priority: 'low',
          outcome: 'not-applicable',
          message: 'Test Message 1',
        },
        {
          testId: 'test-test-id-2',
          priority: 'medium',
          outcome: 'pass',
          message: 'Test Message 2',
        },
        {
          testId: 'test-test-id-3',
          priority: 'medium',
          outcome: 'fail',
          message: 'Test Message 3',
        },
      ]
    })

    test('renders the test results sorted by outcome, with `fail` first, then `pass`, then `not-applicable`', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTestIds(['test-test-id-3', 'test-test-id-2', 'test-test-id-1'])
    })
  })

  describe('when there are two test results with the same outcome', () => {
    beforeEach(() => {
      propsData.testResults = [
        {
          testId: 'test-test-id-2',
          priority: 'low',
          outcome: 'pass',
          message: 'Test Message',
        },
        {
          testId: 'test-test-id-1',
          priority: 'low',
          outcome: 'pass',
          message: 'Test Message',
        },
      ]
    })

    test('renders the test results sorted by test-id', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTestIds(['test-test-id-1', 'test-test-id-2'])
    })
  })
})
