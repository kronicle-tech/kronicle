/* eslint-disable no-use-before-define */

import { Component, GraphEdge, GraphNode } from '~/types/kronicle-service'

export interface NodeLabel {
  x: number
  y: number
}

export type EdgeRelationType =
  | 'all'
  | 'other'
  | 'scope-related'
  | 'scoped'
  | 'related'
  | 'direct'
  | 'selected'

export interface Edge {
  index: number
  sourceNode: Node
  targetNode: Node
  relatedNodes: Node[]
  d: string
  scopeRelated: boolean
  edgeRelationType: EdgeRelationType
  edge: GraphEdge
}

export interface Node {
  index: number
  text: Array<string>
  iconPath?: string
  depth: number
  row: number
  column: number
  x: number
  y: number
  label: NodeLabel
  edgeRelationType: EdgeRelationType
  node: GraphNode
  edges: Edge[]
  component?: Component
}

export interface Network {
  nodes: Node[]
  nodeGroups: Map<EdgeRelationType, Node[]>
  edges: Edge[]
  edgeGroups: Map<EdgeRelationType, Edge[]>
}
