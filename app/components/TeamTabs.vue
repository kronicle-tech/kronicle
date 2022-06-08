<template>
  <div class="my-3">
    <b-button class="my-1" :to="`/teams/${teamId}`">Info</b-button>
    <b-button class="my-1" :to="`/teams/${teamId}/dependencies`"
      >Visualizations</b-button
    >
    <b-button class="my-1" :to="`/teams/${teamId}/components`"
      >Components</b-button
    >
    <b-button
      class="my-1"
      :to="`/teams/${teamId}/cross-functional-requirements`"
    >
      Cross Functional Requirements
    </b-button>
    <b-button class="my-1" :to="`/teams/${teamId}/tech-debts`"
      >Tech Debts</b-button
    >
    <b-button class="my-1" :to="`/teams/${teamId}/tests`">Tests</b-button>
    <b-button class="my-1" :to="`/teams/${teamId}/errors`">Errors</b-button>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { BButton } from 'bootstrap-vue'

export default Vue.extend({
  components: {
    'b-button': BButton,
  },
  props: {
    teamId: {
      type: String,
      required: true,
    },
  },
})
</script>
