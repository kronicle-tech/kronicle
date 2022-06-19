<template>
  <div class="my-3">
    <b-button class="my-1" :to="`/components/${componentId}`"> Info </b-button>
    <b-button class="my-1" :to="`/components/${componentId}/diagrams`">
      Diagrams
    </b-button>
    <b-button
      v-if="componentAvailableData.hasNodes"
      class="my-1"
      :to="`/components/${componentId}/response-times`"
    >
      Response Times
    </b-button>
    <b-button
      v-if="componentAvailableData.hasNodes"
      class="my-1"
      :to="`/components/${componentId}/downstream-response-times`"
    >
      Downstream Response Times
    </b-button>
    <b-button
      v-if="componentAvailableData.hasCallGraphs"
      class="my-1"
      :to="`/components/${componentId}/call-graphs`"
    >
      Call Graphs
    </b-button>
    <b-button
      v-if="hasMetadataType('cross-functional-requirement')"
      class="my-1"
      :to="`/components/${componentId}/cross-functional-requirements`"
    >
      Cross Functional Requirements
    </b-button>
    <b-button
      v-if="hasMetadataType('tech-debt')"
      class="my-1"
      :to="`/components/${componentId}/tech-debts`"
    >
      Tech Debts
    </b-button>
    <b-button
      v-if="hasMetadataType('scanner-error')"
      class="my-1"
      :to="`/components/${componentId}/errors`"
    >
      Errors
    </b-button>
    <b-button
      v-if="hasStateType('openapi-specs')"
      class="my-1"
      :to="`/components/${componentId}/openapi-specs`"
    >
      OpenAPI
    </b-button>
    <b-button
      v-if="hasStateType('graphql-schemas')"
      class="my-1"
      :to="`/components/${componentId}/graphql-schemas`"
    >
      GraphQL
    </b-button>
    <b-button
      v-if="hasStateType('git-repo')"
      class="my-1"
      :to="`/components/${componentId}/repo`"
    >
      Repo
    </b-button>
    <b-button
      v-if="hasStateType('readme')"
      class="my-1"
      :to="`/components/${componentId}/readme`"
    >
      README
    </b-button>
    <b-button
      v-if="hasStateType('to-dos')"
      class="my-1"
      :to="`/components/${componentId}/to-dos`"
    >
      To Dos
    </b-button>
    <b-button
      v-if="hasStateType('lines-of-code')"
      class="my-1"
      :to="`/components/${componentId}/lines-of-code`"
    >
      Lines of Code
    </b-button>
    <b-button
      v-if="hasStateType('software-repositories')"
      class="my-1"
      :to="`/components/${componentId}/software-repositories`"
    >
      Software Repositories
    </b-button>
    <b-button
      v-if="hasStateType('softwares')"
      class="my-1"
      :to="`/components/${componentId}/software`"
    >
      Software
    </b-button>
    <b-button
      v-if="hasStateType('imports')"
      class="my-1"
      :to="`/components/${componentId}/imports`"
    >
      Imports
    </b-button>
    <b-button
      v-if="hasMetadataType('test-result')"
      class="my-1"
      :to="`/components/${componentId}/tests`"
    >
      Tests
    </b-button>
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BButton } from 'bootstrap-vue'
import { ComponentAvailableData } from '~/src/fetchComponentAvailableData'

export default Vue.extend({
  components: {
    'b-button': BButton,
  },
  props: {
    componentId: {
      type: String,
      required: true,
    },
    componentAvailableData: {
      type: Object as PropType<ComponentAvailableData>,
      required: true,
    },
  },
  methods: {
    hasMetadataType(metadataType: string): boolean {
      return this.componentAvailableData.metadataTypes.includes(metadataType)
    },
    hasStateType(stateType: string): boolean {
      return this.componentAvailableData.stateTypes.includes(stateType)
    },
  },
})
</script>
