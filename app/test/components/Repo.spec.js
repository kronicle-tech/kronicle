import { mount } from '@vue/test-utils'
import Repo from '@/components/Repo.vue'

describe('Repo', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(Repo, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when repo prop is not set', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when repo prop contains a repo', () => {
    beforeEach(() => {
      propsData.repo = {
        url: 'https://example.com/repo.git',
      }
    })

    test("renders an `a` tag with href pointing at the repo's url", () => {
      createWrapper()
      expect(wrapper.html()).toEqual(
        `<a href="https://example.com/repo.git" target="_blank">https://example.com/repo.git</a>`
      )
    })
  })
})
