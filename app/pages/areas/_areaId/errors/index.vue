<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ area.name }} Area - Errors</h1>

    <AreaTabs :area-id="area.id" />

    <ScannerErrorsView :components="area.components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Area } from '~/types/kronicle-service'
import AreaTabs from '~/components/AreaTabs.vue'
import ScannerErrorsView from '~/components/ScannerErrorsView.vue'

export default Vue.extend({
  components: {
    AreaTabs,
    ScannerErrorsView,
  },
  async asyncData({ $config, route, store }) {
    const area = await fetch(
      `${$config.serviceBaseUrl}/v1/areas/${route.params.areaId}?fields=area(id,name,components(id,name,typeId,tags,teams,platformId,scannerErrors))`
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
      title: `Kronicle - ${this.area.name} Area - Errors`,
    }
  },
})
</script>
