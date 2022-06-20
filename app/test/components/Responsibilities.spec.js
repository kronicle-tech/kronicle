import { mount } from '@vue/test-utils'
import Responsibilities from '@/components/Responsibilities.vue'

describe('Responsibilities', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(Responsibilities, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when responsibilities prop is not set', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when responsibilities prop is set to an empty array', () => {
    beforeEach(() => {
      propsData.responsibilities = []
    })

    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when responsibilities prop is set to one responsibility', () => {
    beforeEach(() => {
      propsData.responsibilities = [
        {
          description: 'Test Description 1',
        },
      ]
    })

    test('renders an unordered list showing the responsibility', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when responsibilities prop is set to one responsibility with Markdown in the description', () => {
    beforeEach(() => {
      propsData.responsibilities = [
        {
          description: 'Text with *bold* formatting',
        },
      ]
    })

    test('renders the responsibility with the Markdown converted to HTML', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when responsibilities prop is set to multiple responsibilities', () => {
    beforeEach(() => {
      propsData.responsibilities = [
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

    test('renders an unordered list showing the responsibilities', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })
})
