import { mount } from '@vue/test-utils'
import AllComponentsTabs from '@/components/AllComponentsTabs.vue'

describe('AllComponentsTabs', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(AllComponentsTabs, { propsData })
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
