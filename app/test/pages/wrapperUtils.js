export function expectTextsInItems(wrapper, itemSelector, texts) {
  expect(
    wrapper
      .findAll(itemSelector)
      .wrappers.map((itemWrapper) => itemWrapper.text())
  ).toEqual(texts)
}
