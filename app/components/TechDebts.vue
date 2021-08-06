<template>
  <ul v-if="techDebts && techDebts.length > 0">
    <li v-for="(techDebt, techDebtIndex) in techDebts" :key="techDebtIndex">
      <span v-if="techDebt.priority === 'very-high'">
        <b-badge variant="danger">Very High</b-badge>
      </span>
      <span v-else-if="techDebt.priority === 'high'">
        <b-badge variant="warning">High</b-badge>
      </span>
      <span v-else-if="techDebt.priority === 'medium'">
        <b-badge variant="info">Medium</b-badge>
      </span>
      <span v-else-if="techDebt.priority === 'low'">
        <b-badge variant="primary">Low</b-badge>
      </span>
      <span v-else>
        <b-badge variant="success">Missing Priority</b-badge>
      </span>
      <Markdown :markdown="techDebt.description" />
      <Markdown :markdown="techDebt.notes" />
      <Links :links="techDebt.links" />
    </li>
  </ul>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BBadge } from 'bootstrap-vue'
import { TechDebt } from '~/types/component-catalog-service'
import Links from '~/components/Links.vue'
import Markdown from '~/components/Markdown.vue'

export default Vue.extend({
  components: {
    'b-badge': BBadge,
    Links,
    Markdown,
  },
  props: {
    techDebts: {
      type: Array as PropType<TechDebt[]>,
      default: undefined,
    },
  },
})
</script>
