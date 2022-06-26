<template>
  <div class="m-3">
    <h1 class="text-info my-3">
      {{ component.name }} - GraphQL Schema {{ graphQlSchemaIndex }}
    </h1>

    <ComponentTabs
      :component-id="component.id"
      :component-available-data="componentAvailableData"
    />

    <div class="text-center mb-3">
      <b-button
        :to="`/components/${component.id}/graphql-schemas/${graphQlSchemaIndex}/content`"
        variant="info"
      >
        View Full Screen
      </b-button>
    </div>

    <GraphQlSchemaView
      :component="component"
      :graph-ql-schema-index="graphQlSchemaIndex"
    />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { BButton } from 'bootstrap-vue'
import { Component } from '~/types/kronicle-service'
import GraphQlSchemaView from '~/components/GraphQlSchemaView.vue'
import {
  ComponentAvailableData,
  fetchComponentAvailableData,
} from '~/src/fetchComponentAvailableData'

export default Vue.extend({
  components: {
    'b-button': BButton,
    GraphQlSchemaView,
  },
  async asyncData({ $config, route }) {
    const componentAvailableData = await fetchComponentAvailableData(
      $config,
      route
    )

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?stateType=graphql-schemas&fields=component(id,name,teams,states)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component)

    return {
      componentAvailableData,
      component,
    }
  },
  data() {
    return {
      componentAvailableData: {} as ComponentAvailableData,
      component: {} as Component,
      graphQlSchemaIndex: parseInt(this.$route.params.graphQlSchemaIndex, 10),
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.$route.params.componentId} - GraphQL ${parseInt(
        this.$route.params.graphQlSchemaIndex,
        10
      )}`,
    }
  },
})
</script>
