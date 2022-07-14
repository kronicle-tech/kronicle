<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ area.name }} - Tech Debts</h1>

    <AreaTabs :area-id="area.id" />

    <TechDebtsView :components="area.components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Area } from '~/types/kronicle-service'
import AreaTabs from '~/components/AreaTabs.vue'
import TechDebtsView from '~/components/TechDebtsView.vue'

export default Vue.extend({
  components: {
    AreaTabs,
    TechDebtsView,
  },
  async asyncData({ $config, route, store, error }) {
    const area = await fetch(
      `${$config.serviceBaseUrl}/v1/areas/${route.params.areaId}?fields=area(id,name,components(id,name,type,tags,teams,platformId,techDebts))`
    )
      .then((res) => res.json())
      .then((json) => json.area as Area | undefined)

    if (!area) {
      error({
        message: 'Area not found',
        statusCode: 404,
      })
      return
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
      title: `Kronicle - ${this.area.name} - Tech Debts`,
    }
  },
})
</script>
