import { mount } from '@vue/test-utils'
import Intro from '@/components/Intro.vue'

describe('Intro', () => {
  let config
  let wrapper
  const createWrapper = () => {
    wrapper = mount(Intro, {
      mocks: {
        $config: config,
      },
    })
  }

  beforeEach(() => {
    config = {
      intro: {}
    }
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when intro config is not set', () => {
    test('renders default intro', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(
        expect.stringContaining('<h1 class="display-3">Kronicle</h1>')
      )
      expect(wrapper.html()).toEqual(
        expect.stringContaining('<p>Kronicle contains information on the organisation\'s components, teams and areas.</p>')
      )
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when intro config is set to undefined', () => {
    beforeEach(() => {
      config.intro = {
        title: undefined,
        markdown: undefined,
      }
    })

    test('renders default intro', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(
        expect.stringContaining('<h1 class="display-3">Kronicle</h1>')
      )
      expect(wrapper.html()).toEqual(
        expect.stringContaining('<p>Kronicle contains information on the organisation\'s components, teams and areas.</p>')
      )
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when intro.title config is set', () => {
    beforeEach(() => {
      config.intro = {
        title: 'Test Title',
      }
    })

    test('renders the custom intro title with default markdown', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(
        expect.stringContaining('<h1 class="display-3">Test Title</h1>')
      )
      expect(wrapper.html()).toEqual(
        expect.stringContaining('<p>Kronicle contains information on the organisation\'s components, teams and areas.</p>')
      )
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when intro.markdown config is set', () => {
    beforeEach(() => {
      config.intro = {
        markdown: '*Test Message*',
      }
    })

    test('renders the custom intro markdown with default title', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(
        expect.stringContaining('<h1 class="display-3">Kronicle</h1>')
      )
      expect(wrapper.html()).toEqual(
        expect.stringContaining('<p><em>Test Message</em></p>')
      )
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when all intro config is set', () => {
    beforeEach(() => {
      config.intro = {
        title: 'Test Title',
        markdown: '*Test Message*',
      }
    })

    test('renders the whole custom intro', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(
        expect.stringContaining('<h1 class="display-3">Test Title</h1>')
      )
      expect(wrapper.html()).toEqual(
        expect.stringContaining('<p><em>Test Message</em></p>')
      )
      expect(wrapper.html()).toMatchSnapshot()
    })
  })
})
