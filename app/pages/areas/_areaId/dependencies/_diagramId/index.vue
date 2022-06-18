<template>
  <div class="m-3">
    <h1 class="text-info my-3">
      {{ area.name }} - Diagrams - ${this.diagram.name}
    </h1>

    <AreaTabs :area-id="area.id" />

    <ComponentDependenciesView
      :all-components="allComponents"
      :components="area.components"
      :diagram="diagram"
    />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Area, Component, Diagram } from '~/types/kronicle-service'
import AreaTabs from '~/components/AreaTabs.vue'

export default Vue.extend({
  components: {
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

    const diagram = await fetch(
      `${$config.serviceBaseUrl}/v1/diagrams/${route.params.diagramId}`
    )
      .then((res) => res.json())
      .then((json) => json.diagram)

    return {
      area,
      diagram,
    }
  },
  data() {
    return {
      area: {} as Area,
      allComponents: [] as Component[],
      diagram: {} as Diagram,
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.area.name} - Diagrams - ${this.diagram.name}`,
    }
  },
})
</script>
