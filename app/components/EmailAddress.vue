<template>
  <a v-if="emailAddress" :href="`mailto:${emailAddress}`">{{ emailAddress }}</a>
</template>

<script lang="ts">
import Vue from 'vue'

export default Vue.extend({
  props: {
    emailAddress: {
      type: String,
      default: undefined,
    },
  },
})
</script>
