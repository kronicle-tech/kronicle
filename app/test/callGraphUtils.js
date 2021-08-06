function getCallGraphs(wrapper) {
  return wrapper.findAll('.call-graphs .call-graph')
}

function getNodes(wrapper) {
  return wrapper.findAll('.nodes .node')
}

export function expectCallGraphCount(wrapper, count) {
  expect(getCallGraphs(wrapper).length).toEqual(count)
}

export function expectNodeCount(wrapper, count) {
  expect(getNodes(wrapper).length).toEqual(count)
}

export function expectNodeVariants(wrapper, nodeVariants) {
  getNodes(wrapper).wrappers.forEach((nodeWrapper, nodeIndex) =>
    expect(nodeWrapper.classes()).toContain(`btn-${nodeVariants[nodeIndex]}`)
  )
}
