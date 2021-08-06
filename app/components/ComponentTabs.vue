<template>
  <div class="my-3">
    <b-button :to="`/components/${componentId}`">Info</b-button>
    <b-button :to="`/components/${componentId}/dependencies`">
      Dependencies
    </b-button>
    <b-button :to="`/components/${componentId}/response-times`">
      Response Times
    </b-button>
    <b-button :to="`/components/${componentId}/downstream-response-times`">
      Downstream Response Times
    </b-button>
    <b-button :to="`/components/${componentId}/call-graphs`">
      Call Graphs
    </b-button>
    <b-button :to="`/components/${componentId}/cross-functional-requirements`">
      Cross Functional Requirements
    </b-button>
    <b-button :to="`/components/${componentId}/tech-debts`">
      Tech Debts
    </b-button>
    <b-button :to="`/components/${componentId}/tests`">Tests</b-button>
    <b-button :to="`/components/${componentId}/errors`">Errors</b-button>
    <b-button :to="`/components/${componentId}/openapi-specs`">
      OpenAPI Specs
    </b-button>
    <b-button :to="`/components/${componentId}/repo`">Repo</b-button>
    <b-button :to="`/components/${componentId}/readme`">README</b-button>
    <b-button :to="`/components/${componentId}/to-dos`">To Dos</b-button>
    <b-button :to="`/components/${componentId}/lines-of-code`">
      Lines of Code
    </b-button>
    <b-button :to="`/components/${componentId}/software-repositories`">
      Software Repositories
    </b-button>
    <b-button :to="`/components/${componentId}/software`">Software</b-button>
    <b-button :to="`/components/${componentId}/imports`">Imports</b-button>
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
