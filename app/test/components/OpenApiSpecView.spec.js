import { createLocalVue, mount } from '@vue/test-utils'
import VueMeta from 'vue-meta'
import flushPromises from 'flush-promises'
import OpenApiSpecView from '~/components/OpenApiSpecView'

const localVue = createLocalVue()
localVue.use(VueMeta, { keyName: 'head' })

describe('OpenApiSpecView', () => {
  let propsData
  let wrapper

  async function createWrapper() {
    wrapper = await mount(OpenApiSpecView, {
      localVue,
      propsData
    })
  }

  beforeEach(() => {
    propsData = {
      component: {
        openApiSpecs: [
          {
            scannerId: 'test-scanner-1',
            url: 'https://example.com/test-1',
            description: 'Text Description 1',
            spec: {
              testField: 'test-value-1',
            },
          },
        ],
      },
      openApiSpecIndex: 1,
    }
    window.Redoc = { init: jest.fn() }
  })

  afterEach(() => {
    window.Redoc = undefined
    wrapper.destroy()
    wrapper = null
  })

  test('includes the script tag for Redoc', async () => {
    await createWrapper()
    expect(wrapper.vm.$metaInfo.script).toStrictEqual([
      {
        src: 'https://cdn.jsdelivr.net/npm/redoc@2.0.0-rc.45/bundles/redoc.standalone.js',
      }
    ])
  })

  test('renders the details of the correct OpenAPI spec', async () => {
    await createWrapper()
    expect(wrapper.element).toMatchSnapshot()
  })

  test('calls Redoc.init()', async () => {
    await createWrapper()
    await flushPromises()
    expect(window.Redoc.init).toBeCalledTimes(1)
    expect(window.Redoc.init.mock.calls[0][0]).toEqual(
      propsData.component.openApiSpecs[0].spec
    )
  })
})
