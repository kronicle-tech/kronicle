import OpenApiSpecsView from '@/components/OpenApiSpecsView.vue'
import { createViewComponentWrapper } from '~/test/components/viewUtils'
import { createComponent } from '~/test/testDataUtils'
import { expectTextsInTableRows } from '~/test/components/tableUtils'

describe('OpenApiSpecsView', () => {
  let propsData
  let wrapper
  async function createWrapper() {
    wrapper = await createViewComponentWrapper(OpenApiSpecsView, {
      propsData,
    })
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

    test('renders a large count of zero with danger styling', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when components prop is set components with no OpenAPI specs', () => {
    beforeEach(() => {
      propsData = {
        components: [
          createComponent({ componentNumber: 1 }),
          createComponent({ componentNumber: 2 }),
        ],
      }
    })

    test('renders a large count of zero with danger styling', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when components prop is set a component with 1 OpenAPI spec', () => {
    beforeEach(() => {
      propsData = {
        components: [
          createComponent({
            componentNumber: 1,
            additionalStates: [
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
              },
            ],
          }),
        ],
      }
    })

    test('renders a small count of 1 with success styling', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when components prop is set a component with 2 OpenAPI specs', () => {
    beforeEach(() => {
      propsData = {
        components: [
          createComponent({
            componentNumber: 1,
            additionalStates: [
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
              },
            ],
          }),
        ],
      }
    })

    test('renders a small count of 2 with success styling', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })

  describe('when components is set to multiple components with multiple OpenAPI specs', () => {
    beforeEach(() => {
      propsData = {
        components: [
          createComponent({
            componentNumber: 1,
            additionalStates: [
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
              },
            ],
          }),
          createComponent({
            componentNumber: 2,
            additionalStates: [
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
              },
            ],
          }),
        ],
      }
    })

    test('renders the OpenAPI specs', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.description p', [
        'Test OpenAPI Spec Description 1 1',
        'Test OpenAPI Spec Description 1 2',
        'Test OpenAPI Spec Description 2 1',
        'Test OpenAPI Spec Description 2 2',
      ])
    })

    test('when selecting a filter, the filter is applied to the OpenAPI specs in the table', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.description p', [
        'Test OpenAPI Spec Description 1 1',
        'Test OpenAPI Spec Description 1 2',
        'Test OpenAPI Spec Description 2 1',
        'Test OpenAPI Spec Description 2 2',
      ])
      await wrapper.get('input[value="test-team-id-1-1"]').setChecked()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.description p', [
        'Test OpenAPI Spec Description 1 1',
        'Test OpenAPI Spec Description 1 2',
      ])
    })
  })

  describe('when an OpenAPI spec has a spec and a URL', () => {
    beforeEach(() => {
      propsData = {
        components: [
          createComponent({
            componentNumber: 1,
            additionalStates: [
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
              },
            ],
          }),
        ],
      }
    })

    test('renders the OpenAPI spec with a URL', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expect(wrapper.findAll('table tbody tr')).toHaveLength(1)
    })
  })

  describe('when an OpenAPI spec has a spec and a file', () => {
    beforeEach(() => {
      propsData = {
        components: [
          createComponent({
            componentNumber: 1,
            additionalStates: [
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
              },
            ],
          }),
        ],
      }
    })

    test('renders the OpenAPI spec with a file', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expect(wrapper.findAll('table tbody tr')).toHaveLength(1)
    })
  })

  describe('when an OpenAPI spec has a URL but no spec', () => {
    beforeEach(() => {
      propsData = {
        components: [
          createComponent({
            componentNumber: 1,
            additionalStates: [
              {
                pluginId: 'test-plugin-id',
                type: 'openapi-specs',
                openApiSpecs: [
                  {
                    url: 'https://example.com/openapi-spec-1-1',
                    description: 'Test OpenAPI Spec Description 1 1',
                  },
                ],
              },
            ],
          }),
        ],
      }
    })

    test('renders the OpenAPI spec with a URL and includes badge saying `missing`', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expect(wrapper.findAll('table tbody tr')).toHaveLength(1)
    })
  })

  describe('when an OpenAPI spec has a file but no spec', () => {
    beforeEach(() => {
      propsData = {
        components: [
          createComponent({
            componentNumber: 1,
            additionalStates: [
              {
                pluginId: 'test-plugin-id',
                type: 'openapi-specs',
                openApiSpecs: [
                  {
                    file: '/openapi-spec-1-1.yaml',
                    description: 'Test OpenAPI Spec Description 1 1',
                  },
                ],
              },
            ],
          }),
        ],
      }
    })

    test('renders the OpenAPI spec with a file and includes badge saying `missing`', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.description p', [
        'Test OpenAPI Spec Description 1 1',
      ])
    })
  })
})
