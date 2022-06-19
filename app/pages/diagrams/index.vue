<template>
  <div class="m-3">
    <DiagramsView :diagrams="diagrams" :components="components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component, Diagram } from '~/types/kronicle-service'
import DiagramsView from '~/components/DiagramsView.vue'

export default Vue.extend({
  components: {
    DiagramsView,
  },
  async asyncData({ $config, route, store }) {
    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,name,typeId,tags,teams,platformId,states(environmentId,pluginId))`
    )
      .then((res) => res.json())
      .then((json) => json.components)

    store.commit('componentFilters/initialize', {
      components,
      route,
    })

    const diagrams = await fetch(
      `${$config.serviceBaseUrl}/v1/diagrams?fields=diagrams(id,name,type,tags)`
    )
      .then((res) => res.json())
      .then((json) => json.diagrams)

    return {
      components,
      diagrams,
    }
  },
  data() {
    return {
      components: [] as Component[],
      diagrams: [] as Diagram[],
    }
  },
  head(): MetaInfo {
    return {
      title: 'Kronicle - Diagrams',
    }
  },
})
</script>
