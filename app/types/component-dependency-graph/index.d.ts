import {
  SummaryComponentDependency,
  SummaryComponentDependencyNode,
  SummarySubComponentDependencyNode,
} from '~/types/kronicle-service'

export interface Node {
  index: number
  text: Array<string>
  depth: number
  row: number
  column: number
  x: number
  y: number
  label: NodeLabel
  dependencyType: DependencyType
  node: SummaryComponentDependencyNode | SummarySubComponentDependencyNode
  dependencies: Dependency[]
}

export interface NodeLabel {
  x: number
  y: number
}

export interface Dependency {
  index: number
  sourceNode: Node
  targetNode: Node
  relatedNodes: Node[]
  manual: boolean
  d: string
  scopeRelated: boolean
  dependencyType: DependencyType
  dependency: SummaryComponentDependency
}

export type DependencyType =
  | 'all'
  | 'manual'
  | 'scope-related'
  | 'scoped'
  | 'related'
  | 'direct'
  | 'selected'

export interface Network {
  nodes: Node[]
  nodeGroups: Map<DependencyType, Node[]>
  dependencies: Dependency[]
  dependencyGroups: Map<DependencyType, Dependency[]>
}
