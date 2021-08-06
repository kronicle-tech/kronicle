import Vuex from 'vuex'
import { createLocalVue, mount } from '@vue/test-utils'

const localVue = createLocalVue()
localVue.use(Vuex)

export async function createComponentWrapper(
  component,
  { propsData, storeCallback }
) {
  const Store = await import('~/.nuxt/store.js')
  const store = Store.createStore()

  if (storeCallback) {
    storeCallback(store)
  }

  return mount(component, {
    localVue,
    store,
    propsData,
  })
}
