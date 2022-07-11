import Index from '@/pages/areas/_areaId/tests/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import {
  createArea,
  createComponent,
  createComponentWithTestResults,
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
        '/v1/areas/test-area-id-1?testOutcome=fail&fields=area(id,name,components(id,name,type,tags,teams,platformId,testResults))':
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
        'Kronicle - Test Area Name 1 - Tests'
      )
    })

    describe('when the area has no components', () => {
      test('renders no test results', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the area has components but the components have no test results', () => {
      beforeEach(() => {
        area.components = [
          createComponent({ componentNumber: 1 }),
          createComponent({ componentNumber: 2 }),
          createComponent({ componentNumber: 3 }),
        ]
      })

      test('renders no test results', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })

    describe('when the area has components and the components have test results', () => {
      beforeEach(() => {
        area.components = [
          createComponentWithTestResults({
            componentNumber: 1,
          }),
          createComponent({ componentNumber: 2 }),
          createComponentWithTestResults({
            componentNumber: 3,
          }),
        ]
      })

      test('renders the details of the test results', async () => {
        await createWrapper()
        expect(wrapper.element).toMatchSnapshot()
      })
    })
  })
})
