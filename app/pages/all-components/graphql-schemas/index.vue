<template>
  <div class="m-3">
    <AllComponentsTabs />

    <GraphQlSchemasView :components="components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { Component } from '~/types/kronicle-service'
import AllComponentsTabs from '~/components/AllComponentsTabs.vue'
import GraphQlSchemasView from '~/components/GraphQlSchemasView.vue'

export default Vue.extend({
  components: {
    AllComponentsTabs,
    GraphQlSchemasView,
  },
  async asyncData({ $config, route, store }) {
    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?stateType=graphql-schemas&fields=components(id,name,type,tags,teams,platformId,states)`
    )
      .then((res) => res.json())
      .then((json) => json.components)

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
  head() {
    return {
      title: 'Kronicle - GraphQL Schemas',
    }
  },
})
</script>
