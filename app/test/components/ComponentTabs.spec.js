import { mount } from '@vue/test-utils'
import ComponentTabs from '@/components/ComponentTabs.vue'

describe('ComponentTabs', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(ComponentTabs, { propsData })
  }

  beforeEach(() => {
    propsData = {
      componentId: 'test-component-id-1',
      stateTypes: [],
    }
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when there are no state types', () => {
    beforeEach(() => {
      propsData.stateTypes = []
    })

    test('renders the component with less tabs', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when there are no state types', () => {
    beforeEach(() => {
      propsData.stateTypes = [
        'lines-of-code',
      ]
    })

    test('renders the component with all tabs', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })
})
