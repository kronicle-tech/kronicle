<template>
  <div>
    <b-container fluid>
      <b-row>
        <b-col>
          <AllComponentsTabs />
        </b-col>
      </b-row>
    </b-container>

    <TestResultsView
      :components="components"
      :test-outcomes-filter-enabled="false"
    />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import {BCol, BContainer, BRow} from "bootstrap-vue";
import { Component } from '~/types/kronicle-service'
import AllComponentsTabs from '~/components/AllComponentsTabs.vue'
import TestResultsView from '~/components/TestResultsView.vue'

export default Vue.extend({
  components: {
    AllComponentsTabs,
    'b-col': BCol,
    'b-container': BContainer,
    'b-row': BRow,
    TestResultsView,
  },
  async asyncData({ $config, route, store }) {
    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?testOutcome=fail&fields=components(id,name,typeId,tags,teams,platformId,testResults)`
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
      title: 'Kronicle - All Components - Tests',
    }
  },
})
</script>
