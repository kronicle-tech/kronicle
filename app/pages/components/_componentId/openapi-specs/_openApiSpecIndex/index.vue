<template>
  <div class="m-3">
    <h1 class="text-info my-3">
      {{ component.name }} - OpenAPI Spec {{ openApiSpecIndex }}
    </h1>

    <ComponentTabs
      :component-id="component.id"
      :component-available-data="componentAvailableData"
    />

    <div class="text-center mb-3">
      <b-button
        :to="`/components/${component.id}/openapi-specs/${openApiSpecIndex}/content`"
        variant="info"
      >
        View Full Screen
      </b-button>
    </div>

    <OpenApiSpecView
      :component="component"
      :open-api-spec-index="openApiSpecIndex"
    />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { BButton } from 'bootstrap-vue'
import { Component } from '~/types/kronicle-service'
import OpenApiSpecView from '~/components/OpenApiSpecView.vue'
import {
  ComponentAvailableData,
  fetchComponentAvailableData,
} from '~/src/fetchComponentAvailableData'

export default Vue.extend({
  components: {
    'b-button': BButton,
    OpenApiSpecView,
  },
  async asyncData({ $config, route, error }) {
    const componentAvailableData = await fetchComponentAvailableData(
      $config,
      route,
      error
    )

    if (!componentAvailableData) {
      return
    }

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?stateType=openapi-specs&fields=component(id,name,teams,states)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component | undefined)

    return {
      componentAvailableData,
      component,
    }
  },
  data() {
    return {
      componentAvailableData: {} as ComponentAvailableData,
      component: {} as Component,
      openApiSpecIndex: parseInt(this.$route.params.openApiSpecIndex, 10),
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${
        this.$route.params.componentId
      } - OpenAPI Spec ${parseInt(this.$route.params.openApiSpecIndex, 10)}`,
    }
  },
})
</script>
