<template>
  <div>
    <b-container fluid>
      <b-row>
        <b-col>
          <h1 class="text-info my-3">
            {{ component.name }} - Cross Functional Requirements
          </h1>

          <ComponentTabs :component-id="component.id" />
        </b-col>
      </b-row>
    </b-container>

    <CrossFunctionalRequirementsView :components="[component]" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import {BCol, BContainer, BRow} from "bootstrap-vue";
import { Component } from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import CrossFunctionalRequirementsView from '~/components/CrossFunctionalRequirementsView.vue'

export default Vue.extend({
  components: {
    'b-col': BCol,
    'b-container': BContainer,
    'b-row': BRow,
    ComponentTabs,
    CrossFunctionalRequirementsView,
  },
  async asyncData({ $config, route, store }) {
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
      title: `Kronicle - ${this.component.name} - Cross Functional Requirements`,
    }
  },
})
</script>
