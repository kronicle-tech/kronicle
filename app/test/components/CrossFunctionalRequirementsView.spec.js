import CrossFunctionalRequirementsView from '@/components/CrossFunctionalRequirementsView.vue'
import { createViewComponentWrapper } from '~/test/components/viewUtils'
import { createComponentWithCrossFunctionalRequirements } from '~/test/testDataUtils'
import { expectTextsInTableRows } from '~/test/components/tableUtils'

describe('CrossFunctionalRequirementsView', () => {
  let propsData
  let wrapper
  async function createWrapper() {
    wrapper = await createViewComponentWrapper(
      CrossFunctionalRequirementsView,
      {
        propsData,
      }
    )
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

  describe('when components prop is set to multiple components with cross functional requirements', () => {
    beforeEach(() => {
      propsData = {
        components: [
          createComponentWithCrossFunctionalRequirements({
            componentNumber: 1,
          }),
          createComponentWithCrossFunctionalRequirements({
            componentNumber: 2,
          }),
          createComponentWithCrossFunctionalRequirements({
            componentNumber: 3,
          }),
        ],
      }
    })

    test('renders all the cross functional requirements in a table', async () => {
      await createWrapper()
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

    test('when selecting a filter, the filter is applied to the cross functional requirements in the table', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.description p', [
        'Test CFR Description 1 1',
        'Test CFR Description 1 2',
        'Test CFR Description 2 1',
        'Test CFR Description 2 2',
        'Test CFR Description 3 1',
        'Test CFR Description 3 2',
      ])
      await wrapper.get('input[value="test-team-id-1-1"]').setChecked()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.description p', [
        'Test CFR Description 1 1',
        'Test CFR Description 1 2',
      ])
    })
  })
})
