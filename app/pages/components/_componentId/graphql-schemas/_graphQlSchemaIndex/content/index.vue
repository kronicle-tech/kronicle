<template>
  <GraphQlSchemaView :component="component" :graph-ql-schema-index="graphQlSchemaIndex" />
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component } from '~/types/kronicle-service'
import GraphQlSchemaView from "~/components/GraphQlSchemaView.vue";

export default Vue.extend({
  components: {
    GraphQlSchemaView,
  },
  layout: 'Minimal',
  async asyncData({ $config, route }) {
    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?stateType=graphql-schemas&fields=component(id,name,teams,states)`
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
      graphQlSchemaIndex: parseInt(this.$route.params.graphQlSchemaIndex, 10),
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${
        this.$route.params.componentId
      } - GraphQL Schema ${parseInt(this.$route.params.graphQlSchemaIndex, 10)}`,
    }
  },
})
</script>
