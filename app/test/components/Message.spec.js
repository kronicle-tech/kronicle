import { mount } from '@vue/test-utils'
import Message from '@/components/Message.vue'

describe('Message', () => {
  let config
  let wrapper
  const createWrapper = () => {
    wrapper = mount(Message, {
      mocks: {
        $config: config,
      },
    })
  }

  beforeEach(() => {
    config = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when messageMarkdown config is not set', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when messageMarkdown config is set', () => {
    beforeEach(() => {
      config.messageMarkdown = '# Test Message'
    })

    test('renders an info alert showing the message as markdown', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expect(wrapper.html()).toEqual(
        expect.stringContaining('<h1>Test Message</h1>')
      )
      expect(wrapper.get('div[role="alert"]').classes()).toContain('alert-info')
    })

    describe('when messageVariant config is set', () => {
      beforeEach(() => {
        config.messageVariant = 'warning'
      })

      test('renders the alert with the specified variant', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
        expect(wrapper.get('div[role="alert"]').classes()).toContain(
          'alert-warning'
        )
      })
    })
  })
})
