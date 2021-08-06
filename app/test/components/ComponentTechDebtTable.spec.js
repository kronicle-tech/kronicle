import { mount } from '@vue/test-utils'
import ComponentTechDebtTable from '@/components/ComponentTechDebtTable.vue'

describe('ComponentTechDebtTable', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(ComponentTechDebtTable, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when componentsAndTechDebts prop is undefined', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(``)
    })
  })

  describe('when componentsAndTechDebts is set to multiple components with multiple tech debts', () => {
    beforeEach(() => {
      propsData.componentsAndTechDebts = [
        {
          component: {
            id: 'test-id-1',
            name: 'Test Name 1',
          },
          techDebts: [
            {
              description: 'Test Description 1a',
            },
            {
              description: 'Test Description 1b',
            },
          ],
        },
        {
          component: {
            id: 'test-id-2',
            name: 'Test Name 2',
          },
          techDebts: [
            {
              description: 'Test Description 2a',
            },
            {
              description: 'Test Description 2b',
            },
          ],
        },
      ]
    })

    test('renders the components and their tech debts', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })
})
