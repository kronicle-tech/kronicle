import { mount } from '@vue/test-utils'
import OpenApiSpecs from '@/components/OpenApiSpecs.vue'

describe('OpenApiSpecs', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(OpenApiSpecs, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when openApiSpecs array is not set', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-component-id',
      }
    })

    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when openApiSpecs array is set to an empty array', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-component-id',
        openApiSpecs: [],
      }
    })

    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when openApiSpecs array is set to one OpenAPI spec', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-component-id',
        openApiSpecs: [
          {
            scannerId: 'test-scanner-1',
            url: 'https://example.com/',
            description: 'Test Description 1',
            spec: {},
          },
        ],
      }
    })

    test('renders an unordered list showing the OpenAPI spec', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when openApiSpecs array is set to an OpenAPI spec with no spec', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-component-id',
        openApiSpecs: [
          {
            scannerId: 'test-scanner-1',
            url: 'https://example.com/',
            description: 'Test Description 1',
          },
        ],
      }
    })

    test('renders the OpenAPI spec with a `missing` badge', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when openApiSpecs array is set to an OpenAPI spec with a url', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-component-id',
        openApiSpecs: [
          {
            scannerId: 'test-scanner-1',
            url: 'https://example.com/',
            description: 'Test Description 1',
            spec: {},
          },
        ],
      }
    })

    test('renders the OpenAPI spec showing the URL as the spec name', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when openApiSpecs array is set to an OpenAPI spec with a file', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-component-id',
        openApiSpecs: [
          {
            scannerId: 'test-scanner-1',
            file: 'test/file.yaml',
            description: 'Test Description 1',
            spec: {},
          },
        ],
      }
    })

    test('renders the OpenAPI spec showing the file as the spec name', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when openApiSpecs array is set to an OpenAPI spec with a description', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-component-id',
        openApiSpecs: [
          {
            scannerId: 'test-scanner-1',
            url: 'https://example.com/',
            description: 'Test Description 1',
            spec: {},
          },
        ],
      }
      propsData.componentId = 'test-component-id'
    })

    test('renders the OpenAPI spec with a description', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when openApiSpecs array is set to an OpenAPI spec with no description', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-component-id',
        openApiSpecs: [
          {
            scannerId: 'test-scanner-1',
            url: 'https://example.com/',
            spec: {},
          },
        ],
      }
    })

    test('renders the OpenAPI spec without a description', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when openApiSpecs array is set to one OpenAPI spec with Markdown in the description', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-component-id',
        openApiSpecs: [
          {
            scannerId: 'test-scanner-1',
            url: 'https://example.com/',
            description: 'Text with *bold* formatting',
            spec: {},
          },
        ],
      }
    })

    test('renders the OpenAPI spec with the Markdown converted to HTML', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when openApiSpecs array is set to multiple OpenAPI specs', () => {
    beforeEach(() => {
      propsData.component = {
        id: 'test-component-id',
        openApiSpecs: [
          {
            scannerId: 'test-scanner-2',
            url: 'https://example.com/',
            description: 'Test Description 2',
            spec: {},
          },
          {
            scannerId: 'test-scanner-2',
            url: 'https://example.com/',
            description: 'Test Description 2',
            spec: {},
          },
        ],
      }
    })

    test('renders an unordered list showing the OpenAPI specs', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })
})
