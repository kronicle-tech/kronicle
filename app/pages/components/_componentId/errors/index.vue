<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ component.name }} - Errors</h1>

    <ComponentTabs :component-id="component.id" :state-types="stateTypes" />

    <ScannerErrorsView :components="[component]" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component } from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import ScannerErrorsView from '~/components/ScannerErrorsView.vue'
import {fetchComponentStateTypes} from "~/src/fetchComponentStateTypes";

export default Vue.extend({
  components: {
    ComponentTabs,
    ScannerErrorsView,
  },
  async asyncData({ $config, route, store }) {
    const stateTypes = await fetchComponentStateTypes($config, route)

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,typeId,tags,teams,platformId,scannerErrors)`
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
      title: `Kronicle - ${this.component.name} - Errors`,
    }
  },
})
</script>
