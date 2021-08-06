import { mount } from '@vue/test-utils'
import FormattedDateTime from '@/components/FormattedDateTime.vue'

describe('FormattedDateTime', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(FormattedDateTime, { propsData })
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

  describe('when value prop is set to a date time', () => {
    beforeEach(() => {
      propsData.value = '2001-02-03T04:05:06.007Z'
    })

    test('renders the date time with year, month, day, hour and seconds', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(`<span>2001-02-03 04:05</span>`)
    })
  })
})
