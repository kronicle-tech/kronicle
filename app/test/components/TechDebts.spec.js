import { mount } from '@vue/test-utils'
import TechDebts from '@/components/TechDebts.vue'

describe('TechDebts', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(TechDebts, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when techDebts prop is not set', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(``)
    })
  })

  describe('when techDebts prop is set to an empty array', () => {
    beforeEach(() => {
      propsData.techDebts = []
    })

    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(``)
    })
  })

  describe('when techDebts prop is set to one tech debt', () => {
    beforeEach(() => {
      propsData.techDebts = [
        {
          description: 'Test Description 1',
        },
      ]
    })

    test('renders an unordered list showing the tech debt', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when techDebts prop is set to a tech debt with very high priority', () => {
    beforeEach(() => {
      propsData.techDebts = [
        {
          description: 'Test Description 1',
          priority: 'very-high',
        },
      ]
    })

    test('renders the tech debt with a very high priority badge', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when techDebts prop is set to a tech debt with high priority', () => {
    beforeEach(() => {
      propsData.techDebts = [
        {
          description: 'Test Description 1',
          priority: 'high',
        },
      ]
    })

    test('renders the tech debt with a high priority badge', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when techDebts prop is set to a tech debt with medium priority', () => {
    beforeEach(() => {
      propsData.techDebts = [
        {
          description: 'Test Description 1',
          priority: 'medium',
        },
      ]
    })

    test('renders the tech debt with a medium priority badge', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when techDebts prop is set to a tech debt with low priority', () => {
    beforeEach(() => {
      propsData.techDebts = [
        {
          description: 'Test Description 1',
          priority: 'low',
        },
      ]
    })

    test('renders the tech debt with a low priority badge', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when techDebts prop is set to a tech debt with no priority', () => {
    beforeEach(() => {
      propsData.techDebts = [
        {
          description: 'Test Description 1',
        },
      ]
    })

    test('renders the tech debt with a missing priority badge', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when techDebts prop is set to a tech debt with links', () => {
    beforeEach(() => {
      propsData.techDebts = [
        {
          description: 'Test Description 1',
          links: [
            {
              url: 'http://example.com/test-link-1',
            },
            {
              url: 'http://example.com/test-link-2',
            },
          ],
        },
      ]
    })

    test('renders the tech debt with the links', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when techDebts prop is set to one tech debt with Markdown in the description', () => {
    beforeEach(() => {
      propsData.techDebts = [
        {
          description: 'Text with *bold* formatting',
        },
      ]
    })

    test('renders the tech debt with the Markdown converted to HTML', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when techDebts prop is set to multiple tech debts', () => {
    beforeEach(() => {
      propsData.techDebts = [
        {
          description: 'Test Description 1',
        },
        {
          description: 'Test Description 2',
        },
        {
          description: 'Test Description 3',
        },
      ]
    })

    test('renders an unordered list showing the tech debts', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })
})
