<template>
  <div class="m-3">
    <h1 class="text-info my-3">
      {{ component.name }} - Downstream Response Times
    </h1>

    <ComponentTabs
      :component-id="component.id"
      :component-available-data="componentAvailableData"
    />

    <ComponentResponseTimesView
      :component-id="component.id"
      direction="downstream"
      :all-components="allComponents"
      :sub-component-dependencies="summary.subComponentDependencies"
    />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component, Summary } from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import ComponentResponseTimesView from '~/components/ComponentResponseTimesView.vue'
import {
  ComponentAvailableData,
  fetchComponentAvailableData,
} from '~/src/fetchComponentAvailableData'

export default Vue.extend({
  components: {
    ComponentResponseTimesView,
    ComponentTabs,
  },
  async asyncData({ $config, route, error }) {
    const componentAvailableData = await fetchComponentAvailableData(
      $config,
      route,
      error
    )

    if (!componentAvailableData) {
      return
    }

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component | undefined)

    const allComponents = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,name)`
    )
      .then((res) => res.json())
      .then((json) => json.components as Component[])

    const summary = await fetch(
      `${$config.serviceBaseUrl}/v1/summary?fields=summary(subComponentDependencies)`
    )
      .then((res) => res.json())
      .then((json) => json.summary as Summary)

    return {
      componentAvailableData,
      component,
      allComponents,
      summary,
    }
  },
  data() {
    return {
      componentAvailableData: {} as ComponentAvailableData,
      component: {} as Component,
      allComponents: [] as Component[],
      summary: {} as Summary,
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.component.name} - Downstream Response Times`,
    }
  },
})
</script>
