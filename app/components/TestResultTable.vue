<template>
  <table v-if="testResults && testResults.length > 0" class="table table-dark">
    <thead>
      <tr>
        <th>Component</th>
        <th>Test</th>
        <th>Teams</th>
        <th>Priority</th>
        <th>Outcome</th>
        <th>Message</th>
      </tr>
    </thead>
    <tbody>
      <tr
        v-for="testResult in testResults"
        :key="`${testResult.component.id}:${testResult.testId}`"
      >
        <td class="test-id">
          <TestId
            :area-id="areaId"
            :team-id="teamId"
            :test-id="testResult.testId"
          />
        </td>
        <td class="test-component table-primary">
          <ComponentName :component="testResult.component" />
        </td>
        <td>
          <ComponentTeams :component-teams="testResult.component.teams" />
        </td>
        <td class="test-priority">
          <b-badge :variant="testResult.priorityVariant">
            {{ testResult.priorityText }}
          </b-badge>
        </td>
        <td :class="`test-outcome table-${testResult.outcomeVariant}`">
          {{ testResult.outcomeText }}
        </td>
        <td class="test-message">
          <Markdown :markdown="testResult.message" />
        </td>
      </tr>
    </tbody>
  </table>
</template>

<style scoped>
.test-id {
  width: 15%;
}

.test-component {
  width: 15%;
}

.test-priority {
  width: 10%;
}

.test-outcome {
  width: 10%;
}
</style>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BBadge } from 'bootstrap-vue'
import { Component, TestResult } from '~/types/component-catalog-service'
import { getPriorityText, getPriorityVariant } from '~/src/priorityHelper'
import {
  getTestOutcomeText,
  getTestOutcomeVariant,
} from '~/src/testOutcomeHelper'
import { compareTestResults } from '~/src/testResultComparator'
import ComponentName from '~/components/ComponentName.vue'
import Markdown from '~/components/Markdown.vue'
import TestId from '~/components/TestId.vue'
import ComponentTeams from '~/components/ComponentTeams.vue'

interface TestResultWithComponent extends TestResult {
  component: Component
  priorityVariant: string
  priorityText: string
  outcomeVariant: string
  outcomeText: string
}

export default Vue.extend({
  components: {
    'b-badge': BBadge,
    ComponentName,
    ComponentTeams,
    Markdown,
    TestId,
  },
  props: {
    testId: {
      type: String,
      default: undefined,
    },
    areaId: {
      type: String,
      default: undefined,
    },
    teamId: {
      type: String,
      default: undefined,
    },
    components: {
      type: Array as PropType<Component[]>,
      required: true,
    },
  },
  computed: {
    testResults(): TestResultWithComponent[] {
      const that = this
      return that.components
        .flatMap((component) => {
          let testResults = component.testResults ?? []
          if (that.testId) {
            testResults = testResults.filter(
              (testResult) => testResult.testId === that.testId
            )
          }
          return testResults.map((testResult) => {
            return {
              ...testResult,
              component,
              priorityVariant: getPriorityVariant(testResult.priority),
              priorityText: getPriorityText(testResult.priority),
              outcomeVariant: getTestOutcomeVariant(testResult.outcome),
              outcomeText: getTestOutcomeText(testResult.outcome),
            }
          })
        })
        .sort(compareTestResults)
    },
  },
})
</script>
