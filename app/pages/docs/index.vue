<template>
  <div class="m-3">
    <h1 class="text-info my-3">Docs</h1>

    <DocsView :components="components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component } from '~/types/kronicle-service'
import DocsView from '~/components/DocsView.vue'

export default Vue.extend({
  components: {
    DocsView,
  },
  async asyncData({ $config, route, store }) {
    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?stateType=doc&fields=components(id,name,teams,states)`
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
      title: `Kronicle - Docs`,
    }
  },
})
</script>
