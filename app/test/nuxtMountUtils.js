import { mount } from '@vue/test-utils'

// See https://github.com/testing-library/vue-testing-library/issues/92
export async function mountWithAsyncData(
  component,
  { mountFunction, asyncDataGlobal, config, route, store, ...options } = {}
) {
  if (!mountFunction) {
    mountFunction = mount
  }
  const asyncData = component.options.asyncData
  if (typeof asyncData !== 'function') {
    throw new TypeError('asyncData should be a function')
  }
  const originalGlobal = {}
  for (const key of Object.keys(asyncDataGlobal)) {
    originalGlobal[key] = global[key]
    global[key] = asyncDataGlobal[key]
  }
  const data = await asyncData({ $config: config, route, store })
  for (const key of Object.keys(asyncDataGlobal)) {
    global[key] = originalGlobal[key]
  }
  const wrapper = mountFunction(component, {
    ...options,
    store,
    mocks: { $route: route },
    data: () => data,
  })
  return wrapper
}
