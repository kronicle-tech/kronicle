<template>
  <div class="my-3">
    <b-button class="my-1" :to="{ name: 'all-components' }">
      Components
    </b-button>
    <b-button
      :to="{ name: 'all-components-tests', query: { testOutcome: 'fail' } }"
    >
      Tests
    </b-button>
    <b-button
      class="my-1"
      :to="{ name: 'all-components-cross-functional-requirements' }"
    >
      Cross Functional Requirements
    </b-button>
    <b-button class="my-1" :to="{ name: 'all-components-tech-debts' }">
      Tech Debts
    </b-button>
    <b-button class="my-1" :to="{ name: 'all-components-openapi-specs' }">
      OpenAPI Specs
    </b-button>
    <b-button class="my-1" :to="{ name: 'all-components-graphql-schemas' }">
      GraphQL Schemas
    </b-button>
    <b-button class="my-1" :to="{ name: 'all-components-key-software' }">
      Key Software
    </b-button>
    <b-button class="my-1" :to="{ name: 'all-components-repos' }">
      Repos
    </b-button>
    <b-button class="my-1" :to="{ name: 'all-components-errors' }">
      Errors
    </b-button>
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
