<template>
  <div class="my-3">
    <b-button class="my-1" :to="`/areas/${areaId}`"> Info </b-button>
    <b-button class="my-1" :to="`/areas/${areaId}/dependencies`">
      Visualizations
    </b-button>
    <b-button class="my-1" :to="`/areas/${areaId}/components`">
      Components
    </b-button>
    <b-button
      class="my-1"
      :to="`/areas/${areaId}/cross-functional-requirements`"
    >
      Cross Functional Requirements
    </b-button>
    <b-button class="my-1" :to="`/areas/${areaId}/tech-debts`">
      Tech Debts
    </b-button>
    <b-button class="my-1" :to="`/areas/${areaId}/tests`"> Tests </b-button>
    <b-button class="my-1" :to="`/areas/${areaId}/errors`"> Errors </b-button>
    <b-button class="my-1" :to="`/areas/${areaId}/openapi-specs`">
      OpenAPI Specs
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
  props: {
    areaId: {
      type: String,
      required: true,
    },
  },
})
</script>
