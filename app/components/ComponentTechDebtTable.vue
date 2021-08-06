<template>
  <table
    v-if="componentsAndTechDebts && componentsAndTechDebts.length > 0"
    class="table table-dark table-bordered table-striped mt-2"
    style="width: 100%"
  >
    <thead>
      <tr>
        <th>Name</th>
        <th>Tech Debts</th>
      </tr>
    </thead>
    <tbody>
      <tr
        v-for="componentAndTechDebts in componentsAndTechDebts"
        :key="componentAndTechDebts.component.id"
      >
        <td class="component-name table-primary">
          <ComponentName :component="componentAndTechDebts.component" />
        </td>
        <td>
          <TechDebts :tech-debts="componentAndTechDebts.techDebts" />
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import ComponentName from '~/components/ComponentName.vue'
import TechDebts from '~/components/TechDebts.vue'
import { ComponentAndTechDebts } from '~/types/component-tech-debts'

export default Vue.extend({
  components: {
    ComponentName,
    TechDebts,
  },
  props: {
    componentsAndTechDebts: {
      type: Array as PropType<ComponentAndTechDebts[]>,
      default: undefined,
    },
  },
})
</script>
