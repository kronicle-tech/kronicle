<template>
  <div class="m-3">
    <EnvironmentStatesView :components="components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import {NuxtRuntimeConfig} from "@nuxt/types/config/runtime";
import {Route} from "vue-router";
import {Store} from "vuex";
import EnvironmentStatesView from '~/components/EnvironmentStatesView.vue'
import { Component } from '~/types/kronicle-service'

interface Data {
  components: Component[]
}

async function loadData(
  { $config, route, store }: { $config: NuxtRuntimeConfig, route: Route, store: Store<any> }
): Promise<Data> {
  const components = await fetch(
    `${$config.serviceBaseUrl}/v1/components?fields=components(id,name,typeId,tags,description,notes,responsibilities,teams,platformId,states)`
  )
    .then((res) => res.json())
    .then((json) => json.components as Component[])

  store.commit('componentFilters/initialize', {
    components,
    route,
  })

  return {
    components: [] as Component[],
  }
}

export default Vue.extend({
  components: {
    EnvironmentStatesView,
  },
  async asyncData({ $config, route, store }) {
    return await loadData({
      $config,
      route,
      store,
    })
  },
  data() {
    return {
      components: [] as Component[],
    }
  },
  head(): MetaInfo {
    return {
      title: 'Kronicle - All Environments',
    }
  },
  mounted() {
    setInterval(
      async () => {
        const data = await loadData({
          $config: this.$config,
          route: this.$route,
          store: this.$store
        })
        this.components = data.components
      },
      60 * 1000
    )
  },
})
</script>
