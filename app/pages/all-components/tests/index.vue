<template>
  <div class="m-3">
    <AllComponentsTabs />

    <TestResultsView
      :components="components"
      :test-outcomes-filter-enabled="false"
    />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component } from '~/types/kronicle-service'
import AllComponentsTabs from '~/components/AllComponentsTabs.vue'
import TestResultsView from '~/components/TestResultsView.vue'

export default Vue.extend({
  components: {
    AllComponentsTabs,
    TestResultsView,
  },
  async asyncData({ $config, route, store }) {
    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?testOutcome=fail&fields=components(id,name,type,tags,teams,platformId,testResults)`
    )
      .then((res) => res.json())
      .then((json) => json.components as Component[])

    store.commit('componentFilters/initialize', {
      components,
      route,
    })

    return {
      components,
    }
  },
  data() {
    return {
      components: [] as Component[],
    }
  },
  head(): MetaInfo {
    return {
      title: 'Kronicle - Tests',
    }
  },
})
</script>
