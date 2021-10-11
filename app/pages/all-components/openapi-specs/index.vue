<template>
  <div>
    <b-container fluid>
      <b-row>
        <b-col>
          <AllComponentsTabs />
        </b-col>
      </b-row>
    </b-container>

    <OpenApiSpecsView :components="components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import {BCol, BContainer, BRow} from "bootstrap-vue";
import { Component } from '~/types/kronicle-service'
import AllComponentsTabs from '~/components/AllComponentsTabs.vue'
import OpenApiSpecsView from '~/components/OpenApiSpecsView.vue'

export default Vue.extend({
  components: {
    AllComponentsTabs,
    'b-col': BCol,
    'b-container': BContainer,
    'b-row': BRow,
    OpenApiSpecsView,
  },
  async asyncData({ $config, route, store }) {
    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,name,typeId,tags,teams,platformId,openApiSpecs)`
    )
      .then((res) => res.json())
      .then((json) => json.components)

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
  head() {
    return {
      title: 'Kronicle - All Components - OpenAPI Specs',
    }
  },
})
</script>
