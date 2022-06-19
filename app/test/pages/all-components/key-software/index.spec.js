import Index from '~/pages/all-components/key-software/index.vue'
import { createPageWrapper } from '~/test/pages/pageUtils'
import { createComponent } from '~/test/testDataUtils'

describe('Index', () => {
  let components = []
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      serviceRequests: {
        '/v1/components?stateType=key-softwares&fields=components(id,name,typeId,tags,teams,platformId,states)':
          {
            responseBody: { components },
          },
      },
    })
  }

  beforeEach(() => {
    components = []
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  test('has the right page title', async () => {
    await createWrapper()
    expect(wrapper.vm.$metaInfo.title).toBe('Kronicle - Key Software')
  })

  describe('when Get Components service endpoint returns an empty array', () => {
    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })

  describe('when Get Components service endpoint returns an array of multiple components with no key software', () => {
    beforeEach(() => {
      components = [
        createComponent({ componentNumber: 1 }),
        createComponent({ componentNumber: 2 }),
        createComponent({ componentNumber: 3 }),
      ]
    })

    test('renders a table row for each component', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })

  describe('when Get Components service endpoint returns an array of multiple components with key software', () => {
    beforeEach(() => {
      components = [
        createComponent({
          componentNumber: 1,
          additionalFields: {
            keySoftware: [
              {
                name: 'test-key-software-1',
                versions: ['test-version-1'],
              },
            ],
          },
        }),
        createComponent({
          componentNumber: 2,
          additionalFields: {
            keySoftware: [
              {
                name: 'test-key-software-2',
                versions: ['test-version-2'],
              },
            ],
          },
        }),
        createComponent({
          componentNumber: 3,
          additionalFields: {
            keySoftware: [
              {
                name: 'test-key-software-3',
                versions: ['test-version-3'],
              },
            ],
          },
        }),
      ]
    })

    test('renders each component with each key software', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })

  describe('when Get Components service endpoint returns an array of multiple components with the same key software names for each component', () => {
    beforeEach(() => {
      components = [
        createComponent({
          componentNumber: 1,
          additionalFields: {
            keySoftware: [
              {
                name: 'test-key-software-1',
                versions: ['test-version-1'],
              },
              {
                name: 'test-key-software-2',
                versions: ['test-version-2'],
              },
            ],
          },
        }),
        createComponent({
          componentNumber: 2,
          additionalFields: {
            keySoftware: [
              {
                name: 'test-key-software-1',
                versions: ['test-version-3'],
              },
              {
                name: 'test-key-software-2',
                versions: ['test-version-4'],
              },
            ],
          },
        }),
        createComponent({
          componentNumber: 3,
          additionalFields: {
            keySoftware: [
              {
                name: 'test-key-software-1',
                versions: ['test-version-5'],
              },
              {
                name: 'test-key-software-2',
                versions: ['test-version-6'],
              },
            ],
          },
        }),
      ]
    })

    test('renders only one table column per key software name', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })

  describe('when a key software has multiple versions', () => {
    beforeEach(() => {
      components = [
        createComponent({
          componentNumber: 1,
          additionalFields: {
            keySoftware: [
              {
                name: 'test-key-software-1',
                versions: ['test-version-1', 'test-version-2'],
              },
            ],
          },
        }),
      ]
    })

    test('renders all versions', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })
})
