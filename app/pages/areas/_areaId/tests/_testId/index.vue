<template>
  <div>
    <h1 class="text-info my-3">
      {{ area.name }} Area - {{ this.$route.params.testId }} Test
    </h1>
    <AreaTabs :area-id="area.id" />
    <TestView :test="test" :components="area.components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Area, Test } from '~/types/component-catalog-service'
import AreaTabs from '~/components/AreaTabs.vue'
import TestView from '~/components/TestView.vue'

export default Vue.extend({
  components: {
    AreaTabs,
    TestView,
  },
  async asyncData({ $config, route, store }) {
    const test = await fetch(
      `${$config.serviceBaseUrl}/v1/tests/${route.params.testId}?fields=test(id,description,priority)`
    )
      .then((res) => res.json())
      .then((json) => json.test as Test)

    const area = await fetch(
      `${$config.serviceBaseUrl}/v1/areas/${route.params.areaId}?fields=area(id,name,components(id,name,typeId,tags,teams,platformId,testResults))`
    )
      .then((res) => res.json())
      .then((json) => json.area as Area)

    store.commit('componentFilters/initialize', {
      components: area.components,
      route,
    })

    return {
      test,
      area,
    }
  },
  data() {
    return {
      test: {} as Test,
      area: {} as Area,
    }
  },
  head(): MetaInfo {
    return {
      title: `Component Catalog - ${this.area.name} Area - ${this.$route.params.testId} Test`,
    }
  },
})
</script>
