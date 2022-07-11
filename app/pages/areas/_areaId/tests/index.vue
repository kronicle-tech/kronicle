<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ area.name }} - Tests</h1>

    <AreaTabs :area-id="area.id" />

    <TestResultsView
      :area-id="area.id"
      :components="area.components"
      :test-outcomes-filter-enabled="false"
    />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Area } from '~/types/kronicle-service'
import AreaTabs from '~/components/AreaTabs.vue'
import TestResultsView from '~/components/TestResultsView.vue'

export default Vue.extend({
  components: {
    AreaTabs,
    TestResultsView,
  },
  async asyncData({ $config, route, store }) {
    const area = await fetch(
      `${$config.serviceBaseUrl}/v1/areas/${route.params.areaId}?testOutcome=fail&fields=area(id,name,components(id,name,type,tags,teams,platformId,testResults))`
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
      title: `Kronicle - ${this.area.name} - Tests`,
    }
  },
})
</script>
