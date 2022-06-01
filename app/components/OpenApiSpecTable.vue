<template>
  <table
    v-if="openApiSpecs && openApiSpecs.length > 0"
    class="table table-dark"
  >
    <thead>
      <tr>
        <th class="component">Component</th>
        <th class="teams">Teams</th>
        <th class="action">Action</th>
        <th class="location">Location</th>
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
        <td class="action table-secondary">
          <b-button
            v-if="openApiSpec.spec"
            :href="`/components/${openApiSpec.component.id}/openapi-specs/${
              openApiSpec.index + 1
            }`"
            variant="info"
          >
            View OpenAPI spec
          </b-button>
          <div v-else>
            <b-badge variant="danger">OpenAPI spec not found</b-badge>
          </div>
        </td>
        <td class="location">
          {{ openApiSpec.url ? openApiSpec.url : openApiSpec.file }}
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
import {BBadge, BButton} from 'bootstrap-vue'
import {Component, OpenApiSpec, OpenApiSpecsState} from '~/types/kronicle-service'
import { compareOpenApiSpecs } from '~/src/openApiSpecComparator'
import ComponentName from '~/components/ComponentName.vue'
import ComponentTeams from '~/components/ComponentTeams.vue'
import Markdown from '~/components/Markdown.vue'
import {findComponentState} from "~/src/componentStateUtils";

interface OpenApiSpecWithIndexAndComponent extends OpenApiSpec {
  index: number
  component: Component
}

export default Vue.extend({
  components: {
    'b-badge': BBadge,
    'b-button': BButton,
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
          const openApiSpecs: OpenApiSpecsState | undefined = findComponentState(component, 'openapi-specs')
          return (openApiSpecs?.openApiSpecs ?? []).map((openApiSpec, index) => {
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
