<template>
  <div>
    <div class="main">
      <b-card no-body class="my-3">
        <b-list-group>
          <b-list-group-item
            v-if="outcomeCounts.length === 0"
            class="zero-count"
            variant="success"
          >
            <span class="display-1">0</span> tests
          </b-list-group-item>
          <b-list-group-item
            v-for="outcomeCount in outcomeCounts"
            :key="outcomeCount.outcome"
            :class="`${outcomeCount.outcome}-count`"
            :variant="outcomeCount.variant"
          >
            <span :class="outcomeCount.countClass">
              {{ outcomeCount.count }}
            </span>
            <b>{{ outcomeCount.text }}</b> test{{
              outcomeCount.count === 1 ? '' : 's'
            }}
          </b-list-group-item>
        </b-list-group>
      </b-card>

      <b-card :title="`${count} Test Results`" class="my-3">
        <b-card-text>
          <TestResultTable
            :test-id="testId"
            :area-id="areaId"
            :team-id="teamId"
            :components="filteredComponents"
          />
        </b-card-text>
      </b-card>
    </div>

    <div class="panel">
      <ComponentFilters
        :components="components"
        :test-outcomes-filter-enabled="testOutcomesFilterEnabled"
      />
    </div>
  </div>
</template>

<style scoped>
.main {
  float: left;
  width: calc(100% - 275px);
}

.panel {
  float: right;
  width: 250px;
}
</style>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BCard, BCardText, BListGroup, BListGroupItem } from 'bootstrap-vue'
import {
  Component,
  TestOutcome,
  TestResult,
} from '~/types/component-catalog-service'
import {
  getTestOutcomeCountClass,
  getTestOutcomeText,
  getTestOutcomeVariant,
} from '~/src/testOutcomeHelper'
import { compareTestOutcomes } from '~/src/testOutcomeComparator'
import ComponentFilters from '~/components/ComponentFilters.vue'
import TestResultTable from '~/components/TestResultTable.vue'

interface OutcomeCount {
  outcome: TestOutcome
  variant: string
  text: string
  countClass: string
  count: number
}

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-card-text': BCardText,
    'b-list-group': BListGroup,
    'b-list-group-item': BListGroupItem,
    ComponentFilters,
    TestResultTable,
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
    testOutcomesFilterEnabled: {
      type: Boolean,
      default: true,
    },
  },
  computed: {
    filteredComponents(): Component[] {
      return this.$store.state.componentFilters.filteredComponents
    },
    testResults(): TestResult[] {
      const that = this
      let testResults = this.filteredComponents.flatMap(
        (component) => component.testResults ?? []
      )
      if (that.testId) {
        testResults = testResults.filter(
          (testResult) => testResult.testId === that.testId
        )
      }
      return testResults
    },
    count(): number {
      return this.testResults.length
    },
    outcomeCounts(): OutcomeCount[] {
      const that = this
      const testResults = that.testResults
      const outcomeMap = testResults.reduce((accumulator, currentValue) => {
        accumulator.set(
          currentValue.outcome,
          (accumulator.get(currentValue.outcome) ?? 0) + 1
        )
        return accumulator
      }, new Map())
      return Array.from(outcomeMap, ([outcome, count]) => ({
        outcome,
        variant: getTestOutcomeVariant(outcome),
        text: getTestOutcomeText(outcome),
        countClass: getTestOutcomeCountClass(outcome),
        count,
      })).sort(compareTestOutcomes)
    },
  },
})
</script>
