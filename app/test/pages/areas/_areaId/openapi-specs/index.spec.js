import Index from '@/pages/areas/_areaId/openapi-specs/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createArea,
  createComponent,
  createComponentWithOpenApiSpecs,
} from '~/test/testDataUtils'

describe('Index', () => {
  const route = {
    params: {
      areaId: 'test-area-id-1',
    },
  }
  let area
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      route,
      serviceRequests: {
        '/v1/areas/test-area-id-1?stateType=openapi-specs&fields=area(id,name,components(id,name,typeId,tags,teams,platformId,states(environmentId,pluginId)))':
          {
            responseBody: { area },
          },
      },
    })
  }

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when Get Area service endpoint returns an area', () => {
    beforeEach(() => {
      area = createArea({ areaNumber: 1 })
    })

    test('has the right page title', async () => {
      await createWrapper()
      expect(wrapper.vm.$metaInfo.title).toBe(
        'Kronicle - Test Area Name 1 - OpenAPI Specs'
      )
    })

    describe('when the area has no components', () => {
      test('renders no OpenAPI specs', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the area has components but the components have no OpenAPI specs', () => {
      beforeEach(() => {
        area.components = [
          createComponent({ componentNumber: 1 }),
          createComponent({ componentNumber: 2 }),
          createComponent({ componentNumber: 3 }),
        ]
      })

      test('renders no OpenAPI specs', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the area has components and the components have OpenAPI specs', () => {
      beforeEach(() => {
        area.components = [
          createComponentWithOpenApiSpecs({ componentNumber: 1 }),
          createComponent({ componentNumber: 2 }),
          createComponentWithOpenApiSpecs({ componentNumber: 3 }),
        ]
      })

      test('renders the details of the OpenAPI specs', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
