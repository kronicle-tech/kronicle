import TestResultsView from '@/components/TestResultsView.vue'
import {
  createViewComponentWrapper,
  expectViewCount,
} from '~/test/components/viewUtils'
import {
  expectHrefsInTableRows,
  expectTextsInTableRows,
} from '~/test/components/tableUtils'
import {
  createComponent,
  createComponentWithTestResults,
} from '~/test/testDataUtils'

describe('TestResultsView', () => {
  let propsData
  let wrapper
  async function createWrapper() {
    wrapper = await createViewComponentWrapper(TestResultsView, {
      propsData,
    })
  }

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when components prop is set to an empty array', () => {
    beforeEach(() => {
      propsData = {
        components: [],
      }
    })

    test('renders a large count of zero with success styling', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectViewCount(
        wrapper,
        '.zero-count',
        'success',
        'display-1',
        /0\s+tests/
      )
    })
  })

  describe('when components prop is set components with no test result', () => {
    beforeEach(() => {
      propsData = {
        components: [
          {
            id: 'test-component-id-1',
            name: 'Test Component Name 1',
          },
          {
            id: 'test-component-id-2',
            name: 'Test Component Name 2',
          },
        ],
      }
    })

    test('renders a large count of zero with success styling', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectViewCount(
        wrapper,
        '.zero-count',
        'success',
        'display-1',
        /0\s+tests/
      )
    })
  })

  describe('when components prop is set a component with 1 fail test result', () => {
    beforeEach(() => {
      propsData = {
        components: [
          {
            id: 'test-component-id-1',
            name: 'Test Component Name 1',
            testResults: [
              {
                testId: 'test-test-id-1',
                priority: 'very-high',
                outcome: 'fail',
                message: 'Test Message 1a',
              },
            ],
          },
        ],
      }
    })

    test('renders a large fail count of 1 with danger styling', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectViewCount(
        wrapper,
        '.fail-count',
        'danger',
        'display-1',
        /1\s+failing test/
      )
    })
  })

  describe('when components prop is set a component with 1 pass test result', () => {
    beforeEach(() => {
      propsData = {
        components: [
          {
            id: 'test-component-id-1',
            name: 'Test Component Name 1',
            testResults: [
              {
                testId: 'test-test-id-1',
                priority: 'very-high',
                outcome: 'pass',
                message: 'Test Message 1a',
              },
            ],
          },
        ],
      }
    })

    test('renders a smaller count of 1 with success styling', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectViewCount(
        wrapper,
        '.pass-count',
        'success',
        'display-4',
        /1\s+passing test/
      )
    })
  })

  describe('when components prop is set a component with 1 not-applicable test result', () => {
    beforeEach(() => {
      propsData = {
        components: [
          {
            id: 'test-component-id-1',
            name: 'Test Component Name 1',
            testResults: [
              {
                testId: 'test-test-id-1',
                priority: 'very-high',
                outcome: 'not-applicable',
                message: 'Test Message 1a',
              },
            ],
          },
        ],
      }
    })

    test('renders a smaller count of 1 with light styling', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectViewCount(
        wrapper,
        '.not-applicable-count',
        'light',
        'display-4',
        /1\s+not applicable test/
      )
    })
  })

  describe('when components is set to multiple components with multiple test results', () => {
    beforeEach(() => {
      propsData = {
        components: [
          createComponentWithTestResults({ componentNumber: 1 }),
          createComponentWithTestResults({ componentNumber: 2 }),
          createComponentWithTestResults({ componentNumber: 3 }),
        ],
      }
    })

    test('renders the test results', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.test-id a', [
        'test-test-id-1-1',
        'test-test-id-2-1',
        'test-test-id-3-1',
        'test-test-id-1-2',
        'test-test-id-2-2',
        'test-test-id-3-2',
      ])
      expectHrefsInTableRows(wrapper, 'td.test-id a', [
        '/tests/test-test-id-1-1',
        '/tests/test-test-id-2-1',
        '/tests/test-test-id-3-1',
        '/tests/test-test-id-1-2',
        '/tests/test-test-id-2-2',
        '/tests/test-test-id-3-2',
      ])
    })

    test('when selecting a filter, the filter is applied to the test results in the table', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.test-id a', [
        'test-test-id-1-1',
        'test-test-id-2-1',
        'test-test-id-3-1',
        'test-test-id-1-2',
        'test-test-id-2-2',
        'test-test-id-3-2',
      ])
      await wrapper.get('input[value="test-team-id-1-1"]').setChecked()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.test-id a', [
        'test-test-id-1-1',
        'test-test-id-1-2',
      ])
    })
  })

  describe('when testId prop is set', () => {
    beforeEach(() => {
      propsData = {
        testId: 'test-test-id-2',
        components: [
          createComponent({
            componentNumber: 1,
            additionalFields: {
              testResults: [
                {
                  testId: 'test-test-id-1',
                  priority: 'very-high',
                  outcome: 'fail',
                  message: 'Test Message 1a',
                },
                {
                  testId: 'test-test-id-2',
                  priority: 'high',
                  outcome: 'pass',
                  message: 'Test Message 1b',
                },
              ],
            },
          }),
          createComponent({
            componentNumber: 2,
            additionalFields: {
              testResults: [
                {
                  testId: 'test-test-id-2',
                  priority: 'very-high',
                  outcome: 'fail',
                  message: 'Test Message 2a',
                },
              ],
            },
          }),
          createComponent({
            componentNumber: 3,
            additionalFields: {
              testResults: [
                {
                  testId: 'test-test-id-1',
                  priority: 'very-high',
                  outcome: 'fail',
                  message: 'Test Message 3a',
                },
              ],
            },
          }),
        ],
      }
    })

    test('renders only the test results that match testId prop', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectViewCount(
        wrapper,
        '.fail-count',
        'danger',
        'display-1',
        /1\s+failing test/
      )
      expectViewCount(
        wrapper,
        '.pass-count',
        'success',
        'display-4',
        /1\s+passing test/
      )
      expectTextsInTableRows(wrapper, 'td.test-id a', [
        'test-test-id-2',
        'test-test-id-2',
      ])
      expectHrefsInTableRows(wrapper, 'td.test-id a', [
        '/tests/test-test-id-2',
        '/tests/test-test-id-2',
      ])
    })
  })

  describe('when areaId prop is set', () => {
    beforeEach(() => {
      propsData = {
        areaId: 'test-area-id-1',
        components: [
          createComponentWithTestResults({ componentNumber: 1 }),
          createComponentWithTestResults({ componentNumber: 2 }),
          createComponentWithTestResults({ componentNumber: 3 }),
        ],
      }
    })

    test("renders the test hrefs pointing to the area's test pages", async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.test-id a', [
        'test-test-id-1-1',
        'test-test-id-2-1',
        'test-test-id-3-1',
        'test-test-id-1-2',
        'test-test-id-2-2',
        'test-test-id-3-2',
      ])
      expectHrefsInTableRows(wrapper, 'td.test-id a', [
        '/areas/test-area-id-1/tests/test-test-id-1-1',
        '/areas/test-area-id-1/tests/test-test-id-2-1',
        '/areas/test-area-id-1/tests/test-test-id-3-1',
        '/areas/test-area-id-1/tests/test-test-id-1-2',
        '/areas/test-area-id-1/tests/test-test-id-2-2',
        '/areas/test-area-id-1/tests/test-test-id-3-2',
      ])
    })
  })

  describe('when teamId prop is set', () => {
    beforeEach(() => {
      propsData = {
        teamId: 'test-team-id-1',
        components: [
          createComponentWithTestResults({ componentNumber: 1 }),
          createComponentWithTestResults({ componentNumber: 2 }),
          createComponentWithTestResults({ componentNumber: 3 }),
        ],
      }
    })

    test("renders the test hrefs pointing to the team's test pages", async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.test-id a', [
        'test-test-id-1-1',
        'test-test-id-2-1',
        'test-test-id-3-1',
        'test-test-id-1-2',
        'test-test-id-2-2',
        'test-test-id-3-2',
      ])
      expectHrefsInTableRows(wrapper, 'td.test-id a', [
        '/teams/test-team-id-1/tests/test-test-id-1-1',
        '/teams/test-team-id-1/tests/test-test-id-2-1',
        '/teams/test-team-id-1/tests/test-test-id-3-1',
        '/teams/test-team-id-1/tests/test-test-id-1-2',
        '/teams/test-team-id-1/tests/test-test-id-2-2',
        '/teams/test-team-id-1/tests/test-test-id-3-2',
      ])
    })
  })
})
