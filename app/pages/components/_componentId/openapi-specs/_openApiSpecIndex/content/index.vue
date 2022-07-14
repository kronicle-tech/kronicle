<template>
  <OpenApiSpecView
    :component="component"
    :open-api-spec-index="openApiSpecIndex"
  />
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component } from '~/types/kronicle-service'
import OpenApiSpecView from '~/components/OpenApiSpecView.vue'

export default Vue.extend({
  components: {
    OpenApiSpecView,
  },
  layout: 'Minimal',
  async asyncData({ $config, route, error }) {
    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?stateType=openapi-specs&fields=component(id,name,teams,states)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component | undefined)

    if (!component) {
      error({
        message: 'Component not found',
        statusCode: 404,
      })
      return
    }

    return {
      component,
    }
  },
  data() {
    return {
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
