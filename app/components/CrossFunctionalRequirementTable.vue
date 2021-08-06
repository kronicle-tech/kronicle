<template>
  <table
    v-if="crossFunctionalRequirements && crossFunctionalRequirements.length > 0"
    class="table table-dark"
  >
    <thead>
      <tr>
        <th class="component">Component</th>
        <th class="teams">Teams</th>
        <th class="description">Description</th>
        <th class="notes">Notes</th>
        <th class="links">Links</th>
      </tr>
    </thead>
    <tbody>
      <tr
        v-for="(
          crossFunctionalRequirement, crossFunctionalRequirementIndex
        ) in crossFunctionalRequirements"
        :key="crossFunctionalRequirementIndex"
      >
        <td class="component table-primary">
          <ComponentName :component="crossFunctionalRequirement.component" />
        </td>
        <td class="teams">
          <ComponentTeams
            :component-teams="crossFunctionalRequirement.component.teams"
          />
        </td>
        <td class="description">
          <Markdown :markdown="crossFunctionalRequirement.description" />
        </td>
        <td class="notes">
          <Markdown :markdown="crossFunctionalRequirement.notes" />
        </td>
        <td class="links">
          <Links :links="crossFunctionalRequirement.links" />
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import {
  Component,
  CrossFunctionalRequirement,
} from '~/types/component-catalog-service'
import ComponentName from '~/components/ComponentName.vue'
import Links from '~/components/Links.vue'
import Markdown from '~/components/Markdown.vue'
import ComponentTeams from '~/components/ComponentTeams.vue'
import { compareObjectsWithComponents } from '~/src/objectWithComponentComparator'

interface CrossFunctionalRequirementWithComponent
  extends CrossFunctionalRequirement {
  component: Component
}

export default Vue.extend({
  components: {
    ComponentName,
    ComponentTeams,
    Links,
    Markdown,
  },
  props: {
    components: {
      type: Array as PropType<Component[]>,
      required: true,
    },
  },
  computed: {
    crossFunctionalRequirements(): CrossFunctionalRequirementWithComponent[] {
      const that = this
      return that.components
        .flatMap((component) => {
          return (component.crossFunctionalRequirements ?? []).map(
            (crossFunctionalRequirement) => {
              return {
                ...crossFunctionalRequirement,
                component,
              }
            }
          )
        })
        .sort(compareObjectsWithComponents)
    },
  },
})
</script>
