<template>
  <div>
    <AllComponentsTabs />
    <b-alert show="10" dismissible variant="info" class="my-3">
      Click a component's name in the table below to view more information about
      that component
    </b-alert>

    <ComponentsView :components="components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { BAlert } from 'bootstrap-vue'
import { MetaInfo } from 'vue-meta'
import { Component } from '~/types/kronicle-service'
import AllComponentsTabs from '~/components/AllComponentsTabs.vue'
import ComponentsView from '~/components/ComponentsView.vue'

export default Vue.extend({
  components: {
    AllComponentsTabs,
    'b-alert': BAlert,
    ComponentsView,
  },
  async asyncData({ $config, route, store }) {
    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,name,typeId,tags,description,notes,responsibilities,teams,platformId)`
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
      title: 'Kronicle - All Components',
    }
  },
})
</script>
