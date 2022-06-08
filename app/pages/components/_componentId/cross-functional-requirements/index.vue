<template>
  <div class="m-3">
    <h1 class="text-info my-3">
      {{ component.name }} - Cross Functional Requirements
    </h1>

    <ComponentTabs
      :component-id="component.id"
      :component-available-data="componentAvailableData"
    />

    <CrossFunctionalRequirementsView :components="[component]" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component } from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import CrossFunctionalRequirementsView from '~/components/CrossFunctionalRequirementsView.vue'
import { fetchComponentAvailableData } from '~/src/fetchComponentAvailableData'

export default Vue.extend({
  components: {
    ComponentTabs,
    CrossFunctionalRequirementsView,
  },
  async asyncData({ $config, route, store }) {
    const componentAvailableData = await fetchComponentAvailableData(
      $config,
      route
    )

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,typeId,tags,teams,platformId,crossFunctionalRequirements)`
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
      title: `Kronicle - ${this.component.name} - Cross Functional Requirements`,
    }
  },
})
</script>
