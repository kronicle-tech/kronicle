import { mount } from '@vue/test-utils'
import AreaTabs from '@/components/AreaTabs.vue'

describe('AreaTabs', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(AreaTabs, { propsData })
  }

  beforeEach(() => {
    propsData = {
      areaId: 'test-area-id',
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
