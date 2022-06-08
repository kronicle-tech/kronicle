<template>
  <table v-if="testResults && testResults.length > 0" class="table table-dark">
    <thead>
      <tr>
        <th>Test</th>
        <th>Priority</th>
        <th>Outcome</th>
        <th>Message</th>
      </tr>
    </thead>
    <tbody>
      <tr
        v-for="(testResult, testResultIndex) in sortedTestResults"
        :key="testResultIndex"
      >
        <td class="test-id">
          <TestId :test-id="testResult.testId" />
        </td>
        <td class="test-priority">
          {{ testResult.priority }}
        </td>
        <td
          :class="`test-outcome ${
            testResult.outcome === 'pass'
              ? 'table-primary'
              : testResult.outcome === 'fail'
              ? 'table-danger'
              : 'table-light'
          }`"
        >
          {{ testResult.outcome }}
        </td>
        <td class="test-message">
          <Markdown v-if="testResult.message" :markdown="testResult.message" />
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { TestResult } from '~/types/kronicle-service'
import Markdown from '~/components/Markdown.vue'
import TestId from '~/components/TestId.vue'
import { compareTestResults } from '~/src/testResultComparator'

export default Vue.extend({
  components: {
    Markdown,
    TestId,
  },
  props: {
    testResults: {
      type: Array as PropType<TestResult[]>,
      default: undefined,
    },
  },
  computed: {
    sortedTestResults() {
      return [...this.testResults].sort(compareTestResults)
    },
  },
})
</script>

<style scoped>
.test-id {
  width: 30%;
}

.test-priority {
  width: 10%;
}

.test-outcome {
  width: 10%;
}
</style>
