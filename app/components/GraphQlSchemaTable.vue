<template>
  <table
    v-if="graphQlSchemas && graphQlSchemas.length > 0"
    class="table table-dark"
  >
    <thead>
      <tr>
        <th class="component">Component</th>
        <th class="teams">Teams</th>
        <th class="location">Location</th>
        <th class="action">Action</th>
        <th class="description">Description</th>
      </tr>
    </thead>
    <tbody>
      <tr
        v-for="(graphQlSchema, graphQlSchemaIndex) in graphQlSchemas"
        :key="graphQlSchemaIndex"
      >
        <td class="component">
          <ComponentName :component="graphQlSchema.component" />
        </td>
        <td class="teams">
          <ComponentTeams :component-teams="graphQlSchema.component.teams" />
        </td>
        <td class="location">
          {{ graphQlSchema.url ? graphQlSchema.url : graphQlSchema.file }}
        </td>
        <td class="action table-secondary">
          <b-link
            v-if="graphQlSchema.schema"
            :href="`/components/${graphQlSchema.component.id}/graphql-schemas/${
              graphQlSchema.index + 1
            }`"
            variant="primary"
          >
            View GraphQL schema
          </b-link>
          <div v-else>
            <b-badge variant="danger">GraphQL schema not found</b-badge>
          </div>
        </td>
        <td class="description">
          <Markdown :markdown="graphQlSchema.description" />
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BBadge, BLink } from 'bootstrap-vue'
import { Component, GraphQlSchema } from '~/types/kronicle-service'
import { compareGraphQlSchemas } from '~/src/graphQlSchemaComparator'
import ComponentName from '~/components/ComponentName.vue'
import ComponentTeams from '~/components/ComponentTeams.vue'
import Markdown from '~/components/Markdown.vue'

interface GraphQlSchemaWithIndexAndComponent extends GraphQlSchema {
  index: number
  component: Component
}

export default Vue.extend({
  components: {
    'b-link': BLink,
    'b-badge': BBadge,
    ComponentName,
    ComponentTeams,
    Markdown,
  },
  props: {
    components: {
      type: Array as PropType<Component[]>,
      required: true,
    },
  },
  computed: {
    graphQlSchemas(): GraphQlSchemaWithIndexAndComponent[] {
      const that = this
      return that.components
        .flatMap((component) => {
          return (component.graphQlSchemas ?? []).map((graphQlSchema, index) => {
            return {
              ...graphQlSchema,
              index,
              component,
            } as GraphQlSchemaWithIndexAndComponent
          })
        })
        .sort(compareGraphQlSchemas)
    },
  },
})
</script>
