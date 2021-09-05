import { createLocalVue, mount } from '@vue/test-utils'
import VueMeta from 'vue-meta'
import flushPromises from 'flush-promises'
import Index from '~/pages/components/_componentId/openapi-specs/_openApiSpecIndex/index.vue'

const localVue = createLocalVue()
localVue.use(VueMeta, { keyName: 'head' })

describe('Index', () => {
  const config = {
    serviceBaseUrl: 'https://example.com/service',
  }
  const route = {
    params: {
      componentId: 'test-component-id-1',
      openApiSpecIndex: '1',
    },
  }
  let component
  let wrapper

  async function createWrapper() {
    wrapper = await mount(Index, {
      localVue,
      mocks: {
        $config: config,
        $route: route,
      },
    })
  }

  beforeEach(() => {
    global.fetch = jest.fn((url) => {
      if (
        url ===
        'https://example.com/service/v1/components/test-component-id-1?fields=component(openApiSpecs)'
      ) {
        return Promise.resolve({
          json: () => Promise.resolve({ component }),
        })
      } else {
        throw new Error(`Unexpected url "${url}"`)
      }
    })
    window.Redoc = { init: jest.fn() }
  })

  afterEach(() => {
    global.fetch = undefined
    window.Redoc = undefined
    wrapper.destroy()
    wrapper = null
  })

  describe('when Get Component service endpoint returns a component with OpenAPI specs', () => {
    beforeEach(() => {
      component = {
        openApiSpecs: [
          {
            scannerId: 'test-scanner-1',
            url: 'https://example.com/test-1',
            description: 'Text Description 1',
            spec: {
              testField: 'test-value-1',
            },
          },
          {
            scannerId: 'test-scanner-2',
            url: 'https://example.com/test-2',
            description: 'Text Description 2',
            spec: {
              testField: 'test-value-2',
            },
          },
        ],
      }
    })

    test('has the right page title', async () => {
      await createWrapper()
      expect(wrapper.vm.$metaInfo.title).toBe(
        'Kronicle - Component test-component-id-1 - OpenAPI Spec 1'
      )
    })

    test('renders the page with the details of the correct OpenAPI spec', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })

    test('calls Redoc.init()', async () => {
      await createWrapper()
      await flushPromises()
      expect(window.Redoc.init).toBeCalledTimes(1)
      expect(window.Redoc.init.mock.calls[0][0]).toEqual(
        component.openApiSpecs[0].spec
      )
    })
  })
})
