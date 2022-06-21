<template>
  <div>
    <h1>{{ diagram.name }}</h1>

    <Markdown :markdown="diagram.description" />

    <ComponentFilters
      :components="components"
      :environment-id-filter-enabled="true"
      :plugin-id-filter-enabled="true"
    >
      <b-card v-if="edgeTypeOptions.length > 0" bg-variant="secondary">
        <b-form-group label="Connection Types">
          <b-form-checkbox-group
            v-model="selectedEdgeTypes"
            :options="edgeTypeOptions"
            name="connectionType"
            stacked
          ></b-form-checkbox-group>
        </b-form-group>
      </b-card>

      <b-card bg-variant="secondary">
        <b-form-group
          label-cols="6"
          label-size="sm"
          label="Radius:"
          label-for="graph-scope-related-radius"
        >
          <b-form-select
            id="graph-scope-related-radius"
            v-model="selectedScopeRelatedRadius"
            :options="scopeRelatedRadiusOptions"
            size="sm"
          />
        </b-form-group>
      </b-card>
    </ComponentFilters>

    <b-card-group columns style="column-count: 3">
      <b-card bg-variant="secondary">
        <b-form-group
          label-cols="6"
          label-size="sm"
          label="Zoom:"
          label-for="graph-zoom"
        >
          <b-form-select
            id="graph-zoom"
            v-model="zoom"
            :options="zoomOptions"
            size="sm"
          />
        </b-form-group>
      </b-card>
    </b-card-group>

    <div class="graph">
      <b-alert show="60" dismissible variant="info">
        Click a dot in the diagram to see more information about that component
      </b-alert>

      <ComponentDependencyGraph
        id="component-dependency-graph"
        :diagram="diagram"
        :edge-types="selectedEdgeTypes"
        edge-relation-type="scope-related"
        :zoom="zoom"
        :selected-component-id="selectedComponentId"
        :scoped-component-ids="filteredComponentIds"
        :fixed-scope="true"
        :scope-related-radius="selectedScopeRelatedRadius"
        @networkChange="networkChange"
        @nodeClick="nodeClick"
      />
    </div>

    <b-sidebar
      id="component"
      v-model="componentSidebarVisible"
      right
      width="800px"
      bg-variant="dark"
      text-variant="light"
      backdrop-variant="dark"
      backdrop
    >
      <div class="m-3">
        <ComponentPanel
          :component="component"
          :component-id="componentId"
          :diagram="diagram"
        />
      </div>
    </b-sidebar>
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import {
  BAlert,
  BCard,
  BCardGroup,
  BFormCheckboxGroup,
  BFormGroup,
  BFormSelect,
  BSidebar,
} from 'bootstrap-vue'
import {
  Component,
  Diagram,
  GraphEdge,
  GraphNode,
  GraphState,
} from '~/types/kronicle-service'
import { Network } from '~/types/component-dependency-graph'
import { intRange } from '~/src/arrayUtils'
import ComponentDependencyGraph from '~/components/DiagramGraph.vue'
import ComponentPanel from '~/components/ComponentPanel.vue'
import ComponentFilters from '~/components/ComponentFilters.vue'
import Markdown from '~/components/Markdown.vue'

interface Option {
  value: string | undefined
  text: string
}

interface Connection {
  readonly source: GraphNode
  readonly target: GraphNode
  readonly edge: GraphEdge
}

export default Vue.extend({
  components: {
    'b-alert': BAlert,
    'b-card': BCard,
    'b-card-group': BCardGroup,
    'b-form-checkbox-group': BFormCheckboxGroup,
    'b-form-group': BFormGroup,
    'b-form-select': BFormSelect,
    'b-sidebar': BSidebar,
    ComponentDependencyGraph,
    ComponentFilters,
    ComponentPanel,
    Markdown,
  },
  props: {
    components: {
      type: Array as PropType<Component[]>,
      required: true,
    },
    diagram: {
      type: Object as PropType<Diagram>,
      required: true,
    },
    allComponents: {
      type: Array as PropType<Component[]>,
      required: true,
    },
    selectedComponentId: {
      type: String,
      default: undefined,
    },
    scopeRelatedRadius: {
      type: Number,
      default: 10,
    },
  },
  data() {
    return {
      componentSidebarVisible: false as boolean,
      node: undefined as GraphNode | undefined,
      component: undefined as Component | undefined,
      componentId: undefined as string | undefined,
      selectedEdgeTypes: [] as string[],
      selectedScopeRelatedRadius: this.scopeRelatedRadius,
      zoom: 100,
      network: undefined as Network | undefined,
    }
  },
  computed: {
    filteredComponentIds(): string[] {
      return this.$store.state.componentFilters.filteredComponentIds
    },
    connections(): Connection[] {
      return this.diagram.states
        .filter((state) => state.type === 'graph')
        .map((state) => state as GraphState)
        .flatMap((state) =>
          state.edges.map((edge) => ({
            source: state.nodes[edge.sourceIndex] as GraphNode,
            target: state.nodes[edge.sourceIndex] as GraphNode,
            edge,
          }))
        )
    },
    edgeTypeOptions(): Option[] {
      return [
        ...new Set(
          this.connections
            .map((connection) => connection.edge.type)
            .filter((edgeType) => !!edgeType)
        ),
      ].map((edgeType) => ({
        value: edgeType ?? '',
        text: edgeType ?? 'none',
      }))
    },
    scopeRelatedRadiusOptions(): Option[] {
      return intRange(0, 11).map((value) => ({
        value: value.toString(),
        text: value.toString(),
      }))
    },
    zoomOptions(): Option[] {
      const zoomOptions = [25, 50, 75, 100, 125, 150, 200, 400]
      return zoomOptions.map((zoomOption) => ({
        value: zoomOption.toString(),
        text: `${zoomOption}%`,
      }))
    },
  },
  methods: {
    findComponent(id: string): Component | undefined {
      return this.allComponents.find((component) => component.id === id)
    },
    networkChange(network: Network): void {
      this.network = network
    },
    nodeClick({ node }: { node: GraphNode | undefined }): void {
      if (node) {
        this.componentSidebarVisible = true
        this.node = node
        this.component = this.findComponent(node.componentId)
        this.componentId = node.componentId
      } else {
        this.componentSidebarVisible = false
        this.node = undefined
        this.component = undefined
        this.componentId = undefined
      }
    },
  },
})
</script>

<style scoped>
.graph {
  overflow-x: scroll;
  height: 1000px;
  height: calc(100vh - 300px);
  scrollbar-color: #444 #111;
}

.form-group {
  margin-bottom: 0;
}
</style>
