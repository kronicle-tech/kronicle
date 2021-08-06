<template>
  <span>{{ formattedNumber }}</span>
</template>

<script lang="ts">
import Vue from 'vue'

const numberFormat = new Intl.NumberFormat()

export default Vue.extend({
  props: {
    value: {
      type: Number,
      default: undefined,
    },
  },
  computed: {
    formattedNumber() {
      if (this.value === null || this.value === undefined) {
        return ''
      }

      return numberFormat.format(this.value)
    },
  },
})
</script>
