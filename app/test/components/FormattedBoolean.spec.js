import { mount } from '@vue/test-utils'
import FormattedBoolean from '@/components/FormattedBoolean.vue'

describe('FormattedBoolean', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(FormattedBoolean, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when value prop is not set', () => {
    test('renders an empty span', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(`<span></span>`)
    })
  })

  describe('when value prop is true', () => {
    beforeEach(() => {
      propsData.value = true
    })

    test('renders a span containing `Yes`', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(`<span>Yes</span>`)
    })
  })

  describe('when value prop is false', () => {
    beforeEach(() => {
      propsData.value = false
    })

    test('renders a span containing `No`', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(`<span>No</span>`)
    })
  })
})
