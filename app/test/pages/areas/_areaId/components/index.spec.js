import Index from '~/pages/areas/_areaId/components/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import { createArea, createComponent } from '~/test/testDataUtils'

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
        '/v1/areas/test-area-id-1?fields=area(id,name,components(id,name,typeId,description,tags,teams,platformId))':
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
        'Kronicle - Test Area Name 1 Area - Components'
      )
    })

    describe('when the area has no components', () => {
      test('renders no components', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the area has components', () => {
      beforeEach(() => {
        area.components = [
          createComponent({ componentNumber: 1 }),
          createComponent({ componentNumber: 2 }),
          createComponent({ componentNumber: 3 }),
        ]
      })

      test('renders the components', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
