<template>
  <!-- eslint-disable vue/require-v-for-key -->
  <ul v-if="responsibilities && responsibilities.length > 0">
    <li v-for="responsibility in responsibilities">
      <Markdown :markdown="responsibility.description" />
    </li>
  </ul>
  <!-- eslint-enable -->
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { Responsibility } from '~/types/component-catalog-service'
import Markdown from '~/components/Markdown.vue'

export default Vue.extend({
  components: {
    Markdown,
  },
  props: {
    responsibilities: {
      type: Array as PropType<Responsibility[]>,
      default: undefined,
    },
  },
})
</script>
