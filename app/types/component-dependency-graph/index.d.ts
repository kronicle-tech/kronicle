/* eslint-disable no-use-before-define */

import {
  SummaryComponentDependency,
  SummaryComponentDependencyNode,
  SummarySubComponentDependencyNode,
} from '~/types/kronicle-service'

export interface NodeLabel {
  x: number
  y: number
}

export type DependencyRelationType =
  | 'all'
  | 'manual'
  | 'scope-related'
  | 'scoped'
  | 'related'
  | 'direct'
  | 'selected'

export interface Dependency {
  index: number
  sourceNode: Node
  targetNode: Node
  relatedNodes: Node[]
  manual: boolean
  d: string
  scopeRelated: boolean
  dependencyRelationType: DependencyRelationType
  dependency: SummaryComponentDependency
}

export interface Node {
  index: number
  text: Array<string>
  depth: number
  row: number
  column: number
  x: number
  y: number
  label: NodeLabel
  dependencyRelationType: DependencyRelationType
  node: SummaryComponentDependencyNode | SummarySubComponentDependencyNode
  dependencies: Dependency[]
}

export interface Network {
  nodes: Node[]
  nodeGroups: Map<DependencyRelationType, Node[]>
  dependencies: Dependency[]
  dependencyGroups: Map<DependencyRelationType, Dependency[]>
}
