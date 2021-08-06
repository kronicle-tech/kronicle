<template>
  <!-- eslint-disable vue/require-v-for-key -->
  <ul v-if="tags && tags.length > 0">
    <li v-for="tag in tags">{{ tag }}</li>
  </ul>
  <!-- eslint-enable -->
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'

export default Vue.extend({
  props: {
    tags: {
      type: Array as PropType<String[]>,
      default: undefined,
    },
  },
})
</script>
