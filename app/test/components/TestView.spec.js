import TestView from '@/components/TestView.vue'
import { createViewComponentWrapper } from '~/test/components/viewUtils'
import { expectTextsInTableRows } from '~/test/components/tableUtils'

describe('TestView', () => {
  let propsData
  let wrapper
  async function createWrapper() {
    wrapper = await createViewComponentWrapper(TestView, {
      propsData,
    })
  }

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when components is set to multiple components with multiple test results', () => {
    beforeEach(() => {
      propsData = {
        test: {
          id: 'test-test-id-1',
          description: 'Test Test Description 1',
          notes: 'Test Test Notes 1',
          priority: 'very-high',
        },
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
              {
                testId: 'test-test-id-2',
                priority: 'high',
                outcome: 'success',
                message: 'Test Message 1b',
              },
            ],
          },
          {
            id: 'test-component-id-2',
            name: 'Test Component Name 2',
            testResults: [
              {
                testId: 'test-test-id-1',
                priority: 'very-high',
                outcome: 'fail',
                message: 'Test Message 2a',
              },
              {
                testId: 'test-test-id-2',
                priority: 'high',
                outcome: 'success',
                message: 'Test Message 2b',
              },
            ],
          },
          {
            id: 'test-component-id-3',
            name: 'Test Component Name 3',
            testResults: [
              {
                testId: 'test-test-id-1',
                priority: 'very-high',
                outcome: 'fail',
                message: 'Test Message 3a',
              },
              {
                testId: 'test-test-id-2',
                priority: 'high',
                outcome: 'success',
                message: 'Test Message 3b',
              },
            ],
          },
        ],
      }
    })

    test('renders the details of the test and the test results of the components and only shows test results for this test', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.test-id a', [
        'test-test-id-1',
        'test-test-id-1',
        'test-test-id-1',
      ])
    })
  })
})
