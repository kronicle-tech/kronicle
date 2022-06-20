import { mount } from '@vue/test-utils'
import ComponentTable from '@/components/ComponentTable.vue'
import { createComponent } from '~/test/testDataUtils'

describe('ComponentTable', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(ComponentTable, { propsData })
  }

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when components prop is set to an empty array', () => {
    beforeEach(() => {
      propsData = {
        components: [],
      }
    })

    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when components is set to multiple components', () => {
    beforeEach(() => {
      propsData = {
        components: [
          createComponent({ componentNumber: 1 }),
          createComponent({ componentNumber: 2 }),
        ],
      }
    })

    test('renders the components', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when a component has no description', () => {
    beforeEach(() => {
      propsData = {
        components: [createComponent(1), createComponent(2)],
      }
      delete propsData.components[0].description
    })

    test('renders the component without a description', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })
})
