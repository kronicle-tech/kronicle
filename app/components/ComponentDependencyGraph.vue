<template>
  <svg
    v-if="dependencies"
    xmlns="http://www.w3.org/2000/svg"
    overflow="auto"
    width="100%"
    height="1200"
  >
    <rect
      class="component-dependency-graph-background"
      fill-opacity="0"
      @click="backgroundClick($event)"
      @touchend.passive="backgroundClick($event)"
    ></rect>
    <defs>
      <marker
        v-for="group in groups"
        :id="`component-dependency-graph-dependency-marker-${group}`"
        :key="group"
        :class="`dependency-marker ${group}-dependency-marker`"
        orient="auto-start-reverse"
        viewBox="0 0 7.1 11.5"
        markerWidth="7.1"
        markerHeight="11.5"
        markerUnits="userSpaceOnUse"
        :refX="nodeSize"
        refY="5.75"
      >
        <path d="M1 11.5L0 10.4L5.1 5.7L0 1L1 0L7.1 5.7L1 11.5"></path>
      </marker>
    </defs>

    <g v-for="group in groups" :key="`dependency-${group}`">
      <path
        v-for="dependency in network.dependencyGroups.get(group)"
        :id="`component-dependency-graph-dependency-${dependency.index}`"
        :key="dependency.index"
        :class="`dependency ${group}-dependency`"
        :d="dependency.d"
        :marker-end="`url(#component-dependencies-${group}-dependency-marker)`"
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
        @click="selectedNodeChange($event, node)"
        @touchend.passive="selectedNodeChange($event, node)"
        @mouseover="mouseOverNodeChange($event, node)"
        @mouseout="mouseOverNodeChange($event, undefined)"
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

<style scoped>
.dependency {
  fill: none;
  stroke: #888;
  stroke-width: 2px;
}

.dependency-marker {
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

.manual-dependency,
.manual-dependency-marker {
  stroke: #e74c3c;
}

.manual-node,
.manual-node-label {
  fill: #e74c3c;
}

.scoped-dependency,
.scoped-dependency-marker {
  stroke: #3498db;
}

.scoped-node,
.scoped-node-label {
  fill: #3498db;
}

.related-dependency,
.related-dependency-marker {
  stroke: #3498db;
}

.related-node,
.related-node-label {
  fill: #3498db;
}

.direct-dependency,
.direct-dependency-marker {
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

.related-dependency,
.direct-dependency {
  stroke-width: 3px;
}
</style>

<script lang="ts">
import Vue, { PropType } from 'vue'
import {
  SummaryComponentDependencies,
  SummaryComponentDependency,
  SummaryComponentDependencyNode,
  SummarySubComponentDependencies,
  SummarySubComponentDependencyNode,
} from '~/types/kronicle-service'
import {
  Dependency,
  DependencyRelationType,
  Network,
  Node,
} from '~/types/component-dependency-graph'

interface SummaryComponentDependencyWithMandatorySourceIndex
  extends SummaryComponentDependency {
  sourceIndex: number
}

export default Vue.extend({
  props: {
    dependencies: {
      type: Object as PropType<
        SummaryComponentDependencies | SummarySubComponentDependencies
      >,
      default: undefined,
    },
    dependencyTypeIds: {
      type: Array as PropType<string[]>,
      default: [] as string[],
    },
    dependencyRelationType: {
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
      nodeSize: 10,
      fontSize: 14,
      labelOffset: 20,
      groups: [
        'all',
        'manual',
        'scope-related',
        'scoped',
        'related',
        'direct',
        'selected',
      ],
      selectedNodeIndex: undefined as number | undefined,
      mouseOverNodeIndex: undefined as number | undefined,
    }
  },
  computed: {
    effectiveFixedScope(): boolean {
      return (
        this.scopedComponentIds &&
        this.scopedComponentIds.length > 0 &&
        this.fixedScope &&
        !['related', 'direct'].includes(this.dependencyRelationType)
      )
    },
    network(): Network {
      const that = this
      const network = {
        nodes: [],
        nodeGroups: new Map<DependencyRelationType, Node[]>(),
        dependencies: [],
        dependencyGroups: new Map<DependencyRelationType, Dependency[]>(),
      } as Network

      addNodes()
      const effectiveSelectedNodeIndexes = getEffectiveSelectedNodeIndexes()
      const scopedNodeIndexes = getScopedNodeIndexes()
      addDependencies()
      classifyDependencies()
      filterNodesAndDependencies()
      const reverseDependencyMap = createReverseDependencyMap()
      const depths = assignNodesToDepths()
      removeUndefinedEntriesInDepths()
      optimizeNodePlacements()
      setNodePositions()
      setDependencyPositions()
      splitNodes()
      splitDependencies()
      emitNetworkChange()
      return network

      function addNodes() {
        that.dependencies.nodes.forEach((node, nodeIndex) => {
          addNode(node, nodeIndex)
        })
      }

      function getEffectiveSelectedNodeIndexes(): number[] {
        if (that.mouseOverNodeIndex !== undefined) {
          return [that.mouseOverNodeIndex]
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

      function addNode(
        dependencyNode:
          | SummaryComponentDependencyNode
          | SummarySubComponentDependencyNode,
        index: number
      ) {
        const node = {
          index,
          text: getNodeText(dependencyNode),
          row: 0,
          column: 0,
          x: 0,
          y: 0,
          label: {
            x: 0,
            y: 0,
          },
          dependencyRelationType: 'all',
          node: dependencyNode,
          dependencies: [] as Dependency[],
        } as Node
        network.nodes.push(node)
      }

      function getNodeText(
        node: SummaryComponentDependencyNode | SummarySubComponentDependencyNode
      ) {
        const text = [node.componentId]
        if ('spanName' in node) {
          text.push(node.spanName)
          Object.keys(node.tags).forEach((key) =>
            text.push(` ${key}=${node.tags[key]}`)
          )
        }
        const maxLineLength = 30;
        return text.map(line => line.length > maxLineLength ? line.substr(0, maxLineLength) + '…' : line)
      }

      function addDependencies() {
        that.dependencies.dependencies.forEach(
          (dependency, dependencyIndex) => {
            if (dependency.sourceIndex !== undefined && filterDependencyType(dependency)) {
              addDependency(
                dependency as SummaryComponentDependencyWithMandatorySourceIndex,
                dependencyIndex
              )
            }
          }
        )
      }

      function filterDependencyType(
        componentDependency: SummaryComponentDependencyWithMandatorySourceIndex
      ) {
        return that.dependencyTypeIds === undefined ||
          that.dependencyTypeIds.length === 0 ||
          that.dependencyTypeIds.includes(componentDependency.typeId)
      }

      function addDependency(
        componentDependency: SummaryComponentDependencyWithMandatorySourceIndex,
        index: number
      ) {
        const dependency = {
          index,
          sourceNode: network.nodes[componentDependency.sourceIndex],
          targetNode: network.nodes[componentDependency.targetIndex],
          relatedNodes: getRelatedNodes(componentDependency.relatedIndexes),
          manual: componentDependency.manual,
          d: '',
          scopeRelated: false,
          dependencyRelationType: 'all',
          dependency: componentDependency,
        } as Dependency
        dependency.sourceNode.dependencies.push(dependency)
        dependency.targetNode.dependencies.push(dependency)
        network.dependencies.push(dependency)
      }

      function classifyDependencies() {
        network.dependencies.forEach((dependency) => {
          dependency.scopeRelated = dependencyIsScopeRelatedDependency(
            dependency,
            that.scopeRelatedRadius
          )
          dependency.dependencyRelationType = getDependencyRelationTypeForDependency(dependency)
        })
      }

      function getDependencyRelationTypeForDependency(
        dependency: Dependency
      ): DependencyRelationType {
        if (dependencyIsDirectDependency(dependency)) {
          return 'direct'
        } else if (dependencyIsRelatedDependency(dependency)) {
          return 'related'
        } else if (dependencyIsScopedDependency(dependency)) {
          return 'scoped'
        } else if (dependency.scopeRelated) {
          return 'scope-related'
        } else if (dependency.manual) {
          return 'manual'
        } else {
          return 'all'
        }
      }

      function dependencyIsDirectDependency(dependency: Dependency) {
        if (effectiveSelectedNodeIndexes.length > 0) {
          return (
            effectiveSelectedNodeIndexes.includes(
              dependency.sourceNode.index
            ) ||
            effectiveSelectedNodeIndexes.includes(dependency.targetNode.index)
          )
        }

        return false
      }

      function dependencyIsRelatedDependency(dependency: Dependency) {
        if (effectiveSelectedNodeIndexes.length > 0) {
          return dependency.relatedNodes.some((node) =>
            effectiveSelectedNodeIndexes.includes(node.index)
          )
        }

        return false
      }

      function dependencyIsScopedDependency(dependency: Dependency) {
        if (scopedNodeIndexes) {
          return (
            scopedNodeIndexes.includes(dependency.sourceNode.index) &&
            scopedNodeIndexes.includes(dependency.targetNode.index)
          )
        }

        return false
      }

      function dependencyIsScopeRelatedDependency(
        dependency: Dependency,
        radius: number,
        visitedDependencies = new Set() as Set<Dependency>
      ): boolean {
        if (!scopedNodeIndexes) {
          return false
        }

        if (visitedDependencies.size === 0) {
          visitedDependencies.add(dependency)

          if (
            scopedNodeIndexes.includes(dependency.sourceNode.index) &&
            scopedNodeIndexes.includes(dependency.targetNode.index)
          ) {
            return true
          }
        }

        if (radius === 0) {
          return false
        }

        if (
          scopedNodeIndexes.includes(dependency.sourceNode.index) ||
          scopedNodeIndexes.includes(dependency.targetNode.index)
        ) {
          return true
        }

        radius--

        const peerDependencies = new Set() as Set<Dependency>
        dependency.sourceNode.dependencies
          .concat(dependency.targetNode.dependencies)
          .forEach((peerDependency) => peerDependencies.add(peerDependency))

        return Array.from(peerDependencies).some((peerDependency) => {
          if (!visitedDependencies.has(peerDependency)) {
            visitedDependencies.add(peerDependency)
            return dependencyIsScopeRelatedDependency(
              peerDependency,
              radius,
              visitedDependencies
            )
          }
        })
      }

      function getAcceptableDependencyRelationTypes() {
        const dependencyRelationTypes = [
          'direct',
          'related',
          'scoped',
          'scope-related',
          'manual',
          'all',
        ] as DependencyRelationType[]
        const acceptableDependencyRelationTypes = [] as DependencyRelationType[]
        let selectedDependencyRelationType = that.dependencyRelationType

        if (
          effectiveSelectedNodeIndexes.length === 0 &&
          ['direct', 'related'].includes(selectedDependencyRelationType)
        ) {
          selectedDependencyRelationType = 'scope-related'
        }

        if (
          !that.scopedComponentIds &&
          ['scoped', 'scope-related'].includes(selectedDependencyRelationType)
        ) {
          selectedDependencyRelationType = 'all'
        }

        dependencyRelationTypes.some((dependencyRelationType) => {
          acceptableDependencyRelationTypes.push(dependencyRelationType)

          if (dependencyRelationType === selectedDependencyRelationType) {
            return true
          }
        })

        return acceptableDependencyRelationTypes
      }

      function filterNodesAndDependencies() {
        const acceptableDependencyRelationTypes = getAcceptableDependencyRelationTypes()

        const dependencyToRemove = [] as Dependency[]
        network.dependencies.forEach((dependency) => {
          if (
            isFixedScopeAndDependencyIsNotScopeRelated(dependency) ||
            dependencyRelationTypeOfDependencyIsNotAcceptable(
              dependency,
              acceptableDependencyRelationTypes
            )
          ) {
            dependencyToRemove.push(dependency)
            arrayRemove(dependency.sourceNode.dependencies, dependency)
            arrayRemove(dependency.targetNode.dependencies, dependency)
          }
        })

        const nodesToRemove = [] as Node[]
        network.nodes.forEach((node) => {
          if (effectiveSelectedNodeIndexes.includes(node.index)) {
            node.dependencyRelationType = 'selected'
          } else if (node.dependencies.length === 0) {
            nodesToRemove.push(node)
          } else if (
            scopedNodeIndexes &&
            scopedNodeIndexes.includes(node.index)
          ) {
            node.dependencyRelationType = 'scoped'
          } else {
            let dependencyRelationType = 'manual' as DependencyRelationType
            node.dependencies.some((dependency) => {
              dependencyRelationType = maxDependencyRelationType(
                dependencyRelationType,
                dependency.dependencyRelationType
              )
              return dependencyRelationType === 'direct'
            })
            node.dependencyRelationType = dependencyRelationType
          }
        })

        arrayRemoveIf(network.dependencies, (dependency) =>
          dependencyToRemove.includes(dependency)
        )
        arrayRemoveIf(network.nodes, (node) => nodesToRemove.includes(node))
      }

      function isFixedScopeAndDependencyIsNotScopeRelated(
        dependency: Dependency
      ) {
        return that.effectiveFixedScope && !dependency.scopeRelated
      }

      function dependencyRelationTypeOfDependencyIsNotAcceptable(
        dependency: Dependency,
        acceptableDependencyRelationTypes: DependencyRelationType[]
      ) {
        return !acceptableDependencyRelationTypes.includes(dependency.dependencyRelationType)
      }

      function maxDependencyRelationType(a: DependencyRelationType, b: DependencyRelationType) {
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
          return 'manual'
        }
      }

      function createReverseDependencyMap() {
        const reverseDependencyMap = new Map<Number, Dependency[]>()
        network.dependencies.forEach((dependency) => {
          let reverseDependencies = reverseDependencyMap.get(
            dependency.targetNode.index
          )

          if (!reverseDependencies) {
            reverseDependencies = []
            reverseDependencyMap.set(
              dependency.targetNode.index,
              reverseDependencies
            )
          }

          reverseDependencies.push(dependency)
        })
        return reverseDependencyMap
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
        reverseDependencyMap.get(index)?.forEach((dependency) => {
          const upstreamIndex = dependency.sourceNode.index
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
        node.dependencies.forEach((dependency) => {
          const otherNode =
            dependency.sourceNode === node
              ? dependency.targetNode
              : dependency.sourceNode

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

      function setDependencyPositions() {
        network.dependencies.forEach((dependency) => {
          const sourceNode = dependency.sourceNode
          const targetNode = dependency.targetNode

          const sourceX = sourceNode?.x ?? 0
          const sourceY = sourceNode?.y ?? 0
          const targetX = targetNode?.x ?? 0
          const targetY = targetNode?.y ?? 0

          if (sourceX === targetX || sourceY === targetY) {
            dependency.d = `M${sourceX},${sourceY}L${targetX},${targetY}`
          } else {
            dependency.d = `M${sourceX},${sourceY}C${sourceX},${targetY} ${targetX},${sourceY} ${targetX},${targetY}`
          }
        })
      }

      function splitNodes() {
        addItemsToItemTypes(network.nodeGroups, network.nodes)
      }

      function splitDependencies() {
        addItemsToItemTypes(network.dependencyGroups, network.dependencies)
      }

      function addItemsToItemTypes<I extends Node | Dependency>(
        itemTypes: Map<DependencyRelationType, I[]>,
        items: I[]
      ) {
        items.forEach((item) => addItemToItemTypes(itemTypes, item))
      }

      function addItemToItemTypes<I extends Node | Dependency>(
        itemTypes: Map<DependencyRelationType, I[]>,
        item: I
      ) {
        let itemType = itemTypes.get(item.dependencyRelationType)

        if (!itemType) {
          itemType = []
          itemTypes.set(item.dependencyRelationType, itemType)
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
    selectedNodeChange(event: Event, node: Node) {
      this.selectedNodeIndex =
        this.selectedNodeIndex === node.index ? undefined : node.index
      this.$emit(
        'selectedNodeChange',
        event,
        this.getNodeByIndex(this.selectedNodeIndex)?.node
      )
    },
    backgroundClick(event: Event) {
      if (this.selectedNodeIndex) {
        this.selectedNodeIndex = undefined
        this.$emit('selectedNodeChange', event, undefined)
      }
    },
    mouseOverNodeChange(event: Event, node: Node) {
      this.mouseOverNodeIndex = node ? node.index : undefined
      this.$emit(
        'selectedNodeChange',
        event,
        this.getNodeByIndex(this.mouseOverNodeIndex)?.node ||
          this.getNodeByIndex(this.selectedNodeIndex)?.node
      )
    },
  },
})
</script>
