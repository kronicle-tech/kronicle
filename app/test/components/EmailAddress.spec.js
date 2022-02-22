import { mount } from '@vue/test-utils'
import EmailAddress from '@/components/EmailAddress.vue'

describe('EmailAddress', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(EmailAddress, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when emailAddress prop is undefined', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(``)
    })
  })

  describe('when emailAddress prop is set to an email address', () => {
    beforeEach(() => {
      propsData.emailAddress = 'example@example.com'
    })

    test('renders an `a` tag with href pointing at the email address', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(
        `<a href="mailto:example@example.com">example@javaimports.com</a>`
      )
    })
  })
})
