<template>
  <a v-if="component" :href="`/components/${component.id}`">{{
    component.name ? component.name : component.id
  }}</a>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { Component } from '~/types/component-catalog-service'

export default Vue.extend({
  props: {
    component: {
      type: Object as PropType<Component>,
      default: undefined,
    },
  },
})
</script>
