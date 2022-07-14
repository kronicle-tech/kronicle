<template>
  <div class="m-3">
    <DiagramView
      :all-components="components"
      :components="components"
      :diagram="diagram"
    />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component, Diagram } from '~/types/kronicle-service'
import DiagramView from '~/components/DiagramView.vue'

export default Vue.extend({
  components: {
    DiagramView,
  },
  async asyncData({ $config, route, store, error }) {
    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,name,type,tags,description,notes,responsibilities,teams,platformId,states(environmentId,pluginId))`
    )
      .then((res) => res.json())
      .then((json) => json.components as Component[])

    store.commit('componentFilters/initialize', {
      components,
      route,
    })

    const diagram = await fetch(
      `${$config.serviceBaseUrl}/v1/diagrams/${route.params.diagramId}`
    )
      .then((res) => res.json())
      .then((json) => json.diagram as Diagram | undefined)

    if (!diagram) {
      error({
        message: 'Diagram not found',
        statusCode: 404,
      })
      return
    }

    return {
      components,
      diagram,
    }
  },
  data() {
    return {
      components: [] as Array<Component>,
      diagram: {} as Diagram,
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - Diagrams - ${this.diagram.name}`,
    }
  },
})
</script>
