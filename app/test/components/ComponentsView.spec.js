import ComponentsView from '@/components/ComponentsView.vue'
import { createViewComponentWrapper } from '~/test/components/viewUtils'
import { createComponent } from '~/test/testDataUtils'
import { expectTextsInTableRows } from '~/test/components/tableUtils'

describe('ComponentsView', () => {
  let propsData
  let wrapper
  async function createWrapper() {
    wrapper = await createViewComponentWrapper(ComponentsView, {
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

  describe('when components prop is set to multiple components', () => {
    beforeEach(() => {
      propsData = {
        components: [
          createComponent({ componentNumber: 1 }),
          createComponent({ componentNumber: 2 }),
          createComponent({ componentNumber: 3 }),
        ],
      }
    })

    test('renders all the components in a table', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.name a', [
        'Test Component Name 1',
        'Test Component Name 2',
        'Test Component Name 3',
      ])
    })

    test('when selecting a filter, the filter is applied to the components in the table', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.name a', [
        'Test Component Name 1',
        'Test Component Name 2',
        'Test Component Name 3',
      ])
      await wrapper.get('input[value="test-team-id-1-1"]').setChecked()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.name a', ['Test Component Name 1'])
    })
  })
})
