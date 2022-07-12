<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ area.name }} - Components</h1>

    <AreaTabs :area-id="area.id" />

    <ComponentsView :components="area.components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Area } from '~/types/kronicle-service'
import AreaTabs from '~/components/AreaTabs.vue'
import ComponentsView from '~/components/ComponentsView.vue'
import { NuxtError } from '~/src/nuxtError'

export default Vue.extend({
  components: {
    AreaTabs,
    ComponentsView,
  },
  async asyncData({ $config, route, store }) {
    const area = await fetch(
      `${$config.serviceBaseUrl}/v1/areas/${route.params.areaId}?fields=area(id,name,components(id,name,discovered,type,description,tags,teams,platformId,states(environmentId,pluginId)))`
    )
      .then((res) => res.json())
      .then((json) => json.area as Area | undefined)

    if (!area) {
      throw new NuxtError('Area not found', 404)
    }

    store.commit('componentFilters/initialize', {
      components: area.components,
      route,
    })

    return {
      area,
    }
  },
  data() {
    return {
      area: {} as Area,
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.area.name} - Components`,
    }
  },
})
</script>
