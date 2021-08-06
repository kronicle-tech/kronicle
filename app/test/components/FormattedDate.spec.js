import { mount } from '@vue/test-utils'
import FormattedDate from '@/components/FormattedDate.vue'

describe('FormattedDate', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(FormattedDate, { propsData })
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

  describe('when value prop is set to a date', () => {
    beforeEach(() => {
      propsData.value = '2001-02-03'
    })

    test('renders the date', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(`<span>2001-02-03</span>`)
    })
  })
})
