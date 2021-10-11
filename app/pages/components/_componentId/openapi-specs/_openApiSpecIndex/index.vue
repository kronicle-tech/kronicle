<template>
  <div>
    <b-container fluid>
      <b-row>
        <b-col>
          <h1 class="text-info my-3">{{ component.name }} - OpenAPI Spec {{ openApiSpecIndex }}</h1>

          <ComponentTabs :component-id="component.id" />
        </b-col>
      </b-row>
      <b-row>
        <b-col class="text-center">
          <NuxtLink :to="`/components/${component.id}/openapi-specs/${openApiSpecIndex}/content`">
            View Full Screen
          </NuxtLink>
        </b-col>
      </b-row>
    </b-container>

    <OpenApiSpecView :component="component" :open-api-spec-index="openApiSpecIndex" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import {BCol, BContainer, BRow} from "bootstrap-vue";
import { Component } from '~/types/kronicle-service'
import OpenApiSpecView from "~/components/OpenApiSpecView.vue";

export default Vue.extend({
  components: {
    'b-col': BCol,
    'b-container': BContainer,
    'b-row': BRow,
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
