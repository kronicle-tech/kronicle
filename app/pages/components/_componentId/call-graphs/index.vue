<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ component.name }} - Call Graphs</h1>

    <ComponentTabs :component-id="component.id" :component-available-data="componentAvailableData" />

    <ComponentCallGraphsView
      :component="component"
      :nodes="nodes"
      :call-graphs="callGraphs"
    />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import {
  Component,
  SummarySubComponentDependencies,
  SummarySubComponentDependencyNode,
} from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import ComponentCallGraphsView from '~/components/ComponentCallGraphsView.vue'
import {fetchComponentAvailableData} from "~/src/fetchComponentAvailableData";

export default Vue.extend({
  components: {
    ComponentCallGraphsView,
    ComponentTabs,
  },
  async asyncData({ $config, route }) {
    const componentAvailableData = await fetchComponentAvailableData($config, route)

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component)

    const nodes = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}/nodes`
    )
      .then((res) => res.json())
      .then((json) => json.nodes as SummarySubComponentDependencyNode[])

    const callGraphs = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}/call-graphs`
    )
      .then((res) => res.json())
      .then((json) => json.callGraphs as SummarySubComponentDependencies[])

    return {
      componentAvailableData,
      component,
      nodes,
      callGraphs,
    }
  },
  data() {
    return {
      componentAvailableData: [] as string[],
      component: {} as Component,
      nodes: [] as SummarySubComponentDependencyNode[],
      callGraphs: [] as SummarySubComponentDependencies[],
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.component.name} - Call Graphs`,
    }
  },
})
</script>
