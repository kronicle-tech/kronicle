import { mount } from '@vue/test-utils'
import ComponentTestResultTable from '@/components/ComponentTestResultTable.vue'

describe('ComponentTestResultTable', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(ComponentTestResultTable, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when componentsAndTestResults prop is undefined', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(``)
    })
  })

  describe('when componentsAndTestResults is set to multiple components with multiple test results', () => {
    beforeEach(() => {
      propsData.componentsAndTestResults = [
        {
          component: {
            id: 'test-id-1',
            name: 'Test Name 1',
          },
          testResults: [
            {
              testId: 'test-test-id-1',
              outcome: 'fail',
              priority: 'low',
              message: 'test-message-1',
            },
            {
              testId: 'test-test-id-2',
              outcome: 'pass',
              priority: 'medium',
              message: 'test-message-2',
            },
          ],
        },
        {
          component: {
            id: 'test-id-2',
            name: 'Test Name 2',
          },
          testResults: [
            {
              testId: 'test-test-id-3',
              outcome: 'not-applicable',
              priority: 'high',
              message: 'test-message-4',
            },
            {
              testId: 'test-test-id-4',
              outcome: 'fail',
              priority: 'very-high',
              message: 'test-message-4',
            },
          ],
        },
      ]
    })

    test('renders the components and their test results', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })
})
