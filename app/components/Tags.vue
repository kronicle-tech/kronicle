<template>
  <ul v-if="tags && tags.length > 0">
    <li v-for="(tag, tagIndex) in tags"
        :key="tagIndex"
    >
      {{ tag.key }}<span v-if="tag.value !== undefined">: {{ tag.value }}</span>
    </li>
  </ul>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { Tag } from "~/types/kronicle-service";

export default Vue.extend({
  props: {
    tags: {
      type: Array as PropType<Tag[]>,
      default: undefined,
    },
  },
})
</script>
