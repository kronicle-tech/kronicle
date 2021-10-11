<template>
  <div>
    <b-container fluid>
      <b-row>
        <b-col>
          <h1 class="text-info my-3">{{ area.name }} Area - Components</h1>

          <AreaTabs :area-id="area.id" />
        </b-col>
      </b-row>
    </b-container>

    <ComponentsView :components="area.components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import {BCol, BContainer, BRow} from "bootstrap-vue";
import { Area } from '~/types/kronicle-service'
import AreaTabs from '~/components/AreaTabs.vue'
import ComponentsView from '~/components/ComponentsView.vue'

export default Vue.extend({
  components: {
    AreaTabs,
    'b-col': BCol,
    'b-container': BContainer,
    'b-row': BRow,
    ComponentsView,
  },
  async asyncData({ $config, route, store }) {
    const area = await fetch(
      `${$config.serviceBaseUrl}/v1/areas/${route.params.areaId}?fields=area(id,name,components(id,name,typeId,description,tags,teams,platformId))`
    )
      .then((res) => res.json())
      .then((json) => json.area as Area)

    store.commit('componentFilters/initialize', {
      components: area.components,
      route,
    })

    return {
      area,
    }
  },
  data() {
    return {
      area: {} as Area,
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.area.name} Area - Components`,
    }
  },
})
</script>
