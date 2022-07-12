<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ component.name }} - Diagrams</h1>

    <ComponentTabs
      :component-id="component.id"
      :component-available-data="componentAvailableData"
    />

    <DiagramsView :diagrams="diagrams" :components="[]" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component, Diagram } from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import DiagramsView from '~/components/DiagramsView.vue'
import {
  ComponentAvailableData,
  fetchComponentAvailableData,
} from '~/src/fetchComponentAvailableData'
import { NuxtError } from '~/src/nuxtError'

export default Vue.extend({
  components: {
    ComponentTabs,
    DiagramsView,
  },
  async asyncData({ $config, route }) {
    const componentAvailableData = await fetchComponentAvailableData(
      $config,
      route
    )

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,type,tags,description,notes,responsibilities,teams,platformId,states(environmentId,pluginId))`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component | undefined)

    if (!component) {
      throw new NuxtError('Component not found', 404)
    }

    const diagrams = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}/diagrams?fields=diagrams(id,name,description)`
    )
      .then((res) => res.json())
      .then((json) => json.diagrams)

    return {
      componentAvailableData,
      component,
      diagrams,
    }
  },
  data() {
    return {
      componentAvailableData: {} as ComponentAvailableData,
      component: {} as Component,
      diagrams: [] as Diagram[],
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.component.name} - Diagrams`,
    }
  },
})
</script>
