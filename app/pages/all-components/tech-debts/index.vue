<template>
  <div class="m-3">
    <AllComponentsTabs />

    <TechDebtsView :components="components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component } from '~/types/kronicle-service'
import AllComponentsTabs from '~/components/AllComponentsTabs.vue'
import TechDebtsView from '~/components/TechDebtsView.vue'

export default Vue.extend({
  components: {
    AllComponentsTabs,
    TechDebtsView,
  },
  async asyncData({ $config, route, store }) {
    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,name,type,tags,teams,platformId,techDebts)`
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
      title: 'Kronicle - Tech Debts',
    }
  },
})
</script>
