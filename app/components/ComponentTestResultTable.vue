<template>
  <table
    v-if="componentsAndTestResults && componentsAndTestResults.length > 0"
    class="table table-dark table-bordered table-striped mt-2"
    style="width: 100%"
  >
    <thead>
      <tr>
        <th>Name</th>
        <th v-if="teamsVisible">Teams</th>
        <th>Test Results</th>
      </tr>
    </thead>
    <tbody>
      <tr
        v-for="componentAndTestResults in componentsAndTestResults"
        :key="componentAndTestResults.component.id"
      >
        <td class="component-name table-primary">
          <ComponentName :component="componentAndTestResults.component" />
        </td>
        <td v-if="teamsVisible">
          <ComponentTeams
            :component-teams="componentAndTestResults.component.teams"
          />
        </td>
        <td>
          <TestResults :test-results="componentAndTestResults.testResults" />
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import ComponentName from '~/components/ComponentName.vue'
import ComponentTeams from '~/components/ComponentTeams.vue'
import TestResults from '~/components/TestResults.vue'
import { ComponentAndTestResults } from '~/types/component-test-results'

export default Vue.extend({
  components: {
    ComponentName,
    ComponentTeams,
    TestResults,
  },
  props: {
    componentsAndTestResults: {
      type: Array as PropType<ComponentAndTestResults[]>,
      default: undefined,
    },
    teamsVisible: {
      type: Boolean,
      default: true,
    },
  },
})
</script>
