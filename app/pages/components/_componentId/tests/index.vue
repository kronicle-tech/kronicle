<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ component.name }} - Tests</h1>

    <ComponentTabs :component-id="component.id" />

    <TestResultsView :components="[component]" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component } from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import TestResultsView from '~/components/TestResultsView.vue'

export default Vue.extend({
  components: {
    ComponentTabs,
    TestResultsView,
  },
  async asyncData({ $config, route, store }) {
    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,typeId,tags,teams,platformId,testResults)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component)

    store.commit('componentFilters/initialize', {
      components: [component],
      route,
    })

    return {
      component,
    }
  },
  data() {
    return {
      component: {} as Component,
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.component.name} - Tests`,
    }
  },
})
</script>
