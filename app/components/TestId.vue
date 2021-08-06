<template>
  <a v-if="testId" :href="testUrl">{{ testId }}</a>
</template>

<script lang="ts">
import Vue from 'vue'

export default Vue.extend({
  props: {
    testId: {
      type: String,
      required: true,
    },
    areaId: {
      type: String,
      default: undefined,
    },
    teamId: {
      type: String,
      default: undefined,
    },
  },
  computed: {
    testUrl(): string {
      if (this.areaId) {
        return `/areas/${this.areaId}/tests/${this.testId}`
      } else if (this.teamId) {
        return `/teams/${this.teamId}/tests/${this.testId}`
      } else {
        return `/tests/${this.testId}`
      }
    },
  },
})
</script>
