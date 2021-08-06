import { mount } from '@vue/test-utils'
import TestId from '@/components/TestId.vue'

describe('TestId', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(TestId, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when testId prop is set', () => {
    beforeEach(() => {
      propsData.testId = 'test-test-id'
    })

    test('renders an `a` tag with href pointing at the test page', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(
        `<a href="/tests/test-test-id">test-test-id</a>`
      )
    })
  })

  describe('when testId and teamId props are set', () => {
    beforeEach(() => {
      propsData.testId = 'test-test-id'
      propsData.teamId = 'test-team-id'
    })

    test("renders an `a` tag with href pointing at the team's test page", () => {
      createWrapper()
      expect(wrapper.html()).toEqual(
        `<a href="/teams/test-team-id/tests/test-test-id">test-test-id</a>`
      )
    })
  })

  describe('when testId and areaId props are set', () => {
    beforeEach(() => {
      propsData.testId = 'test-test-id'
      propsData.areaId = 'test-area-id'
    })

    test("renders an `a` tag with href pointing at the area's test page", () => {
      createWrapper()
      expect(wrapper.html()).toEqual(
        `<a href="/areas/test-area-id/tests/test-test-id">test-test-id</a>`
      )
    })
  })
})
