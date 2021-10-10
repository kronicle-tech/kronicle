import { mount } from '@vue/test-utils'
import KeySoftwareBadges from '@/components/KeySoftwareBadges.vue'

describe('KeySoftwareBadges', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(KeySoftwareBadges, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when keySoftware prop is not set', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(``)
    })
  })

  describe('when keySoftware prop is set to an empty array', () => {
    beforeEach(() => {
      propsData.keySoftware = []
    })

    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(``)
    })
  })

  describe('when keySoftware prop is set to one key software item with 1 version', () => {
    beforeEach(() => {
      propsData.keySoftware = [
        {
          name: 'Test Key Software',
          versions: ['1.2.3'],
        },
      ]
    })

    test('renders 1 badge containing 1 version', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when keySoftware prop is set to one key software item with multiple versions', () => {
    beforeEach(() => {
      propsData.keySoftware = [
        {
          name: 'Test Key Software',
          versions: ['1.0.0', '1.1.0', '1.2.0'],
        },
      ]
    })

    test('renders 1 badge containing all the versions', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when keySoftware prop is set to multiple key software items', () => {
    beforeEach(() => {
      propsData.keySoftware = [
        {
          name: 'Test Key Software 1',
          versions: ['1.0.0', '1.1.0', '1.2.0'],
        },
        {
          name: 'Test Key Software 2',
          versions: ['2.0.0', '2.1.0', '2.2.0'],
        },
        {
          name: 'Test Key Software 3',
          versions: ['3.0.0', '3.1.0', '3.2.0'],
        },
      ]
    })

    test('renders 3 badges', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })
})
