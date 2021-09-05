<template>
  <div>
    <b-card title="Description" class="my-3">
      <Markdown :markdown="test.description" />
    </b-card>

    <b-card title="Notes" class="my-3">
      <Markdown :markdown="test.notes" />
    </b-card>

    <b-card title="Priority" class="my-3">
      {{ priorityText }}
    </b-card>

    <TestResultsView :test-id="test.id" :components="components" />
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BCard } from 'bootstrap-vue'
import { Component, Test } from '~/types/kronicle-service'
import { getPriorityText } from '~/src/priorityHelper'
import Markdown from '~/components/Markdown.vue'
import TestResultsView from '~/components/TestResultsView.vue'

export default Vue.extend({
  components: {
    'b-card': BCard,
    Markdown,
    TestResultsView,
  },
  props: {
    test: {
      type: Object as PropType<Test>,
      required: true,
    },
    components: {
      type: Array as PropType<Component[]>,
      required: true,
    },
  },
  computed: {
    priorityText(): string {
      return getPriorityText(this.test.priority)
    },
  },
})
</script>
