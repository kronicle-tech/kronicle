import { mount } from '@vue/test-utils'
import TeamTable from '@/components/ComponentTable.vue'
import { createTeam } from '~/test/testDataUtils'

describe('TeamTable', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(TeamTable, { propsData })
  }

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when teams prop is set to an empty array', () => {
    beforeEach(() => {
      propsData = {
        teams: [],
      }
    })

    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when teams is set to multiple teams', () => {
    beforeEach(() => {
      propsData = {
        teams: [createTeam({ teamNumber: 1 }), createTeam({ teamNumber: 2 })],
      }
    })

    test('renders the teams', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when a team has no description', () => {
    beforeEach(() => {
      propsData = {
        teams: [createTeam(1), createTeam(2)],
      }
      delete propsData.teams[0].description
    })

    test('renders the team without a description', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })
})
