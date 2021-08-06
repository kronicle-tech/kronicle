import { mount } from '@vue/test-utils'
import AllTeamsTabs from '@/components/AllTeamsTabs.vue'

describe('AllTeamsTabs', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(AllTeamsTabs, { propsData })
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
