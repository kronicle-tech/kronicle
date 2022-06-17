<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ area.name }} Area - Visualizations</h1>

    <AreaTabs :area-id="area.id" />

    <ComponentDependenciesView
      :all-components="allComponents"
      :components="area.components"
      :diagrams="diagrams"
    />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import AreaTabs from '~/components/AreaTabs.vue'
import ComponentDependenciesView from '~/components/ComponentDependenciesView.vue'
import { Area, Component, Diagram } from '~/types/kronicle-service'

export default Vue.extend({
  components: {
    ComponentDependenciesView,
    AreaTabs,
  },
  async asyncData({ $config, route, store }) {
    const area = await fetch(
      `${$config.serviceBaseUrl}/v1/areas/${route.params.areaId}?fields=area(id,name,components(id,name,typeId,tags,description,notes,responsibilities,teams,platformId,states(environmentId,pluginId)))`
    )
      .then((res) => res.json())
      .then((json) => json.area as Area)

    store.commit('componentFilters/initialize', {
      components: area.components,
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
      area,
      allComponents,
      diagrams,
    }
  },
  data() {
    return {
      area: {} as Area,
      allComponents: [] as Component[],
      diagrams: [] as Diagram[],
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.area.name} Area - Visualizations`,
    }
  },
})
</script>
