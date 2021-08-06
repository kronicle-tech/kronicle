import { mount } from '@vue/test-utils'
import AllScannersTabs from '@/components/AllScannersTabs.vue'

describe('AllScannersTabs', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(AllScannersTabs, { propsData })
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
