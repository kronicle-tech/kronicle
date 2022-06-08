import { mount } from '@vue/test-utils'

// See https://github.com/testing-library/vue-testing-library/issues/92
export async function mountWithAsyncData(
  component,
  {
    mountFunction,
    hasAsyncData,
    asyncDataGlobal,
    config,
    route,
    store,
    ...options
  } = {}
) {
  if (!mountFunction) {
    mountFunction = mount
  }
  const asyncData = component.options.asyncData
  if (hasAsyncData && typeof asyncData !== 'function') {
    throw new TypeError('asyncData should be a function')
  }
  let data
  if (hasAsyncData) {
    const originalGlobal = {}
    for (const key of Object.keys(asyncDataGlobal)) {
      originalGlobal[key] = global[key]
      global[key] = asyncDataGlobal[key]
    }
    data = await asyncData({ $config: config, route, store })
    for (const key of Object.keys(asyncDataGlobal)) {
      global[key] = originalGlobal[key]
    }
  } else {
    data = {}
  }
  const wrapper = mountFunction(component, {
    ...options,
    store,
    mocks: { $route: route, $config: config },
    data: () => data,
  })
  return wrapper
}
