<template>
  <div>
    <AllTestsTabs />
    <b-alert show="10" dismissible variant="info" class="my-3">
      Click a test's id in the table below to view more information about that
      test
    </b-alert>

    <table
      class="table table-dark table-bordered table-striped mt-2"
      style="width: 100%"
    >
      <thead>
        <tr>
          <th class="test-id">Id</th>
          <th>Description</th>
          <th>Priority</th>
          <th>Pass Count</th>
          <th>Fail Count</th>
          <th>Not Applicable Count</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="testSummary in testSummaries" :key="testSummary.id">
          <td>
            <TestId :test-id="testSummary.id" />
          </td>
          <td>
            <Markdown :markdown="testSummary.description" />
          </td>
          <td>
            {{ testSummary.priority }}
          </td>
          <td :class="testSummary.passCount > 0 ? 'table-primary' : ''">
            <FormattedNumber :value="testSummary.passCount" />
          </td>
          <td :class="testSummary.failCount > 0 ? 'table-danger' : ''">
            <FormattedNumber :value="testSummary.failCount" />
          </td>
          <td :class="testSummary.notApplicableCount > 0 ? 'table-light' : ''">
            <FormattedNumber :value="testSummary.notApplicableCount" />
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { BAlert } from 'bootstrap-vue'
import { MetaInfo } from 'vue-meta'
import { Component, Test, TestOutcome } from '~/types/kronicle-service'
import AllTestsTabs from '~/components/AllTestsTabs.vue'
import FormattedNumber from '~/components/FormattedNumber.vue'
import Markdown from '~/components/Markdown.vue'
import TestId from '~/components/TestId.vue'

interface TestSummary {
  id: string
  description: string
  passCount: number
  failCount: number
  notApplicableCount: number
}

export default Vue.extend({
  components: {
    AllTestsTabs,
    'b-alert': BAlert,
    FormattedNumber,
    Markdown,
    TestId,
  },
  async asyncData({ $config }) {
    const tests = await fetch(
      `${$config.serviceBaseUrl}/v1/tests?fields=tests(id,description,priority)`
    )
      .then((res) => res.json())
      .then((json) => json.tests as Test[])

    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(testResults)`
    )
      .then((res) => res.json())
      .then((json) => json.components as Component[])

    return {
      tests,
      components,
    }
  },
  data() {
    return {
      tests: [] as Test[],
      components: [] as Component[],
    }
  },
  computed: {
    testSummaries(): TestSummary[] {
      const outcomeCountMap = new Map<string, Map<string, number>>()
      this.components.forEach((component) => {
        component.testResults?.forEach((testResult) => {
          const optionalOutcomeCounts = outcomeCountMap.get(testResult.testId)
          let outcomeCounts

          if (!optionalOutcomeCounts) {
            outcomeCounts = new Map<string, number>()
            outcomeCountMap.set(testResult.testId, outcomeCounts)
          } else {
            outcomeCounts = optionalOutcomeCounts
          }

          const outcomeCount = outcomeCounts.get(testResult.outcome)
          outcomeCounts.set(
            testResult.outcome,
            outcomeCount ? outcomeCount + 1 : 1
          )
        })
      })

      return this.tests.map((test) => {
        return {
          id: test.id,
          description: test.description,
          priority: test.priority,
          passCount: getOutcomeCount(test.id, 'pass'),
          failCount: getOutcomeCount(test.id, 'fail'),
          notApplicableCount: getOutcomeCount(test.id, 'not-applicable'),
        } as TestSummary
      })

      function getOutcomeCount(testId: string, outcome: TestOutcome) {
        const outcomeCounts = outcomeCountMap.get(testId)

        if (outcomeCounts === undefined) {
          return undefined
        }

        return outcomeCounts.get(outcome)
      }
    },
  },
  head(): MetaInfo {
    return {
      title: 'Kronicle - All Tests',
    }
  },
})
</script>
