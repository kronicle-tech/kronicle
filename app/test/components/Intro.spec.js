import { mount } from '@vue/test-utils'
import Intro from '@/components/Intro.vue'

describe('Intro', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(Intro, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when no props are set', () => {
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

  describe('when title prop is set', () => {
    beforeEach(() => {
      propsData.title = 'Test Title'
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

  describe('when markdown prop is set', () => {
    beforeEach(() => {
      propsData.markdown = '*Test Message*'
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

  describe('when all props are set', () => {
    beforeEach(() => {
      propsData.title = 'Test Title'
      propsData.markdown = '*Test Message*'
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
