<template>
  <div>
    <b-alert show dismissible variant="info" class="my-3">
      <p>
        The call graphs on this page come from a combination of Zipkin traces.
        The visualisation is not out-of-the-box Zipkin functionality and is
        bespoke to the Component Catalog.
      </p>

      <p class="mb-0">
        A maximum of {{ maxCallGraphCount }} call graphs are displayed on this
        page at any one time.
      </p>
    </b-alert>

    <div v-if="filteredCallGraphs.length === 0">
      <b-alert show variant="warning" class="my-3">
        No call graphs are available for this component. Possibly:

        <ol>
          <li>The component is not integrated with Zipkin yet or</li>
          <li>
            The Component Catalog may have restarted recently and not have
            loaded Zipkin data yet
          </li>
        </ol>
      </b-alert>
    </div>

    <div class="call-graphs">
      <div
        v-for="(callGraph, callGraphIndex) in filteredCallGraphs"
        :key="callGraphIndex"
        class="call-graph my-2"
      >
        <h5>
          Call Graph {{ callGraphIndex + 1 }}
          <b-badge variant="primary" class="ml-2">
            {{ callGraph.traceCount }} Traces
          </b-badge>
        </h5>

        <ComponentDependencyGraph
          :dependencies="callGraph"
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
        {{ node.spanName }}
        <ul v-if="Object.keys(node.tags).length > 0">
          <li v-for="(tagValue, tagKey) in node.tags" :key="tagKey">
            {{ tagKey }}: {{ tagValue }}
          </li>
        </ul>
      </b-button>
    </div>
  </div>
</template>

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

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BAlert, BBadge, BButton } from 'bootstrap-vue'
import {
  Component,
  SummaryCallGraph,
  SummarySubComponentDependencyNode,
} from '~/types/component-catalog-service'
import ComponentDependencyGraph from '~/components/ComponentDependencyGraph.vue'

interface ExtendedNode extends SummarySubComponentDependencyNode {
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
    nodes: {
      type: Array as PropType<SummarySubComponentDependencyNode[]>,
      required: true,
    },
    callGraphs: {
      type: Array as PropType<SummaryCallGraph[]>,
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
    filteredCallGraphs(): SummaryCallGraph[] {
      const that = this
      if (that.nodes.length === 0) {
        return []
      }
      const selectedNode = that.nodes[that.selectedNodeIndex]
      return this.callGraphs
        .filter((callGraph) =>
          callGraph.nodes.some((node) => that.nodeEquals(node, selectedNode))
        )
        .sort((a, b) => b.traceCount - a.traceCount)
        .slice(0, that.maxCallGraphCount)
    },
  },
  methods: {
    nodeEquals(
      a: SummarySubComponentDependencyNode,
      b: SummarySubComponentDependencyNode
    ): boolean {
      if (a.componentId !== b.componentId) {
        return false
      }

      if (a.spanName !== b.spanName) {
        return false
      }

      if (Object.keys(a.tags).length !== Object.keys(b.tags).length) {
        return false
      }

      for (const tagKey in a.tags) {
        if (a.tags[tagKey] !== b.tags[tagKey]) {
          return false
        }
      }

      return true
    },
    nodeClick(nodeIndex: number): void {
      this.selectedNodeIndex = nodeIndex
    },
  },
})
</script>
