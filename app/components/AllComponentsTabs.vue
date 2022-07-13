<template>
  <div class="my-3">
    <b-button class="my-1" to="/components"> Components </b-button>
    <b-button to="/all-components/tests?testOutcome=fail"> Tests </b-button>
    <b-button class="my-1" to="/all-components/cross-functional-requirements">
      Cross Functional Requirements
    </b-button>
    <b-button class="my-1" to="/all-components/tech-debts">
      Tech Debts
    </b-button>
    <b-button class="my-1" to="/all-components/key-software">
      Key Software
    </b-button>
    <b-button class="my-1" to="/all-components/repos"> Repos </b-button>
    <b-button class="my-1" to="/all-components/errors"> Errors </b-button>
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
