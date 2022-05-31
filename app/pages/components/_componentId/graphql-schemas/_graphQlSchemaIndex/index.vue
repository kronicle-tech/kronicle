<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ component.name }} - GraphQL Schema {{ graphQlSchemaIndex }}</h1>

    <ComponentTabs :component-id="component.id" :state-types="stateTypes" />

    <div class="text-center mb-3">
      <NuxtLink :to="`/components/${component.id}/graphql-schemas/${graphQlSchemaIndex}/content`">
        View Full Screen
      </NuxtLink>
    </div>

    <GraphQlSchemaView :component="component" :graph-ql-schema-index="graphQlSchemaIndex" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component } from '~/types/kronicle-service'
import GraphQlSchemaView from "~/components/GraphQlSchemaView.vue";
import {fetchComponentStateTypes} from "~/src/fetchComponentStateTypes";

export default Vue.extend({
  components: {
    GraphQlSchemaView,
  },
  async asyncData({ $config, route }) {
    const stateTypes = await fetchComponentStateTypes($config, route)

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,teams,graphQlSchemas)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component)

    return {
      stateTypes,
      component,
    }
  },
  data() {
    return {
      stateTypes: [] as string[],
      component: {} as Component,
      graphQlSchemaIndex: parseInt(this.$route.params.graphQlSchemaIndex, 10),
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${
        this.$route.params.componentId
      } - GraphQL ${parseInt(this.$route.params.graphQlSchemaIndex, 10)}`,
    }
  },
})
</script>
