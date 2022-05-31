<template>
  <div class="m-3">
    <h1 class="text-info">{{ component.name }} - OpenAPI Specs</h1>

    <ComponentTabs :component-id="component.id" :state-types="stateTypes" />

    <OpenApiSpecsView :components="[component]" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component } from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import OpenApiSpecsView from '~/components/OpenApiSpecsView.vue'
import {fetchComponentStateTypes} from "~/src/fetchComponentStateTypes";

export default Vue.extend({
  components: {
    ComponentTabs,
    OpenApiSpecsView,
  },
  async asyncData({ $config, route, store }) {
    const stateTypes = await fetchComponentStateTypes($config, route)

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,teams,openApiSpecs)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component)

    store.commit('componentFilters/initialize', {
      components: [component],
      route,
    })

    return {
      stateTypes,
      component,
    }
  },
  data() {
    return {
      stateTypes: [] as string[],
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
