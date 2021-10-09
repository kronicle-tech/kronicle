import { mount } from '@vue/test-utils'
import Markdown from '@/components/Markdown.vue'

describe('Markdown', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(Markdown, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  test('renders nothing when markdown prop is undefined', () => {
    createWrapper()
    expect(wrapper.html()).toEqual(`<div></div>`)
  })

  describe('when markdown prop contains HTML', () => {
    beforeEach(() => {
      propsData.markdown = `Content`
    })

    test('renders markdown prop as HTML', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })

    describe('toc prop', () => {
      beforeEach(() => {
        propsData.toc = true
      })

      describe('when true', () => {
        test('renders Table of Contents section', () => {
          createWrapper()
          expect(wrapper.html()).toMatchSnapshot()
        })

        test('renders Table of Contents section including headings', () => {
          propsData.markdown = `# Main Heading
## Subheading`
          createWrapper()
          expect(wrapper.html()).toMatchSnapshot()
        })
      })
    })

    describe('when markdown prop contains code', () => {
      beforeEach(() => {
        propsData.markdown = `\`\`\`javascript
console.log()
\`\`\``
      })

      test('renders highlighted code', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })

      test('renders an error for an unknown code language', () => {
        propsData.markdown = `\`\`\`unknown
console.log()
\`\`\``
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })
    })

    describe('when markdown prop contains a CSS class', () => {
      beforeEach(() => {
        propsData.markdown = `<h1 class="example">Main Heading</h1>`
      })

      test('renders the CSS class', () => {
        createWrapper()
        expect(wrapper.html()).toEqual(`<div>
  <h1 class="example">Main Heading</h1>
</div>`)
      })
    })

    describe('when markdown prop contains unsafe HTML', () => {
      beforeEach(() => {
        propsData.markdown = `<img src="x" onerror="alert('charlie')">`
      })

      test('sanitizes unsafe HTML', () => {
        createWrapper()
        expect(wrapper.html()).toEqual(`<div>
  <img src="x"></div>`)
      })
    })
  })
})
