<template>
  <a v-if="area" :href="`/areas/${area.id}`">{{
    area.name ? area.name : area.id
  }}</a>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { Area } from '~/types/component-catalog-service'

export default Vue.extend({
  props: {
    area: {
      type: Object as PropType<Area>,
      default: undefined,
    },
  },
})
</script>
