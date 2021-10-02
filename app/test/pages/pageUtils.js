import Vuex from 'vuex'
import VueMeta from 'vue-meta'
import { createLocalVue } from '@vue/test-utils'
import { mountWithAsyncData } from '~/test/nuxtMountUtils'

const localVue = createLocalVue()
localVue.use(Vuex)
localVue.use(VueMeta, { keyName: 'head' })

const defaultConfig = {
  serviceBaseUrl: 'https://example.com/service',
}

export async function createPageWrapper(page, { serviceRequests, route, config }) {
  const Store = await import('~/.nuxt/store.js')
  const store = Store.createStore()
  const fetch = createFetch(serviceRequests)

  if (!route) {
    route = {}
  }

  if (!route.query) {
    route.query = {}
  }

  return await mountWithAsyncData(page, {
    localVue,
    store,
    asyncDataGlobal: {
      fetch,
    },
    config: {
      ...defaultConfig,
      ...config,
    },
    route,
  })
}

function createFetch(serviceRequests) {
  return jest.fn((url) => {
    if (!url.startsWith(defaultConfig.serviceBaseUrl)) {
      throw new Error(
        `Unexpected url "${url}" to start with "${defaultConfig.serviceBaseUrl}"`
      )
    }

    const urlPath = url.slice(defaultConfig.serviceBaseUrl.length)
    const serviceRequest = serviceRequests[urlPath]

    if (!serviceRequest) {
      throw new Error(`Unexpected urlPath "${urlPath}"`)
    }

    return Promise.resolve({
      json: () => Promise.resolve(serviceRequest.responseBody),
    })
  })
}
