import { mount } from '@vue/test-utils'
import AllAreasTabs from '@/components/AllAreasTabs.vue'

describe('AllAreasTabs', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(AllAreasTabs, { propsData })
  }

  beforeEach(() => {
    propsData = {}
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
