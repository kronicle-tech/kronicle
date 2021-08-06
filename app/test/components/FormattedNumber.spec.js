import { mount } from '@vue/test-utils'
import FormattedNumber from '@/components/FormattedNumber.vue'

describe('FormattedNumber', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(FormattedNumber, { propsData })
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

  describe('when value prop is set to zero', () => {
    beforeEach(() => {
      propsData.value = 0
    })

    test('renders a span containing zero', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(`<span>0</span>`)
    })
  })

  describe('when value prop is set to a number', () => {
    beforeEach(() => {
      propsData.value = 1
    })

    test('renders a span containing the number', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(`<span>1</span>`)
    })
  })

  describe('when value prop is set to a number with more than 3 digits', () => {
    beforeEach(() => {
      propsData.value = 1234
    })

    test('renders a span containing the number with a comma every 3 digits', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(`<span>1,234</span>`)
    })
  })
})
