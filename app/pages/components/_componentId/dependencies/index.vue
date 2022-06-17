<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ component.name }} - Visualizations</h1>

    <ComponentTabs
      :component-id="component.id"
      :component-available-data="componentAvailableData"
    />

    <ComponentDependenciesView
      :all-components="allComponents"
      :components="[component]"
      :diagrams="diagrams"
      :selected-component-id="component.id"
      :scope-related-radius="1"
    />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component, Diagram } from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import ComponentDependenciesView from '~/components/ComponentDependenciesView.vue'
import { fetchComponentAvailableData } from '~/src/fetchComponentAvailableData'

export default Vue.extend({
  components: {
    ComponentDependenciesView,
    ComponentTabs,
  },
  async asyncData({ $config, route, store }) {
    const componentAvailableData = await fetchComponentAvailableData(
      $config,
      route
    )

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,typeId,tags,description,notes,responsibilities,teams,platformId,states(environmentId,pluginId))`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component)

    store.commit('componentFilters/initialize', {
      components: [component],
      route,
    })

    const allComponents = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,name,typeId,tags,description,notes,responsibilities,teams,platformId)`
    )
      .then((res) => res.json())
      .then((json) => json.components as Component[])

    const diagrams = await fetch(
      `${$config.serviceBaseUrl}/v1/components/diagrams`
    )
      .then((res) => res.json())
      .then((json) => json.diagrams as Diagram[])

    return {
      componentAvailableData,
      component,
      allComponents,
      diagrams,
    }
  },
  data() {
    return {
      componentAvailableData: [] as string[],
      component: {} as Component,
      allComponents: [] as Component[],
      diagrams: [] as Diagram[],
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.component.name} - Visualizations`,
    }
  },
})
</script>
