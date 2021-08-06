import { mount } from '@vue/test-utils'
import ComponentName from '@/components/ComponentName.vue'

describe('ComponentName', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(ComponentName, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when component prop is undefined', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(``)
    })
  })

  describe('when component prop contains a component', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-id',
        name: 'Test Name',
      }
    })

    test('renders an `a` tag with href pointing at the Component page', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(
        `<a href="/components/test-id">Test Name</a>`
      )
    })
  })
})
