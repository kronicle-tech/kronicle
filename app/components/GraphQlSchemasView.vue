<template>
  <div>
    <b-card no-body class="my-3">
      <b-list-group>
        <b-list-group-item :variant="countVariant">
          <span :class="countClass">{{ count }}</span>
          GraphQL schema{{ count === 1 ? '' : 's' }}
        </b-list-group-item>
      </b-list-group>
    </b-card>

    <ComponentFilters :components="components" />

    <GraphQlSchemaTable :components="filteredComponents" />
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BCard, BListGroup, BListGroupItem } from 'bootstrap-vue'
import {
  Component,
  GraphQlSchema,
  GraphQlSchemasState,
} from '~/types/kronicle-service'
import ComponentFilters from '~/components/ComponentFilters.vue'
import GraphQlSchemaTable from '~/components/GraphQlSchemaTable.vue'
import { findComponentState } from '~/src/componentStateUtils'

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-list-group': BListGroup,
    'b-list-group-item': BListGroupItem,
    ComponentFilters,
    GraphQlSchemaTable,
  },
  props: {
    components: {
      type: Array as PropType<Component[]>,
      required: true,
    },
  },
  computed: {
    filteredComponents(): Component[] {
      return this.$store.state.componentFilters.filteredComponents
    },
    graphQlSchemas(): GraphQlSchema[] {
      return this.filteredComponents.flatMap((component) => {
        const graphQlSchemas: GraphQlSchemasState | undefined =
          findComponentState(component, 'graphql-schemas')
        return graphQlSchemas?.graphQlSchemas ?? []
      })
    },
    count(): number {
      return this.graphQlSchemas.length
    },
    countVariant(): string {
      return this.count > 0 ? 'success' : 'danger'
    },
    countClass(): string {
      return this.count > 0 ? '' : 'display-1'
    },
  },
})
</script>
