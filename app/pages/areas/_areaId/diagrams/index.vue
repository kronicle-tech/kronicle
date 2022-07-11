<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ area.name }} - Diagrams</h1>

    <AreaTabs :area-id="area.id" />

    <DiagramsView :diagrams="diagrams" :components="area.components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Area, Diagram } from '~/types/kronicle-service'
import AreaTabs from '~/components/AreaTabs.vue'
import DiagramsView from '~/components/DiagramsView.vue'

export default Vue.extend({
  components: {
    AreaTabs,
    DiagramsView,
  },
  async asyncData({ $config, route, store }) {
    const area = await fetch(
      `${$config.serviceBaseUrl}/v1/areas/${route.params.areaId}?fields=area(id,name,components(id,name,type,tags,description,notes,responsibilities,teams,platformId))`
    )
      .then((res) => res.json())
      .then((json) => json.area as Area)

    store.commit('componentFilters/initialize', {
      components: area.components,
      route,
    })

    const diagrams = await fetch(`${$config.serviceBaseUrl}/v1/diagrams`)
      .then((res) => res.json())
      .then((json) => json.diagrams)

    return {
      area,
      diagrams,
    }
  },
  data() {
    return {
      area: {} as Area,
      diagrams: [] as Diagram[],
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.area.name} - Diagrams`,
    }
  },
})
</script>
