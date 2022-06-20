import { mount } from '@vue/test-utils'
import TechDebtTable from '@/components/TechDebtTable.vue'
import { createComponentWithTechDebts } from '~/test/testDataUtils'
import { expectTextsInTableRows } from '~/test/components/tableUtils'

describe('TechDebtTable', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(TechDebtTable, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when components prop is set to an empty array', () => {
    beforeEach(() => {
      propsData.components = []
    })

    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when components is set to multiple components with multiple tech debts', () => {
    beforeEach(() => {
      propsData.components = [
        createComponentWithTechDebts({ componentNumber: 1 }),
        createComponentWithTechDebts({ componentNumber: 2 }),
        createComponentWithTechDebts({ componentNumber: 3 }),
      ]
    })

    test('renders the tech debts', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.description p', [
        'Test Tech Debt Description 1 2',
        'Test Tech Debt Description 2 2',
        'Test Tech Debt Description 3 2',
        'Test Tech Debt Description 1 1',
        'Test Tech Debt Description 2 1',
        'Test Tech Debt Description 3 1',
      ])
    })
  })
})
