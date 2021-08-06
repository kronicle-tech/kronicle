import { mount } from '@vue/test-utils'
import AreaName from '@/components/AreaName.vue'

describe('AreaName', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(AreaName, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when area prop is undefined', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(``)
    })
  })

  describe('when area prop contains an area', () => {
    beforeEach(() => {
      propsData.area = {
        id: 'test-area-id',
        name: 'Test Area Name',
      }
    })

    test('renders an `a` tag with href pointing at the Area page', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(
        `<a href="/areas/test-area-id">Test Area Name</a>`
      )
    })
  })

  describe('when area prop contains an area with no name', () => {
    beforeEach(() => {
      propsData.area = {
        id: 'test-area-id',
      }
    })

    test('renders an `a` tag with href pointing at the Area page, with the area id as the text', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(
        `<a href="/areas/test-area-id">test-area-id</a>`
      )
    })
  })
})
