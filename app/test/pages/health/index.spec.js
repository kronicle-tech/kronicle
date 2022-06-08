import { createLocalVue } from '@vue/test-utils'
import VueMeta from 'vue-meta'
import Index from '~/pages/health'
import { createPageWrapper } from '~/test/pages/pageUtils'

const localVue = createLocalVue()
localVue.use(VueMeta, { keyName: 'head' })

describe('Index', () => {
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      localVue,
      hasAsyncData: false,
    })
  }

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  test('has the right page title', async () => {
    await createWrapper()
    expect(wrapper.vm.$metaInfo.title).toBe('Kronicle - Health')
  })

  test('renders an empty page', async () => {
    await createWrapper()
    expect(wrapper.element).toMatchSnapshot()
  })
})
