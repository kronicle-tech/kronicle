<template>
  <svg
    v-if="diagram"
    xmlns="http://www.w3.org/2000/svg"
    overflow="auto"
    width="100%"
    height="1200"
  >
    <rect
      class="component-dependency-graph-background"
      fill-opacity="0"
      @click="backgroundClick()"
      @touchend.passive="backgroundClick()"
    ></rect>
    <defs>
      <marker
        v-for="group in groups"
        :id="`component-dependency-graph-${group}-edge-marker`"
        :key="group"
        :class="`edge-marker ${group}-edge-marker`"
        orient="auto-start-reverse"
        viewBox="0 0 7.1 11.5"
        markerWidth="7.1"
        markerHeight="11.5"
        markerUnits="userSpaceOnUse"
        :refX="nodeSize - 2"
        refY="5.75"
      >
        <path d="M1 11.5L0 10.4L5.1 5.7L0 1L1 0L7.1 5.7L1 11.5"></path>
      </marker>
    </defs>

    <g v-for="group in groups" :key="`edge-${group}`">
      <path
        v-for="edge in network.edgeGroups.get(group)"
        :id="`component-dependency-graph-edge-${edge.index}`"
        :key="edge.index"
        :class="`edge ${group}-edge`"
        :d="edge.d"
        :marker-end="`url(#component-dependency-graph-${group}-edge-marker)`"
      />
    </g>

    <g v-for="group in groups" :key="`${group}-node`">
      <circle
        v-for="node in network.nodeGroups.get(group)"
        :id="`component-dependency-graph-node-${node.index}`"
        :key="node.index"
        :class="`node ${group}-node`"
        :r="nodeSize / 2"
        :cx="node.x"
        :cy="node.y"
        :title="node.text.join(', ')"
        @click="selectedNodeChange(node)"
        @touchend.passive="selectedNodeChange(node)"
        @mouseover="hoverNodeChange(node)"
        @mouseout="hoverNodeChange(undefined)"
      />
    </g>

    <g v-for="group in groups" :key="`${group}-node-label`">
      <text
        v-for="node in network.nodeGroups.get(group)"
        :key="node.index"
        :class="`node-label ${group}-node-label`"
        :x="node.label.x"
        :y="node.label.y"
        :font-size="fontSize"
        :stroke-width="fontSize / 8"
      >
        <tspan
          v-for="(text, textIndex) in node.text"
          :key="textIndex"
          :x="node.label.x"
          :y="node.label.y + textIndex * fontSize * 1.2"
        >
          {{ text }}
        </tspan>
      </text>
    </g>
  </svg>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import {
  Diagram,
  GraphEdge,
  GraphNode,
  GraphState,
} from '~/types/kronicle-service'
import {
  Edge,
  EdgeRelationType,
  Network,
  Node,
} from '~/types/component-dependency-graph'

export default Vue.extend({
  props: {
    diagram: {
      type: Object as PropType<Diagram>,
      default: undefined,
    },
    edgeTypes: {
      type: Array as PropType<string[]>,
      default: () => [] as string[],
    },
    edgeRelationType: {
      type: String as PropType<
        'all' | 'scope-related' | 'scoped' | 'related' | 'direct'
      >,
      default: 'all',
    },
    zoom: {
      type: Number,
      default: 100,
    },
    selectedComponentId: {
      type: String,
      default: undefined,
    },
    scopedComponentIds: {
      type: Array as PropType<String[]>,
      default: undefined,
    },
    fixedScope: {
      type: Boolean,
      default: false,
    },
    scopeRelatedRadius: {
      type: Number,
      default: 1,
    },
    nodeSpacingX: {
      type: Number,
      default: 300,
    },
    nodeSpacingY: {
      type: Number,
      default: 100,
    },
    paddingX: {
      type: Number,
      default: 50,
    },
    paddingY: {
      type: Number,
      default: 50,
    },
  },
  data() {
    return {
      nodeSize: 20,
      fontSize: 14,
      labelOffset: 20,
      groups: [
        'all',
        'other',
        'scope-related',
        'scoped',
        'related',
        'direct',
        'selected',
      ],
      selectedNodeIndex: undefined as number | undefined,
      hoverNodeIndex: undefined as number | undefined,
    }
  },
  computed: {
    effectiveFixedScope(): boolean {
      return (
        this.scopedComponentIds &&
        this.scopedComponentIds.length > 0 &&
        this.fixedScope &&
        !['related', 'direct'].includes(this.edgeRelationType)
      )
    },
    graph(): GraphState | undefined {
      if (!this.diagram || this.diagram.states.length === 0) {
        return undefined
      }

      return this.diagram.states.find((state) => state.type === 'graph') as
        | GraphState
        | undefined
    },
    network(): Network {
      const that = this

      const network = {
        nodes: [],
        nodeGroups: new Map<EdgeRelationType, Node[]>(),
        edges: [],
        edgeGroups: new Map<EdgeRelationType, Edge[]>(),
      } as Network

      if (!that.graph) {
        return network
      }

      const graph = that.graph

      addNodes()
      const effectiveSelectedNodeIndexes = getEffectiveSelectedNodeIndexes()
      const scopedNodeIndexes = getScopedNodeIndexes()
      addEdges()
      classifyEdges()
      filterNodesAndEdges()
      const reverseEdgeMap = createReverseEdgeMap()
      const depths = assignNodesToDepths()
      removeUndefinedEntriesInDepths()
      optimizeNodePlacements()
      setNodePositions()
      setEdgePositions()
      splitNodes()
      splitEdges()
      emitNetworkChange()
      return network

      function addNodes() {
        graph.nodes.forEach((node, nodeIndex) => {
          addNode(node, nodeIndex)
        })
      }

      function getEffectiveSelectedNodeIndexes(): number[] {
        if (that.hoverNodeIndex !== undefined) {
          return [that.hoverNodeIndex]
        }

        if (that.selectedNodeIndex !== undefined) {
          return [that.selectedNodeIndex]
        }

        if (that.selectedComponentId) {
          const nodeIndexes = network.nodes
            .filter(
              (node) => node.node.componentId === that.selectedComponentId
            )
            .map((node) => node.index)

          if (nodeIndexes.length > 0) {
            return nodeIndexes
          }
        }

        return []
      }

      function getScopedNodeIndexes() {
        if (that.scopedComponentIds) {
          const scopedNodeIndexes = network.nodes
            .filter((node) =>
              that.scopedComponentIds.includes(node.node.componentId)
            )
            .map((node) => node.index)

          return scopedNodeIndexes
        }

        return undefined
      }

      function addNode(edgeNode: GraphNode, index: number) {
        const node = {
          index,
          text: getNodeText(edgeNode),
          row: 0,
          column: 0,
          x: 0,
          y: 0,
          label: {
            x: 0,
            y: 0,
          },
          edgeRelationType: 'all',
          node: edgeNode,
          edges: [] as Edge[],
        } as Node
        network.nodes.push(node)
      }

      function getNodeText(node: GraphNode) {
        const text = [node.componentId]
        if ('name' in node) {
          text.push(node.name)
          node.tags.forEach((tag) => text.push(` ${tag.key}=${tag.value}`))
        }
        const maxLineLength = 30
        return text.map((line) =>
          line.length > maxLineLength
            ? line.substr(0, maxLineLength) + '…'
            : line
        )
      }

      function addEdges() {
        graph.edges.forEach((edge, edgeIndex) => {
          if (edge.sourceIndex !== undefined && filterEdgeType(edge)) {
            addEdge(edge, edgeIndex)
          }
        })
      }

      function filterEdgeType(componentEdge: GraphEdge) {
        return (
          that.edgeTypes === undefined ||
          that.edgeTypes.length === 0 ||
          that.edgeTypes.includes(componentEdge.type)
        )
      }

      function addEdge(componentEdge: GraphEdge, index: number) {
        const edge = {
          index,
          sourceNode: network.nodes[componentEdge.sourceIndex],
          targetNode: network.nodes[componentEdge.targetIndex],
          relatedNodes: getRelatedNodes(componentEdge.relatedIndexes),
          d: '',
          scopeRelated: false,
          edgeRelationType: 'all',
          edge: componentEdge,
        } as Edge
        edge.sourceNode.edges.push(edge)
        edge.targetNode.edges.push(edge)
        network.edges.push(edge)
      }

      function classifyEdges() {
        network.edges.forEach((edge) => {
          edge.scopeRelated = edgeIsScopeRelatedEdge(
            edge,
            that.scopeRelatedRadius
          )
          edge.edgeRelationType = getEdgeRelationTypeForEdge(edge)
        })
      }

      function getEdgeRelationTypeForEdge(edge: Edge): EdgeRelationType {
        if (edgeIsDirectEdge(edge)) {
          return 'direct'
        } else if (edgeIsRelatedEdge(edge)) {
          return 'related'
        } else if (edgeIsScopedEdge(edge)) {
          return 'scoped'
        } else if (edge.scopeRelated) {
          return 'scope-related'
        } else {
          return 'all'
        }
      }

      function edgeIsDirectEdge(edge: Edge) {
        if (effectiveSelectedNodeIndexes.length > 0) {
          return (
            effectiveSelectedNodeIndexes.includes(edge.sourceNode.index) ||
            effectiveSelectedNodeIndexes.includes(edge.targetNode.index)
          )
        }

        return false
      }

      function edgeIsRelatedEdge(edge: Edge) {
        if (effectiveSelectedNodeIndexes.length > 0) {
          return edge.relatedNodes.some((node) =>
            effectiveSelectedNodeIndexes.includes(node.index)
          )
        }

        return false
      }

      function edgeIsScopedEdge(edge: Edge) {
        if (scopedNodeIndexes) {
          return (
            scopedNodeIndexes.includes(edge.sourceNode.index) &&
            scopedNodeIndexes.includes(edge.targetNode.index)
          )
        }

        return false
      }

      function edgeIsScopeRelatedEdge(
        edge: Edge,
        radius: number,
        visitedEdges = new Set() as Set<Edge>
      ): boolean {
        if (!scopedNodeIndexes) {
          return false
        }

        if (visitedEdges.size === 0) {
          visitedEdges.add(edge)

          if (
            scopedNodeIndexes.includes(edge.sourceNode.index) &&
            scopedNodeIndexes.includes(edge.targetNode.index)
          ) {
            return true
          }
        }

        if (radius === 0) {
          return false
        }

        if (
          scopedNodeIndexes.includes(edge.sourceNode.index) ||
          scopedNodeIndexes.includes(edge.targetNode.index)
        ) {
          return true
        }

        radius--

        const peerEdges = new Set() as Set<Edge>
        edge.sourceNode.edges
          .concat(edge.targetNode.edges)
          .forEach((peerEdge) => peerEdges.add(peerEdge))

        return Array.from(peerEdges).some((peerEdge) => {
          if (!visitedEdges.has(peerEdge)) {
            visitedEdges.add(peerEdge)
            return edgeIsScopeRelatedEdge(peerEdge, radius, visitedEdges)
          } else {
            return false
          }
        })
      }

      function getAcceptableEdgeRelationTypes() {
        const edgeRelationTypes = [
          'direct',
          'related',
          'scoped',
          'scope-related',
          'all',
        ] as EdgeRelationType[]
        const acceptableEdgeRelationTypes = [] as EdgeRelationType[]
        let selectedEdgeRelationType = that.edgeRelationType

        if (
          effectiveSelectedNodeIndexes.length === 0 &&
          ['direct', 'related'].includes(selectedEdgeRelationType)
        ) {
          selectedEdgeRelationType = 'scope-related'
        }

        if (
          !that.scopedComponentIds &&
          ['scoped', 'scope-related'].includes(selectedEdgeRelationType)
        ) {
          selectedEdgeRelationType = 'all'
        }

        edgeRelationTypes.some((edgeRelationType) => {
          acceptableEdgeRelationTypes.push(edgeRelationType)

          return edgeRelationType === selectedEdgeRelationType
        })

        return acceptableEdgeRelationTypes
      }

      function filterNodesAndEdges() {
        const acceptableEdgeRelationTypes = getAcceptableEdgeRelationTypes()

        const edgeToRemove = [] as Edge[]
        network.edges.forEach((edge) => {
          if (
            isFixedScopeAndEdgeIsNotScopeRelated(edge) ||
            edgeRelationTypeOfEdgeIsNotAcceptable(
              edge,
              acceptableEdgeRelationTypes
            )
          ) {
            edgeToRemove.push(edge)
            arrayRemove(edge.sourceNode.edges, edge)
            arrayRemove(edge.targetNode.edges, edge)
          }
        })

        const nodesToRemove = [] as Node[]
        network.nodes.forEach((node) => {
          if (effectiveSelectedNodeIndexes.includes(node.index)) {
            node.edgeRelationType = 'selected'
          } else if (node.edges.length === 0) {
            nodesToRemove.push(node)
          } else if (
            scopedNodeIndexes &&
            scopedNodeIndexes.includes(node.index)
          ) {
            node.edgeRelationType = 'scoped'
          } else {
            let edgeRelationType = 'other' as EdgeRelationType
            node.edges.some((edge) => {
              edgeRelationType = maxEdgeRelationType(
                edgeRelationType,
                edge.edgeRelationType
              )
              return edgeRelationType === 'direct'
            })
            node.edgeRelationType = edgeRelationType
          }
        })

        arrayRemoveIf(network.edges, (edge) => edgeToRemove.includes(edge))
        arrayRemoveIf(network.nodes, (node) => nodesToRemove.includes(node))
      }

      function isFixedScopeAndEdgeIsNotScopeRelated(edge: Edge) {
        return that.effectiveFixedScope && !edge.scopeRelated
      }

      function edgeRelationTypeOfEdgeIsNotAcceptable(
        edge: Edge,
        acceptableEdgeRelationTypes: EdgeRelationType[]
      ) {
        return !acceptableEdgeRelationTypes.includes(edge.edgeRelationType)
      }

      function maxEdgeRelationType(a: EdgeRelationType, b: EdgeRelationType) {
        if (a === 'direct' || b === 'direct') {
          return 'direct'
        } else if (a === 'related' || b === 'related') {
          return 'related'
        } else if (a === 'scoped' || b === 'scoped') {
          return 'scoped'
        } else if (a === 'scope-related' || b === 'scope-related') {
          return 'scope-related'
        } else if (a === 'all' || b === 'all') {
          return 'all'
        } else {
          return 'other'
        }
      }

      function createReverseEdgeMap() {
        const reverseEdgeMap = new Map<Number, Edge[]>()
        network.edges.forEach((edge) => {
          let reverseEdges = reverseEdgeMap.get(edge.targetNode.index)

          if (!reverseEdges) {
            reverseEdges = []
            reverseEdgeMap.set(edge.targetNode.index, reverseEdges)
          }

          reverseEdges.push(edge)
        })
        return reverseEdgeMap
      }

      function getRelatedNodes(relatedIndexes: number[]) {
        return relatedIndexes.map((index) => network.nodes[index])
      }

      function assignNodesToDepths() {
        const depths = [] as Node[][]

        network.nodes.forEach((node) => {
          node.depth = findNodeDepth(node.index)
          let depth = depths[node.depth]
          if (!depth) {
            depth = []
            depths[node.depth] = depth
          }
          depth.push(node)
        })

        return depths
      }

      function findNodeDepth(
        index: number,
        currentDepth = 0,
        visitedIndexes = [] as number[]
      ) {
        visitedIndexes.push(index)
        let maxDepth = currentDepth
        reverseEdgeMap.get(index)?.forEach((edge) => {
          const upstreamIndex = edge.sourceNode.index
          if (
            !visitedIndexes.includes(upstreamIndex) &&
            upstreamIndex !== index
          ) {
            const newVisitedIndexes = [...visitedIndexes]
            const newDepth = findNodeDepth(
              upstreamIndex,
              currentDepth + 1,
              newVisitedIndexes
            )
            maxDepth = Math.max(maxDepth, newDepth)
          }
        })
        return maxDepth
      }

      function removeUndefinedEntriesInDepths() {
        arrayRemoveIf(depths, (depth) => !depth)
      }

      function optimizeNodePlacements() {
        createNodePlacements()
        for (let i = 0; i < 100; i++) {
          const changed = adjustNodePlacements()

          if (!changed) {
            return
          }
        }
      }

      function createNodePlacements() {
        depths.forEach((depth, row) => {
          depth.forEach((node, column) => {
            node.row = row
            node.column = column
          })
        })
      }

      function adjustNodePlacements(): boolean {
        let changed = false

        depths.forEach((depth) => {
          depth.forEach((node, column) => {
            const spread = node ? calculateNodeSpread(node) : 0
            const nextNode = depth[column + 1]
            const nextSpread = nextNode ? calculateNodeSpread(nextNode) : 0

            if (spread > nextSpread) {
              moveNode(depth, node, column + 1)
              moveNode(depth, nextNode, column)
              changed = true
            }
          })
        })

        return changed
      }

      function moveNode(depth: Node[], node: Node, to: number) {
        if (node) {
          node.column = to
        }
        depth[to] = node
      }

      function calculateNodeSpread(node: Node) {
        let spread = 0
        node.edges.forEach((edge) => {
          const otherNode =
            edge.sourceNode === node ? edge.targetNode : edge.sourceNode

          spread += otherNode.column - node.column
        })
        return spread
      }

      function setNodePositions() {
        network.nodes.forEach((node) => {
          node.x = that.paddingX + node.column * that.nodeSpacingX
          node.y = that.paddingY + node.row * that.nodeSpacingY
          node.label = {
            x: node.x + that.nodeSize / 2 + that.fontSize / 2,
            y: node.y + that.labelOffset,
          }
        })
      }

      function setEdgePositions() {
        network.edges.forEach((edge) => {
          const sourceNode = edge.sourceNode
          const targetNode = edge.targetNode

          const sourceX = sourceNode?.x ?? 0
          const sourceY = sourceNode?.y ?? 0
          const targetX = targetNode?.x ?? 0
          const targetY = targetNode?.y ?? 0

          if (sourceX === targetX || sourceY === targetY) {
            edge.d = `M${sourceX},${sourceY}L${targetX},${targetY}`
          } else {
            edge.d = `M${sourceX},${sourceY}C${sourceX},${targetY} ${targetX},${sourceY} ${targetX},${targetY}`
          }
        })
      }

      function splitNodes() {
        addItemsToItemTypes(network.nodeGroups, network.nodes)
      }

      function splitEdges() {
        addItemsToItemTypes(network.edgeGroups, network.edges)
      }

      function addItemsToItemTypes<I extends Node | Edge>(
        itemTypes: Map<EdgeRelationType, I[]>,
        items: I[]
      ) {
        items.forEach((item) => addItemToItemTypes(itemTypes, item))
      }

      function addItemToItemTypes<I extends Node | Edge>(
        itemTypes: Map<EdgeRelationType, I[]>,
        item: I
      ) {
        let itemType = itemTypes.get(item.edgeRelationType)

        if (!itemType) {
          itemType = []
          itemTypes.set(item.edgeRelationType, itemType)
        }

        itemType.push(item)
      }

      function emitNetworkChange() {
        that.$emit('networkChange', network)
      }

      function arrayRemoveIf<T>(
        array: T[],
        callbackfn: (value: T, index: number, array: T[]) => unknown
      ) {
        let index = array.length - 1

        while (index >= 0) {
          if (callbackfn(array[index], index, array)) {
            array.splice(index, 1)
          }

          index--
        }
      }

      function arrayRemove<T>(array: T[], element: T) {
        const index = array.indexOf(element)

        if (index === -1) {
          return
        }

        array.splice(index, 1)
      }
    },
  },
  watch: {
    zoom() {
      this.resize()
    },
  },
  mounted() {
    this.resize()
  },
  updated() {
    this.resize()
  },
  methods: {
    resize() {
      if (process.client) {
        const svg = this.$el as SVGGraphicsElement

        if (!svg || typeof svg.querySelector !== 'function') {
          return
        }

        const background = svg.querySelector(
          '.component-dependency-graph-background'
        )

        if (!background) {
          return
        }

        background.setAttribute('width', '0')
        background.setAttribute('height', '0')
        const bBox = svg.getBBox()
        const zoomFactor = this.zoom / 100
        const width = bBox.width * zoomFactor + this.paddingX * 2
        const height = bBox.height * zoomFactor + this.paddingY * 2
        const viewBoxWidth = bBox.width + this.paddingX * 2
        const viewBoxHeight = bBox.height + this.paddingY * 2

        svg.setAttribute('width', width.toString())
        svg.setAttribute('height', height.toString())
        svg.setAttribute('viewBox', `0 0 ${viewBoxWidth} ${viewBoxHeight}`)
        background.setAttribute('width', viewBoxWidth.toString())
        background.setAttribute('height', viewBoxHeight.toString())
      }
    },
    getNodeByIndex(index: number | undefined) {
      if (index === undefined) {
        return undefined
      }

      return this.network.nodes.find((node) => node.index === index)
    },
    selectedNodeChange(node: Node) {
      this.selectedNodeIndex =
        this.selectedNodeIndex === node.index ? undefined : node.index
      this.$emit('nodeClick', {
        node: this.getNodeByIndex(this.selectedNodeIndex)?.node,
      })
    },
    backgroundClick() {
      if (this.selectedNodeIndex) {
        this.selectedNodeIndex = undefined
      }
    },
    hoverNodeChange(node: Node) {
      this.hoverNodeIndex = node ? node.index : undefined
    },
  },
})
</script>

<style scoped>
.edge {
  fill: none;
  stroke: #888;
  stroke-width: 2px;
}

.edge-marker {
  stroke: #888;
  stroke-width: 2px;
}

.node {
  fill: #ccc;
  stroke: #fff;
  stroke-width: 2px;
}

.node-label {
  fill: #fff;
}

.other-edge,
.other-edge-marker {
  stroke: #e74c3c;
}

.other-node,
.other-node-label {
  fill: #e74c3c;
}

.scoped-edge,
.scoped-edge-marker {
  stroke: #3498db;
}

.scoped-node,
.scoped-node-label {
  fill: #3498db;
}

.related-edge,
.related-edge-marker {
  stroke: #3498db;
}

.related-node,
.related-node-label {
  fill: #3498db;
}

.direct-edge,
.direct-edge-marker {
  stroke: #f39c12;
}

.direct-node,
.direct-node-label {
  fill: #f39c12;
}

.selected-node,
.selected-node-label {
  fill: #00bc8c;
}

.selected-node-label {
  font-weight: bold;
}

.related-edge,
.direct-edge {
  stroke-width: 3px;
}
</style>
