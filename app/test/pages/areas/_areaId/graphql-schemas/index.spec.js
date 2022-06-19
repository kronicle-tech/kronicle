import Index from '@/pages/areas/_areaId/graphql-schemas/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createArea,
  createComponent,
  createComponentWithGraphQlSchemas,
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
        '/v1/areas/test-area-id-1?stateType=graphql-schemas&fields=area(id,name,components(id,name,typeId,tags,teams,platformId,states))':
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
        'Kronicle - Test Area Name 1 - GraphQL Schemas'
      )
    })

    describe('when the area has no components', () => {
      test('renders no GraphQL Schemas', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the area has components but the components have no GraphQL Schemas', () => {
      beforeEach(() => {
        area.components = [
          createComponent({ componentNumber: 1 }),
          createComponent({ componentNumber: 2 }),
          createComponent({ componentNumber: 3 }),
        ]
      })

      test('renders no GraphQL Schemas', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the area has components and the components have GraphQL Schemas', () => {
      beforeEach(() => {
        area.components = [
          createComponentWithGraphQlSchemas({ componentNumber: 1 }),
          createComponent({ componentNumber: 2 }),
          createComponentWithGraphQlSchemas({ componentNumber: 3 }),
        ]
      })

      test('renders the details of the GraphQL Schemas', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
