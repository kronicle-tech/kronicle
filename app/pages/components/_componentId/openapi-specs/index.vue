<template>
  <div class="m-3">
    <h1 class="text-info">{{ component.name }} - OpenAPI Specs</h1>

    <ComponentTabs
      :component-id="component.id"
      :component-available-data="componentAvailableData"
    />

    <OpenApiSpecsView :components="[component]" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component } from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import OpenApiSpecsView from '~/components/OpenApiSpecsView.vue'
import {
  ComponentAvailableData,
  fetchComponentAvailableData,
} from '~/src/fetchComponentAvailableData'

export default Vue.extend({
  components: {
    ComponentTabs,
    OpenApiSpecsView,
  },
  async asyncData({ $config, route, store, error }) {
    const componentAvailableData = await fetchComponentAvailableData(
      $config,
      route,
      error
    )

    if (!componentAvailableData) {
      return
    }

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?stateType=openapi-specs&fields=component(id,name,teams,states)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component | undefined)

    store.commit('componentFilters/initialize', {
      components: [component],
      route,
    })

    return {
      componentAvailableData,
      component,
    }
  },
  data() {
    return {
      componentAvailableData: {} as ComponentAvailableData,
      component: {} as Component,
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.component.name} - OpenAPI Specs`,
    }
  },
})
</script>
