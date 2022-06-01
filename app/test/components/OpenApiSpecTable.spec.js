import { mount } from '@vue/test-utils'
import OpenApiSpecTable from '@/components/OpenApiSpecTable.vue'
import { expectTextsInTableRows } from '~/test/components/tableUtils'

describe('OpenApiSpecTable', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(OpenApiSpecTable, { propsData })
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
      expect(wrapper.html()).toEqual(``)
    })
  })

  describe('when components is set to multiple components with multiple OpenAPI specs', () => {
    beforeEach(() => {
      propsData = {
        components: [
          {
            id: 'test-id-1',
            name: 'Test Name 1',
            states: [
              {
                pluginId: 'test-plugin-id',
                type: 'openapi-specs',
                openApiSpecs: [
                  {
                    spec: {},
                    url: 'https://example.com/openapi-spec-1-1',
                    description: 'Test OpenAPI Spec Description 1 1',
                  },
                  {
                    spec: {},
                    url: 'https://example.com/openapi-spec-1-2',
                    description: 'Test OpenAPI Spec Description 1 2',
                  },
                ],
              }
            ],
          },
          {
            id: 'test-id-2',
            name: 'Test Name 2',
            states: [
              {
                pluginId: 'test-plugin-id',
                type: 'openapi-specs',
                openApiSpecs: [
                  {
                    spec: {},
                    url: 'https://example.com/openapi-spec-2-1',
                    description: 'Test OpenAPI Spec Description 2 1',
                  },
                  {
                    spec: {},
                    url: 'https://example.com/openapi-spec-2-2',
                    description: 'Test OpenAPI Spec Description 2 2',
                  },
                ],
              }
            ],
          },
        ],
      }
    })

    test('renders the OpenAPI specs', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.description p', [
        'Test OpenAPI Spec Description 1 1',
        'Test OpenAPI Spec Description 1 2',
        'Test OpenAPI Spec Description 2 1',
        'Test OpenAPI Spec Description 2 2',
      ])
    })
  })

  describe('when an OpenAPI spec has a spec and a URL', () => {
    beforeEach(() => {
      propsData = {
        components: [
          {
            id: 'test-id-1',
            name: 'Test Name 1',
            states: [
              {
                pluginId: 'test-plugin-id',
                type: 'openapi-specs',
                openApiSpecs: [
                  {
                    spec: {},
                    url: 'https://example.com/openapi-spec-1-1',
                    description: 'Test OpenAPI Spec Description 1 1',
                  },
                ],
              }
            ],
          },
        ],
      }
    })

    test('renders the OpenAPI spec with a URL', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when an OpenAPI spec has a spec and a file', () => {
    beforeEach(() => {
      propsData = {
        components: [
          {
            id: 'test-id-1',
            name: 'Test Name 1',
            states: [
              {
                pluginId: 'test-plugin-id',
                type: 'openapi-specs',
                openApiSpecs: [
                  {
                    spec: {},
                    file: '/openapi-spec-1-1.yaml',
                    description: 'Test OpenAPI Spec Description 1 1',
                  },
                ],
              }
            ],
          },
        ],
      }
    })

    test('renders the OpenAPI spec with a file', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when an OpenAPI spec has a URL but no spec', () => {
    beforeEach(() => {
      propsData = {
        components: [
          {
            id: 'test-id-1',
            name: 'Test Name 1',
            states: [
              {
                pluginId: 'test-plugin-id',
                type: 'openapi-specs',
                openApiSpecs: [
                  {
                    url: 'https://example.com/openapi-spec-1-1',
                    description: 'Test OpenAPI Spec Description 1 1',
                  },
                ],
              }
            ],
          },
        ],
      }
    })

    test('renders the OpenAPI spec with a URL and includes badge saying `missing`', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when an OpenAPI spec has a file but no spec', () => {
    beforeEach(() => {
      propsData = {
        components: [
          {
            id: 'test-id-1',
            name: 'Test Name 1',
            states: [
              {
                pluginId: 'test-plugin-id',
                type: 'openapi-specs',
                openApiSpecs: [
                  {
                    file: '/openapi-spec-1-1.yaml',
                    description: 'Test OpenAPI Spec Description 1 1',
                  },
                ],
              }
            ],
          },
        ],
      }
    })

    test('renders the OpenAPI spec with a file and includes badge saying `missing`', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })
})
