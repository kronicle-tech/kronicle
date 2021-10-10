<template>
  <ul v-if="tags && tags.length > 0">
    <li v-for="(tag, tagIndex) in tags"
        :key="tagIndex"
    >
      {{ tag }}
    </li>
  </ul>
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
