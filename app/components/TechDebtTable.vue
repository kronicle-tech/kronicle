<template>
  <table v-if="techDebts && techDebts.length > 0" class="table table-dark">
    <thead>
      <tr>
        <th class="component">Component</th>
        <th class="priority">Priority</th>
        <th class="teams">Teams</th>
        <th class="description">Description</th>
        <th class="notes">Notes</th>
        <th class="links">Links</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="(techDebt, techDebtIndex) in techDebts" :key="techDebtIndex">
        <td class="component table-primary">
          <ComponentName :component="techDebt.component" />
        </td>
        <td class="priority">
          <b-badge :variant="techDebt.priorityVariant">
            {{ techDebt.priorityText }}
          </b-badge>
        </td>
        <td class="teams">
          <ComponentTeams :component-teams="techDebt.component.teams" />
        </td>
        <td class="description">
          <Markdown :markdown="techDebt.description" />
        </td>
        <td class="notes">
          <Markdown :markdown="techDebt.notes" />
        </td>
        <td class="links">
          <Links :links="techDebt.links" />
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BBadge } from 'bootstrap-vue'
import { Component, TechDebt } from '~/types/component-catalog-service'
import { getPriorityText, getPriorityVariant } from '~/src/priorityHelper'
import ComponentName from '~/components/ComponentName.vue'
import Links from '~/components/Links.vue'
import Markdown from '~/components/Markdown.vue'
import ComponentTeams from '~/components/ComponentTeams.vue'
import { compareObjectsWithPriorities } from '~/src/objectWithPriorityComparator'

interface TechDebtWithComponent extends TechDebt {
  component: Component
  priorityVariant: string
  priorityText: string
}

export default Vue.extend({
  components: {
    'b-badge': BBadge,
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
    techDebts(): TechDebtWithComponent[] {
      const that = this
      return that.components
        .flatMap((component) => {
          return (component.techDebts ?? []).map((techDebt) => {
            return {
              ...techDebt,
              component,
              priorityVariant: getPriorityVariant(techDebt.priority),
              priorityText: getPriorityText(techDebt.priority),
            }
          })
        })
        .sort(compareObjectsWithPriorities)
    },
  },
})
</script>
