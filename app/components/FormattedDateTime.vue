<template>
  <span>{{ formattedDateTime }}</span>
</template>

<script lang="ts">
import Vue from 'vue'
import { DateTime } from 'luxon'

export default Vue.extend({
  props: {
    value: {
      type: String,
      default: undefined,
    },
  },
  computed: {
    formattedDateTime() {
      if (this.value === null || this.value === undefined) {
        return ''
      }

      // return this.value
      const text = DateTime.fromISO(this.value as string).toISO()

      return `${text.slice(0, 10)} ${text.slice(11, 16)} UTC`
    },
  },
})
</script>
