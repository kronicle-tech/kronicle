<template>
  <!-- eslint-disable vue/require-v-for-key -->
  <ul v-if="formattedScannerErrors && formattedScannerErrors.length > 0">
    <li v-for="scannerError in formattedScannerErrors">{{ scannerError }}</li>
  </ul>
  <!-- eslint-enable -->
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { ScannerError } from '~/types/kronicle-service'

function formatScannerError(value: ScannerError) {
  let text = value.message
  if (value.cause) {
    text += ' | ' + formatScannerError(value.cause)
  }
  return text
}

export default Vue.extend({
  props: {
    scannerErrors: {
      type: Array as PropType<ScannerError[]>,
      default: undefined,
    },
  },
  computed: {
    formattedScannerErrors() {
      if (!this.scannerErrors) {
        return null
      }

      return this.scannerErrors.map(formatScannerError)
    },
  },
})
</script>
