<template>
  <div v-if="version" class="mx-3 my-2 text-right">
    Kronicle App v{{ version }}
  </div>
</template>

<script lang="ts">
import Vue from 'vue'

export default Vue.extend({
  data() {
    return {
      version: this.$config.version,
    }
  },
})
</script>
