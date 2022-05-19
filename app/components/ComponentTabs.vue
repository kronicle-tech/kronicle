<template>
  <div class="my-3">
    <b-button class="my-1" :to="`/components/${componentId}`">Info</b-button>
    <b-button class="my-1" :to="`/components/${componentId}/dependencies`">
      Visualizations
    </b-button>
    <b-button class="my-1" :to="`/components/${componentId}/response-times`">
      Response Times
    </b-button>
    <b-button class="my-1" :to="`/components/${componentId}/downstream-response-times`">
      Downstream Response Times
    </b-button>
    <b-button class="my-1" :to="`/components/${componentId}/call-graphs`">
      Call Graphs
    </b-button>
    <b-button class="my-1" :to="`/components/${componentId}/cross-functional-requirements`">
      Cross Functional Requirements
    </b-button>
    <b-button class="my-1" :to="`/components/${componentId}/tech-debts`">
      Tech Debts
    </b-button>
    <b-button class="my-1" :to="`/components/${componentId}/tests`">Tests</b-button>
    <b-button class="my-1" :to="`/components/${componentId}/errors`">Errors</b-button>
    <b-button class="my-1" :to="`/components/${componentId}/openapi-specs`">
      OpenAPI
    </b-button>
    <b-button class="my-1" :to="`/components/${componentId}/graphql-schemas`">
      GraphQL
    </b-button>
    <b-button class="my-1" :to="`/components/${componentId}/repo`">Repo</b-button>
    <b-button class="my-1" :to="`/components/${componentId}/readme`">README</b-button>
    <b-button class="my-1" :to="`/components/${componentId}/to-dos`">To Dos</b-button>
    <b-button class="my-1" :to="`/components/${componentId}/lines-of-code`">
      Lines of Code
    </b-button>
    <b-button class="my-1" :to="`/components/${componentId}/software-repositories`">
      Software Repositories
    </b-button>
    <b-button class="my-1" :to="`/components/${componentId}/software`">Software</b-button>
    <b-button class="my-1" :to="`/components/${componentId}/imports`">Imports</b-button>
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
    componentId: {
      type: String,
      required: true,
    },
  },
})
</script>
