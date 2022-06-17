<template>
  <div class="m-3">
    <GraphQlSchemasView :components="components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { Component } from '~/types/kronicle-service'
import GraphQlSchemasView from '~/components/GraphQlSchemasView.vue'

export default Vue.extend({
  components: {
    GraphQlSchemasView,
  },
  async asyncData({ $config, route, store }) {
    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?stateType=graphql&fields=components(id,name,typeId,tags,teams,platformId,states)`
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
