<template>
  <span>{{ formattedBoolean }}</span>
</template>

<script lang="ts">
import Vue from 'vue'

export default Vue.extend({
  props: {
    value: {
      type: Boolean,
      default: undefined,
    },
  },
  computed: {
    formattedBoolean() {
      if (this.value === null || typeof this.value === 'undefined') {
        return ''
      }

      return this.value ? 'Yes' : 'No'
    },
  },
})
</script>
