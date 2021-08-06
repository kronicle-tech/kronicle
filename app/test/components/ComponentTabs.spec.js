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
    }
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  test('renders the component', () => {
    createWrapper()
    expect(wrapper.html()).toMatchSnapshot()
  })
})
