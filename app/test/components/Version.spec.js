import { mount } from '@vue/test-utils'
import Version from '@/components/Version.vue'

describe('Version', () => {
  let config
  let wrapper
  const createWrapper = () => {
    wrapper = mount(Version, {
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

  describe('when version config is not set', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when version config is set', () => {
    beforeEach(() => {
      config.version = '1.2.3'
    })

    test('renders the version', () => {
      createWrapper()
      expect(wrapper.get('div').text()).toContain('Kronicle App v1.2.3')
      expect(wrapper.html()).toMatchSnapshot()
    })
  })
})
