<template>
  <div>
    <AllComponentsTabs />
    <ComponentDependenciesView
      :all-components="components"
      :components="components"
      :component-dependencies="summary.componentDependencies"
      :sub-component-dependencies="summary.subComponentDependencies"
    />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component, Summary } from '~/types/kronicle-service'
import AllComponentsTabs from '~/components/AllComponentsTabs.vue'
import ComponentDependenciesView from '~/components/ComponentDependenciesView.vue'

export default Vue.extend({
  components: {
    AllComponentsTabs,
    ComponentDependenciesView,
  },
  async asyncData({ $config, route, store }) {
    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,name,typeId,tags,description,notes,responsibilities,teams,platformId)`
    )
      .then((res) => res.json())
      .then((json) => json.components as Component[])

    const summary = await fetch(
      `${$config.serviceBaseUrl}/v1/summary?fields=summary(componentDependencies,subComponentDependencies)`
    )
      .then((res) => res.json())
      .then((json) => json.summary as Summary)

    store.commit('componentFilters/initialize', {
      components,
      route,
    })

    return {
      components,
      summary,
    }
  },
  data() {
    return {
      components: [] as Array<Component>,
      summary: {} as Summary,
    }
  },
  head(): MetaInfo {
    return {
      title: 'Kronicle - All Components - Dependencies',
    }
  },
})
</script>
