<template>
  <div class="my-3">
    <b-button :to="{ name: 'all-scanners' }">Scanners</b-button>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { BButton } from 'bootstrap-vue'

export default Vue.extend({
  components: {
    'b-button': BButton,
  },
})
</script>
