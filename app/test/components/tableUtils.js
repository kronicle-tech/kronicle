export function expectTextsInTableRows(wrapper, selector, texts) {
  expect(
    getRows(wrapper).wrappers.map((rowWrapper) =>
      rowWrapper.find(selector).text()
    )
  ).toEqual(texts)
}

export function expectClassesInTableRows(wrapper, selector, classes) {
  expect(
    getRows(wrapper).wrappers.map(
      (rowWrapper) => rowWrapper.find(selector).attributes().class
    )
  ).toEqual(classes)
}

export function expectHrefsInTableRows(wrapper, selector, classes) {
  expect(
    getRows(wrapper).wrappers.map(
      (rowWrapper) => rowWrapper.find(selector).attributes().href
    )
  ).toEqual(classes)
}

export function expectSubTextsInTableRows(wrapper, selector, subTexts) {
  getRows(wrapper)
    .wrappers.map((rowWrapper) => rowWrapper.find(selector).text())
    .forEach((text, rowIndex) =>
      expect(text).toEqual(expect.stringContaining(subTexts[rowIndex]))
    )
}

function getRows(wrapper) {
  return wrapper.findAll('table tbody tr')
}
