<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ component.name }} - OpenAPI Spec {{ openApiSpecIndex }}</h1>

    <ComponentTabs :component-id="component.id" />

    <div class="text-center mb-3">
      <NuxtLink :to="`/components/${component.id}/openapi-specs/${openApiSpecIndex}/content`">
        View Full Screen
      </NuxtLink>
    </div>

    <OpenApiSpecView :component="component" :open-api-spec-index="openApiSpecIndex" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component } from '~/types/kronicle-service'
import OpenApiSpecView from "~/components/OpenApiSpecView.vue";

export default Vue.extend({
  components: {
    OpenApiSpecView,
  },
  async asyncData({ $config, route }) {
    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,teams,openApiSpecs)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component)

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
