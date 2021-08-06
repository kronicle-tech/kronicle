<template>
  <table
    v-if="openApiSpecs && openApiSpecs.length > 0"
    class="table table-dark"
  >
    <thead>
      <tr>
        <th class="component">Component</th>
        <th class="teams">Teams</th>
        <th class="link">Link</th>
        <th class="description">Description</th>
      </tr>
    </thead>
    <tbody>
      <tr
        v-for="(openApiSpec, openApiSpecIndex) in openApiSpecs"
        :key="openApiSpecIndex"
      >
        <td class="component table-primary">
          <ComponentName :component="openApiSpec.component" />
        </td>
        <td class="teams table-primary">
          <ComponentTeams :component-teams="openApiSpec.component.teams" />
        </td>
        <td class="link">
          <b-link
            v-if="openApiSpec.spec"
            :href="`/components/${openApiSpec.component.id}/openapi-specs/${
              openApiSpec.index + 1
            }`"
          >
            {{ openApiSpec.url ? openApiSpec.url : openApiSpec.file }}
          </b-link>
          <div v-else>
            {{ openApiSpec.url ? openApiSpec.url : openApiSpec.file }}
            <b-badge variant="danger">Missing</b-badge>
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
import { Component, OpenApiSpec } from '~/types/component-catalog-service'
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
