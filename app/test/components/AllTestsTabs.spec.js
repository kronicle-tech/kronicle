import { mount } from '@vue/test-utils'
import AllTestsTabs from '@/components/AllTestsTabs.vue'

describe('AllTestsTabs', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(AllTestsTabs, { propsData })
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
