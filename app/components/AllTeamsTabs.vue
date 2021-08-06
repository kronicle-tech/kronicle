<template>
  <div class="my-3">
    <b-button to="/all-teams">Teams</b-button>
    <b-button to="/all-teams/components">Components</b-button>
    <b-button to="/all-teams/tech-debts">Tech Debts</b-button>
    <b-button to="/all-teams/tests">Tests</b-button>
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
