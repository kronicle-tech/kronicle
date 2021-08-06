import { mount } from '@vue/test-utils'
import TeamName from '@/components/TeamName.vue'

describe('TeamName', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(TeamName, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when team prop is undefined', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(``)
    })
  })

  describe('when team prop contains a team', () => {
    beforeEach(() => {
      propsData.team = {
        id: 'test-team-id',
        name: 'Test Team Name',
      }
    })

    test('renders an `a` tag with href pointing at the Team page', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(
        `<a href="/teams/test-team-id" class="team-name">Test Team Name</a>`
      )
    })
  })

  describe('when team prop contains a team with an id but no name', () => {
    beforeEach(() => {
      propsData.team = {
        id: 'test-team-id',
      }
    })

    test('renders an `a` tag with the text set to the test id', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(
        `<a href="/teams/test-team-id" class="team-name">test-team-id</a>`
      )
    })
  })
})
