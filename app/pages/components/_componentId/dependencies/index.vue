<template>
  <div>
    <b-container fluid>
      <b-row>
        <b-col>
          <h1 class="text-info my-3">{{ component.name }} - Dependencies</h1>

          <ComponentTabs :component-id="component.id" />
        </b-col>
      </b-row>
    </b-container>

    <ComponentDependenciesView
      :all-components="allComponents"
      :components="[component]"
      :component-dependencies="summary.componentDependencies"
      :sub-component-dependencies="summary.subComponentDependencies"
      :selected-component-id="component.id"
      :scope-related-radius="1"
    />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import {BCol, BContainer, BRow} from "bootstrap-vue";
import { Component, Summary } from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import ComponentDependenciesView from '~/components/ComponentDependenciesView.vue'

export default Vue.extend({
  components: {
    'b-col': BCol,
    'b-container': BContainer,
    'b-row': BRow,
    ComponentDependenciesView,
    ComponentTabs,
  },
  async asyncData({ $config, route, store }) {
    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,typeId,tags,description,notes,responsibilities,teams,platformId)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component)

    store.commit('componentFilters/initialize', {
      components: [component],
      route,
    })

    const allComponents = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,name,typeId,tags,description,notes,responsibilities,teams,platformId)`
    )
      .then((res) => res.json())
      .then((json) => json.components as Component[])

    const summary = await fetch(
      `${$config.serviceBaseUrl}/v1/summary?fields=summary(componentDependencies,subComponentDependencies)`
    )
      .then((res) => res.json())
      .then((json) => json.summary as Summary)

    return {
      component,
      allComponents,
      summary,
    }
  },
  data() {
    return {
      component: {} as Component,
      allComponents: [] as Component[],
      summary: {} as Summary,
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.component.name} - Dependencies`,
    }
  },
})
</script>
