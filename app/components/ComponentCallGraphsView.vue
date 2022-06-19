<template>
  <div>
    <b-alert show dismissible variant="info" class="my-3">
      <p>
        The call graphs on this page come from a combination of Zipkin traces.
        The visualisation is not out-of-the-box Zipkin functionality and is
        bespoke to Kronicle.
      </p>

      <p class="mb-0">
        A maximum of {{ maxCallGraphCount }} call graphs are displayed on this
        page at any one time.
      </p>
    </b-alert>

    <div v-if="filteredDiagrams.length === 0">
      <b-alert show variant="warning" class="my-3">
        No call graphs are available for this component. Possibly:

        <ol>
          <li>The component is not integrated with Zipkin yet or</li>
          <li>
            Kronicle may have restarted recently and not have loaded Zipkin data
            yet
          </li>
        </ol>
      </b-alert>
    </div>

    <div class="call-graphs">
      <div
        v-for="(diagram, diagramIndex) in filteredDiagrams"
        :key="diagramIndex"
        class="call-graph my-2"
      >
        <h5>
          Call Graph {{ diagramIndex + 1 }}
          <b-badge variant="primary" class="ml-2">
            {{ getDiagramGraph(diagram).sampleSize }} Traces
          </b-badge>
        </h5>

        <ComponentDependencyGraph
          :diagram="diagram"
          :selected-component-id="component.id"
          :node-spacing-x="400"
          :node-spacing-y="200"
        />
      </div>
    </div>

    <div class="nodes">
      <h4>Component Nodes</h4>

      <b-button
        v-for="(node, nodeIndex) in extendedNodes"
        :key="nodeIndex"
        block
        :variant="node.selected ? 'success' : 'secondary'"
        class="node my-1"
        @click="nodeClick(nodeIndex)"
      >
        {{ node.name }}
        <ul v-if="node.tags.length > 0">
          <li v-for="tag in node.tags" :key="tag.key">
            {{ tag.key }}: {{ tag.value }}
          </li>
        </ul>
      </b-button>
    </div>
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BAlert, BBadge, BButton } from 'bootstrap-vue'
import {
  Component,
  Diagram,
  GraphNode,
  GraphState,
} from '~/types/kronicle-service'
import ComponentDependencyGraph from '~/components/ComponentDependencyGraph.vue'

interface ExtendedNode extends GraphNode {
  selected: boolean
}

export default Vue.extend({
  components: {
    'b-alert': BAlert,
    'b-badge': BBadge,
    'b-button': BButton,
    ComponentDependencyGraph,
  },
  props: {
    component: {
      type: Object as PropType<Component>,
      required: true,
    },
    diagrams: {
      type: Array as PropType<Diagram[]>,
      required: true,
    },
  },
  data() {
    return {
      selectedNodeIndex: 0,
      maxCallGraphCount: 20,
    }
  },
  computed: {
    graphs(): GraphState[] {
      const that = this
      return that.diagrams
        .map((diagram) => that.getDiagramGraph(diagram))
        .filter((graph) => graph !== undefined)
        .map((graph) => graph as GraphState)
    },
    nodes(): GraphNode[] {
      const that = this
      const nodes = that.graphs
        .flatMap((graph) => graph.nodes)
        .filter((node) => node.componentId === that.component.id)
      return this.uniqueNodes(nodes)
    },
    extendedNodes(): ExtendedNode[] {
      const that = this
      return that.nodes.map(
        (node, nodeIndex) =>
          ({
            ...node,
            selected: nodeIndex === that.selectedNodeIndex,
          } as ExtendedNode)
      )
    },
    filteredDiagrams(): Diagram[] {
      const that = this
      if (that.diagrams.length === 0) {
        return []
      }
      const selectedNode = that.nodes[that.selectedNodeIndex]
      return that.diagrams
        .filter((diagram) => {
          const graph = that.getDiagramGraph(diagram)
          if (!graph) {
            return false
          }
          return graph.nodes.some((node) => this.nodeEquals(node, selectedNode))
        })
        .sort(
          (a, b) =>
            (that.getDiagramGraph(a)?.sampleSize ?? 0) -
            (that.getDiagramGraph(b)?.sampleSize ?? 0)
        )
        .slice(0, that.maxCallGraphCount)
    },
  },
  methods: {
    getDiagramGraph(diagram: Diagram): GraphState | undefined {
      return diagram.states.find((state) => state.type === 'graph') as
        | GraphState
        | undefined
    },
    nodeEquals(a: GraphNode, b: GraphNode): boolean {
      if (a.componentId !== b.componentId) {
        return false
      }
      if (a.name !== b.name) {
        return false
      }
      if (a.tags.length !== b.tags.length) {
        return false
      }
      for (let tagIndex = 0; tagIndex < a.tags.length; tagIndex++) {
        const tagA = a.tags[tagIndex]
        const tagB = b.tags[tagIndex]
        if (tagA.key !== tagB.key || tagA.value !== tagB.value) {
          return false
        }
      }
      return true
    },
    uniqueNodes(values: GraphNode[]): GraphNode[] {
      const uniqueValues: GraphNode[] = []
      for (const value of values) {
        let isUnique = true
        for (const uniqueValue of uniqueValues) {
          if (this.nodeEquals(value, uniqueValue)) {
            isUnique = false
            break
          }
        }

        if (isUnique) {
          uniqueValues.push(value)
        }
      }
      return uniqueValues
    },
    nodeClick(nodeIndex: number): void {
      this.selectedNodeIndex = nodeIndex
    },
  },
})
</script>

<style scoped>
.call-graphs {
  float: left;
  width: calc(100% - 320px);
}

.call-graph {
  overflow-x: scroll;
  scrollbar-color: #444 #111;
}

.nodes {
  float: right;
  width: 300px;
}

.nodes button {
  text-align: left;
}
</style>
