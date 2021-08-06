import { mount } from '@vue/test-utils'
import FormattedAge from '@/components/FormattedAge.vue'
import MockDate from 'mockdate'

describe('FormattedAge', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(FormattedAge, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    MockDate.reset()
    wrapper.destroy()
    wrapper = null
  })

  describe('when value prop is not set', () => {
    test('renders an empty span', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(`<span></span>`)
    })
  })

  describe('when value prop is set to a date time string', () => {
    beforeEach(() => {
      propsData.value = '1970-01-01T00:00:00.000Z'
      MockDate.set(Date.parse('1972-04-05T00:00:00.000Z'))
    })

    test('renders a span containing the years, months and days since that date time', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(`<span>2 years, 3 months, 4 days</span>`)
    })
  })

  describe('when value prop is set to a date time string less than 1 month ago', () => {
    beforeEach(() => {
      propsData.value = '1970-01-01T00:00:00.000Z'
      MockDate.set(Date.parse('1970-01-28T00:00:00.000Z'))
    })

    test('renders a span containing just the days since the date time', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(`<span>27 days</span>`)
    })
  })

  describe('when value prop is set to a date time string for today', () => {
    beforeEach(() => {
      propsData.value = '1970-01-01T00:00:00.000Z'
      MockDate.set(Date.parse('1970-01-01T00:00:00.000Z'))
    })

    test('renders a span containing 0 days', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(`<span>0 days</span>`)
    })
  })

  describe('when value prop is set to a date time string that is a faction of a day different from now', () => {
    beforeEach(() => {
      propsData.value = '1970-01-01T01:00:00.000Z'
      MockDate.set(Date.parse('1970-01-03T02:00:00.000Z'))
    })

    test('renders a span containing an exact number of days', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(`<span>2 days</span>`)
    })
  })

  describe('when value prop is set to a date time string that is 1 unit different from now', () => {
    beforeEach(() => {
      propsData.value = '1970-01-01T01:00:00.000Z'
      MockDate.set(Date.parse('1970-01-02T02:00:00.000Z'))
    })

    test('renders a span containing the singular version of the time unit', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(`<span>1 day</span>`)
    })
  })

  describe('when value prop is set to a date time string that is more than 1 unit different from now', () => {
    beforeEach(() => {
      propsData.value = '1970-01-01T00:00:00.000Z'
      MockDate.set(Date.parse('1970-01-03T00:00:00.000Z'))
    })

    test('renders a span containing the plural version of the time unit', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(`<span>2 days</span>`)
    })
  })
})
