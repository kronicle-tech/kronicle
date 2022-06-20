import { mount } from '@vue/test-utils'
import ComponentTeams from '~/components/ComponentTeams.vue'

describe('Teams', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(ComponentTeams, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when teams prop is not set', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when teams prop is set to an empty array', () => {
    beforeEach(() => {
      propsData.teams = []
    })

    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when teams prop is set to one team', () => {
    beforeEach(() => {
      propsData.teams = [
        {
          id: 'test-team-1',
        },
      ]
    })

    test('renders an unordered list showing the team', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when teams prop is set to a team with type of primary', () => {
    beforeEach(() => {
      propsData.teams = [
        {
          id: 'test-team-1',
          type: 'primary',
        },
      ]
    })

    test('renders the team with a primary team badge', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when teams prop is set to a team with type of previous', () => {
    beforeEach(() => {
      propsData.teams = [
        {
          id: 'test-team-1',
          type: 'previous',
        },
      ]
    })

    test('renders the team with a previous team badge', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when teams prop is set to a team with a description', () => {
    beforeEach(() => {
      propsData.teams = [
        {
          id: 'test-team-1',
          description: 'Test Description 1',
        },
      ]
    })

    test('renders the team with a description', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when teams prop is set to a team with type of primary and a description', () => {
    beforeEach(() => {
      propsData.teams = [
        {
          id: 'test-team-1',
          type: 'primary',
          description: 'Test Description 1',
        },
      ]
    })

    test('renders the team with a primary team badge and a description', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when teams prop is set to one team with Markdown in the description', () => {
    beforeEach(() => {
      propsData.teams = [
        {
          id: 'test-team-1',
          description: 'Text with *bold* formatting',
        },
      ]
    })

    test('renders the team with the Markdown converted to HTML', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when teams prop is set to multiple teams', () => {
    beforeEach(() => {
      propsData.teams = [
        {
          id: 'test-team-1',
        },
        {
          id: 'test-team-2',
        },
        {
          id: 'test-team-3',
        },
      ]
    })

    test('renders an unordered list showing the teams', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })
})
