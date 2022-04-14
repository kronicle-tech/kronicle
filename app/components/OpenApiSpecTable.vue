<template>
  <table
    v-if="openApiSpecs && openApiSpecs.length > 0"
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
        v-for="(openApiSpec, openApiSpecIndex) in openApiSpecs"
        :key="openApiSpecIndex"
      >
        <td class="component">
          <ComponentName :component="openApiSpec.component" />
        </td>
        <td class="teams">
          <ComponentTeams :component-teams="openApiSpec.component.teams" />
        </td>
        <td class="location">
          {{ openApiSpec.url ? openApiSpec.url : openApiSpec.file }}
        </td>
        <td class="action table-secondary">
          <b-link
            v-if="openApiSpec.spec"
            :href="`/components/${openApiSpec.component.id}/openapi-specs/${
              openApiSpec.index + 1
            }`"
            variant="primary"
          >
            View OpenAPI spec
          </b-link>
          <div v-else>
            <b-badge variant="danger">OpenApi spec not found</b-badge>
          </div>
        </td>
        <td class="description">
          <Markdown :markdown="openApiSpec.description" />
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BBadge, BLink } from 'bootstrap-vue'
import { Component, OpenApiSpec } from '~/types/kronicle-service'
import { compareOpenApiSpecs } from '~/src/openApiSpecComparator'
import ComponentName from '~/components/ComponentName.vue'
import ComponentTeams from '~/components/ComponentTeams.vue'
import Markdown from '~/components/Markdown.vue'

interface OpenApiSpecWithIndexAndComponent extends OpenApiSpec {
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
    openApiSpecs(): OpenApiSpecWithIndexAndComponent[] {
      const that = this
      return that.components
        .flatMap((component) => {
          return (component.openApiSpecs ?? []).map((openApiSpec, index) => {
            return {
              ...openApiSpec,
              index,
              component,
            } as OpenApiSpecWithIndexAndComponent
          })
        })
        .sort(compareOpenApiSpecs)
    },
  },
})
</script>
