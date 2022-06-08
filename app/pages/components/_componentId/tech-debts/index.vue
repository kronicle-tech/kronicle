<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ component.name }} - Tech Debts</h1>

    <ComponentTabs
      :component-id="component.id"
      :component-available-data="componentAvailableData"
    />

    <TechDebtsView :components="[component]" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component } from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import TechDebtsView from '~/components/TechDebtsView.vue'
import { fetchComponentAvailableData } from '~/src/fetchComponentAvailableData'

export default Vue.extend({
  components: {
    ComponentTabs,
    TechDebtsView,
  },
  async asyncData({ $config, route, store }) {
    const componentAvailableData = await fetchComponentAvailableData(
      $config,
      route
    )

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,typeId,tags,teams,platformId,techDebts)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component)

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
      componentAvailableData: [] as string[],
      component: {} as Component,
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.component.name} - Tech Debts`,
    }
  },
})
</script>
