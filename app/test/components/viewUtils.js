import { createComponentWrapper } from '~/test/components/componentUtils'

export async function createViewComponentWrapper(viewComponent, { propsData }) {
  return await createComponentWrapper(viewComponent, {
    propsData,
    storeCallback: (store) => {
      const route = {
        query: {},
      }
      store.commit('componentFilters/initialize', {
        components: propsData.components,
        route,
      })
    },
  })
}

export function expectViewCount(wrapper, selector, variant, sizeClass, text) {
  const item = wrapper.find(selector)
  expect(item.classes()).toContain(`list-group-item-${variant}`)
  expect(item.find('span').classes()).toContain(sizeClass)
  expect(item.text()).toEqual(expect.stringMatching(text))
}
