import { createLocalVue } from '@vue/test-utils'
import VueMeta from 'vue-meta'
import flushPromises from 'flush-promises'
import Index from '~/pages/components/_componentId/openapi-specs/_openApiSpecIndex/content'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {createComponentResponseWithStateTypes} from "~/test/testDataUtils";

const localVue = createLocalVue()
localVue.use(VueMeta, { keyName: 'head' })

describe('Index', () => {
  const route = {
    params: {
      componentId: 'test-component-id-1',
      openApiSpecIndex: '1',
    },
  }
  let component
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      localVue,
      route,
      serviceRequests: {
        '/v1/components/test-component-id-1?fields=component(id,name,states(type))': createComponentResponseWithStateTypes(1),
        '/v1/components/test-component-id-1?stateType=openapi-specs&fields=component(id,name,teams,states)':
          {
            responseBody: { component },
          },
      },
    })
    await flushPromises()
  }

  beforeEach(() => {
    window.Redoc = { init: jest.fn() }
  })

  afterEach(() => {
    window.Redoc = undefined
    wrapper.destroy()
    wrapper = null
  })

  describe('when Get Component service endpoint returns a component with OpenAPI specs', () => {
    beforeEach(() => {
      component = {
        states: [
          {
            pluginId: 'test-plugin-id',
            type: 'openapi-specs',
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
            ]
          }
        ],
      }
    })

    test('has the right page title', async () => {
      await createWrapper()
      expect(wrapper.vm.$metaInfo.title).toBe(
        'Kronicle - test-component-id-1 - OpenAPI Spec 1'
      )
    })

    test('renders the page with the details of the correct OpenAPI spec', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })

    test('calls Redoc.init()', async () => {
      await createWrapper()
      expect(window.Redoc.init).toBeCalledTimes(1)
      expect(window.Redoc.init.mock.calls[0][0]).toEqual(
        component.states[0].openApiSpecs[0].spec
      )
    })
  })
})
