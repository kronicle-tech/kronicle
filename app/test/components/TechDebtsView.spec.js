import TechDebtsView from '@/components/TechDebtsView.vue'
import { createViewComponentWrapper } from '~/test/components/viewUtils'
import { createComponentWithTechDebts } from '~/test/testDataUtils'
import { expectTextsInTableRows } from '~/test/components/tableUtils'

describe('TechDebtsView', () => {
  let propsData
  let wrapper
  async function createWrapper() {
    wrapper = await createViewComponentWrapper(TechDebtsView, {
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

    test('renders no table', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expect(wrapper.find('table').exists()).toBe(false)
    })
  })

  describe('when components prop is set to multiple components with tech debts', () => {
    beforeEach(() => {
      propsData = {
        components: [
          createComponentWithTechDebts({
            componentNumber: 1,
          }),
          createComponentWithTechDebts({
            componentNumber: 2,
          }),
          createComponentWithTechDebts({
            componentNumber: 3,
          }),
        ],
      }
    })

    test('renders all the tech debts in a table', async () => {
      await createWrapper()
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

    test('when selecting a filter, the filter is applied to the tech debts in the table', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.description p', [
        'Test Tech Debt Description 1 2',
        'Test Tech Debt Description 2 2',
        'Test Tech Debt Description 3 2',
        'Test Tech Debt Description 1 1',
        'Test Tech Debt Description 2 1',
        'Test Tech Debt Description 3 1',
      ])
      await wrapper.get('input[value="test-team-id-1-1"]').setChecked()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.description p', [
        'Test Tech Debt Description 1 2',
        'Test Tech Debt Description 1 1',
      ])
    })
  })
})
