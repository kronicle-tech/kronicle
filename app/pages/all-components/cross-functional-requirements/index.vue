<template>
  <div>
    <b-container fluid>
      <b-row>
        <b-col>
          <AllComponentsTabs />
        </b-col>
      </b-row>
    </b-container>

    <CrossFunctionalRequirementsView :components="components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import {BCol, BContainer, BRow} from "bootstrap-vue";
import { Component } from '~/types/kronicle-service'
import AllComponentsTabs from '~/components/AllComponentsTabs.vue'
import CrossFunctionalRequirementsView from '~/components/CrossFunctionalRequirementsView.vue'

export default Vue.extend({
  components: {
    AllComponentsTabs,
    'b-col': BCol,
    'b-container': BContainer,
    'b-row': BRow,
    CrossFunctionalRequirementsView,
  },
  async asyncData({ $config, route, store }) {
    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,name,typeId,tags,teams,platformId,crossFunctionalRequirements)`
    )
      .then((res) => res.json())
      .then((json) => json.components as Component[])

    store.commit('componentFilters/initialize', {
      components,
      route,
    })

    return {
      components,
    }
  },
  data() {
    return {
      components: [] as Component[],
    }
  },
  head(): MetaInfo {
    return {
      title:
        'Kronicle - All Components - Cross Functional Requirements',
    }
  },
})
</script>
