<template>
  <div>
    <h1 class="text-info my-3">{{ area.name }} Area - OpenAPI Specs</h1>
    <AreaTabs :area-id="area.id" />
    <OpenApiSpecsView :components="area.components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Area } from '~/types/kronicle-service'
import AreaTabs from '~/components/AreaTabs.vue'
import OpenApiSpecsView from '~/components/OpenApiSpecsView.vue'

export default Vue.extend({
  components: {
    AreaTabs,
    OpenApiSpecsView,
  },
  async asyncData({ $config, route, store }) {
    const area = await fetch(
      `${$config.serviceBaseUrl}/v1/areas/${route.params.areaId}?fields=area(id,name,components(id,name,typeId,tags,teams,platformId,openApiSpecs))`
    )
      .then((res) => res.json())
      .then((json) => json.area as Area)

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
      title: `Kronicle - ${this.area.name} Area - OpenAPI Specs`,
    }
  },
})
</script>
