import { mount } from '@vue/test-utils'
import ComponentPanel from '@/components/ComponentPanel.vue'

describe('ComponentPanel', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(ComponentPanel, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when component prop is undefined', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when component is set to a component', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-id',
        name: 'Test Name',
      }
    })

    test("renders a h3 tag containing the component name and a link to the component's page", () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when component is set to just a component id', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-id',
      }
    })

    test('renders a h3 tag containing the component id', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when teams is set', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-id',
        name: 'Test Name',
        teams: [
          {
            teamId: 'test-team-id-1',
          },
          {
            teamId: 'test-team-id-2',
          },
        ],
      }
    })

    test('renders the teams', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when tags is set', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-id',
        name: 'Test Name',
        tags: ['test-tag-1', 'test-tag-2'],
      }
    })

    test('renders the tags', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when description is set', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-id',
        name: 'Test Name',
        description: 'Description',
      }
    })

    test('renders the description', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when notes is set', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-id',
        name: 'Test Name',
        notes: 'Notes',
      }
    })

    test('renders the notes', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when responsibilities is set', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-id',
        name: 'Test Name',
        responsibilities: [
          {
            description: 'Test Description 1',
          },
          {
            description: 'Test Description 2',
          },
        ],
      }
    })

    test('renders the responsibilities', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when links is set', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-id',
        name: 'Test Name',
        links: [
          {
            url: 'https://example.org/test-1',
          },
          {
            url: 'https://example.org/test-2',
          },
        ],
      }
    })

    test('renders the links', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when techDebts is set', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-id',
        name: 'Test Name',
        techDebts: [
          {
            description: 'Test Description 1',
          },
          {
            description: 'Test Description 2',
          },
        ],
      }
    })

    test('renders the techDebts', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })
})
