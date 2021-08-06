import { mount } from '@vue/test-utils'
import ReadmeContent from '@/components/ReadmeContent.vue'

describe('ReadmeContent', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(ReadmeContent, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when readme prop is not set', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual(``)
    })
  })

  describe('when readme prop is set to a readme with the file extension `.md`', () => {
    beforeEach(() => {
      propsData.readme = {
        fileName: 'anything-1.md',
        content: 'Test Content',
      }
    })

    test('renders the readme content as Markdown', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })

    describe('when readme content contains Markdown', () => {
      beforeEach(() => {
        propsData.readme.content = 'Text with *bold* formatting'
      })

      test('renders the readme content with Markdown converted to HTML', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })
    })

    describe('when readme content contains headings`', () => {
      beforeEach(() => {
        propsData.readme.content = '# Test Heading'
      })

      test('renders the readme content with a table of contents', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })
    })
  })

  describe('when readme prop is set to a readme with no `.md` file extension', () => {
    beforeEach(() => {
      propsData.readme = {
        fileName: 'anything-1.txt',
        content: 'Test Content',
      }
    })

    test('renders the readme content in a pre tag', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })

    describe('when readme content contains HTML', () => {
      beforeEach(() => {
        propsData.readme.content = 'Text with <span>HTML</span>'
      })

      test('renders the readme content with the HTML escaped', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })
    })
  })
})
