import { mount } from '@vue/test-utils'
import TeamTabs from '@/components/TeamTabs.vue'

describe('TeamTabs', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(TeamTabs, { propsData })
  }

  beforeEach(() => {
    propsData = {
      teamId: 'test-team-id',
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
