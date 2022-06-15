/* eslint-disable no-use-before-define */

import { GraphEdge, GraphNode } from '~/types/kronicle-service'

export interface NodeLabel {
  x: number
  y: number
}

export type DependencyRelationType =
  | 'all'
  | 'other'
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
  d: string
  scopeRelated: boolean
  dependencyRelationType: DependencyRelationType
  dependency: GraphEdge
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
  node: GraphNode
  dependencies: Dependency[]
}

export interface Network {
  nodes: Node[]
  nodeGroups: Map<DependencyRelationType, Node[]>
  dependencies: Dependency[]
  dependencyGroups: Map<DependencyRelationType, Dependency[]>
}
