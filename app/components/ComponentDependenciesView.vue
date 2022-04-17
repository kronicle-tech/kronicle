<template>
  <div>
    <b-alert show dismissible variant="info">
      Click a dot in the dependencies diagram to see more information about that component
    </b-alert>

    <ComponentFilters
      :components="components"
      :environment-id-filter-enabled="true"
      :plugin-id-filter-enabled="true"
    >
      <b-card bg-variant="secondary">
        <b-form-group
          label="Dependency Types"
        >
          <b-form-checkbox-group
            v-model="selectedDependencyTypeIds"
            :options="dependencyTypeIdOptions"
            name="dependencyTypeId"
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

      <b-card bg-variant="secondary">
        <b-form-checkbox
          id="detailed-dependencies"
          v-model="detailed"
          :value="true"
          :unchecked-value="false"
        >
          Detailed dependencies
        </b-form-checkbox>
      </b-card>
    </ComponentFilters>

    <div class="graph">
      <ComponentDependencyGraph
        id="component-dependency-graph"
        :dependencies="dependencies"
        :dependency-type-ids="selectedDependencyTypeIds"
        dependency-relation-type="scope-related"
        :zoom="zoom"
        :selected-component-id="selectedComponentId"
        :scoped-component-ids="filteredComponentIds"
        :fixed-scope="true"
        :scope-related-radius="parseInt(selectedScopeRelatedRadius, 10)"
        @networkChange="networkChange"
        @nodeClick="nodeClick"
      />
    </div>

    <b-sidebar
      id="component"
      v-model="componentSidebarVisible"
      right
      width="600px"
      bg-variant="dark"
      text-variant="light"
      backdrop-variant="dark"
      backdrop
    >
      <div class="m-3">
        <ComponentPanel :component="component" />
      </div>
    </b-sidebar>
  </div>
</template>

<style scoped>
.graph {
  overflow-x: scroll;
  height: 1000px;
  height: calc(100vh - 200px);
  scrollbar-color: #444 #111;
}
</style>

<script lang="ts">
import Vue, { PropType } from 'vue'
import {
  BAlert,
  BCard,
  BFormCheckbox,
  BFormCheckboxGroup,
  BFormGroup,
  BFormSelect,
  BSidebar
} from 'bootstrap-vue'
import {
  Component,
  SummaryComponentDependencies,
  SummaryComponentDependencyNode,
  SummarySubComponentDependencies,
  SummarySubComponentDependencyNode,
} from '~/types/kronicle-service'
import { Network } from '~/types/component-dependency-graph'
import { intRange } from '~/src/arrayUtils'
import ComponentDependencyGraph from '~/components/ComponentDependencyGraph.vue'
import ComponentPanel from '~/components/ComponentPanel.vue'
import ComponentFilters from '~/components/ComponentFilters.vue'

interface Option {
  value: string | undefined
  text: string
}

export default Vue.extend({
  components: {
    'b-alert': BAlert,
    'b-card': BCard,
    'b-form-checkbox': BFormCheckbox,
    'b-form-checkbox-group': BFormCheckboxGroup,
    'b-form-group': BFormGroup,
    'b-form-select': BFormSelect,
    'b-sidebar': BSidebar,
    ComponentDependencyGraph,
    ComponentFilters,
    ComponentPanel,
  },
  props: {
    components: {
      type: Array as PropType<Component[]>,
      required: true,
    },
    componentDependencies: {
      type: Object as PropType<SummaryComponentDependencies>,
      required: true,
    },
    subComponentDependencies: {
      type: Object as PropType<SummarySubComponentDependencies>,
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
      default: 1,
    },
  },
  data() {
    return {
      componentSidebarVisible: false as boolean,
      node: undefined as
        | SummaryComponentDependencyNode
        | SummarySubComponentDependencyNode
        | undefined,
      component: undefined as Component | undefined,
      selectedDependencyTypeIds: [] as string[],
      selectedScopeRelatedRadius: this.scopeRelatedRadius,
      zoom: 100,
      detailed: false,
      network: undefined as Network | undefined,
    }
  },
  computed: {
    filteredComponentIds(): Component[] {
      return this.$store.state.componentFilters.filteredComponentIds
    },
    dependencies():
      | SummaryComponentDependencies
      | SummarySubComponentDependencies {
      return this.detailed
        ? this.subComponentDependencies
        : this.componentDependencies
    },
    dependencyTypeIdOptions(): Option[] {
      return [...new Set(this.dependencies.dependencies.map(dependency => dependency.typeId))]
        .map(dependencyTypeId => ({
          value: dependencyTypeId,
          text: dependencyTypeId,
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
    nodeClick(
      { node }: {
        node: SummaryComponentDependencyNode
          | SummarySubComponentDependencyNode
          | undefined
      }
    ): void {
      if (node) {
        this.componentSidebarVisible = true
        this.node = node
        this.component = this.findComponent(node.componentId)
      } else {
        this.componentSidebarVisible = false
        this.node = undefined
        this.component = undefined
      }
    },
  },
})
</script>
