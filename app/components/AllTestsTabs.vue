<template>
  <div class="my-3">
    <b-button class="my-1" to="/all-tests">Tests</b-button>
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
