<template>
  <span>{{ formattedDate }}</span>
</template>

<script lang="ts">
import Vue from 'vue'

export default Vue.extend({
  props: {
    value: {
      type: String,
      default: undefined,
    },
  },
  computed: {
    formattedDate() {
      if (this.value === null || this.value === undefined) {
        return ''
      }

      const text = new Date(this.value).toISOString()
      return text.slice(0, 10)
    },
  },
})
</script>
