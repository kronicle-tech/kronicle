<template>
  <div v-if="tags && tags.length > 0" class="h5">
    <b-badge
      v-for="(tag, tagIndex) in tags"
      :key="tagIndex"
      variant="info"
      class="mr-2 mb-2"
    >
      {{ tag.key }}<span v-if="tag.value !== undefined">: {{ tag.value }}</span>
    </b-badge>
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BBadge } from 'bootstrap-vue'
import { Tag } from '~/types/kronicle-service'

export default Vue.extend({
  components: {
    'b-badge': BBadge,
  },
  props: {
    tags: {
      type: Array as PropType<Tag[]>,
      default: undefined,
    },
  },
})
</script>
