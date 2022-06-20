import { mount } from '@vue/test-utils'
import CrossFunctionalRequirementTable from '@/components/CrossFunctionalRequirementTable.vue'
import { createComponentWithCrossFunctionalRequirements } from '~/test/testDataUtils'
import { expectTextsInTableRows } from '~/test/components/tableUtils'

describe('CrossFunctionalRequirementTable', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(CrossFunctionalRequirementTable, { propsData })
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

  describe('when components is set to multiple components with multiple cross functional requirements', () => {
    beforeEach(() => {
      propsData.components = [
        createComponentWithCrossFunctionalRequirements({ componentNumber: 1 }),
        createComponentWithCrossFunctionalRequirements({ componentNumber: 2 }),
        createComponentWithCrossFunctionalRequirements({ componentNumber: 3 }),
      ]
    })

    test('renders the cross functional requirements', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.description p', [
        'Test CFR Description 1 1',
        'Test CFR Description 1 2',
        'Test CFR Description 2 1',
        'Test CFR Description 2 2',
        'Test CFR Description 3 1',
        'Test CFR Description 3 2',
      ])
    })
  })
})
