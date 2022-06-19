<template>
  <a v-if="diagram" :href="`/diagrams/${diagram.id}`">{{
    diagram.name ? diagram.name : diagram.id
  }}</a>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { Diagram } from '~/types/kronicle-service'

export default Vue.extend({
  props: {
    diagram: {
      type: Object as PropType<Diagram>,
      default: undefined,
    },
  },
})
</script>
