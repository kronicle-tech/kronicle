import { mount } from '@vue/test-utils'
import Links from '@/components/Links.vue'

describe('Links', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(Links, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when links prop is not set', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(``)
    })
  })

  describe('when links prop is set to an empty array', () => {
    beforeEach(() => {
      propsData.links = []
    })

    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(``)
    })
  })

  describe('when links prop is set to one link', () => {
    beforeEach(() => {
      propsData.links = [
        {
          url: 'test-url-1',
        },
      ]
    })

    test('renders an unordered list showing the link', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when links prop is set to a link with a description', () => {
    beforeEach(() => {
      propsData.links = [
        {
          url: 'test-url-1',
          description: 'Test Description 1',
        },
      ]
    })

    test('renders the link with a description', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when links prop is set to one link with Markdown in the description', () => {
    beforeEach(() => {
      propsData.links = [
        {
          url: 'test-url-1',
          description: 'Text with *bold* formatting',
        },
      ]
    })

    test('renders the link with the Markdown converted to HTML', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when links prop is set to multiple links', () => {
    beforeEach(() => {
      propsData.links = [
        {
          url: 'test-url-1',
        },
        {
          url: 'test-url-2',
        },
        {
          url: 'test-url-3',
        },
      ]
    })

    test('renders an unordered list showing the links', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })
})
