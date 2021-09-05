<template>
  <table
    v-if="scannerErrors && scannerErrors.length > 0"
    class="table table-dark"
  >
    <thead>
      <tr>
        <th class="component table-primary">Component</th>
        <th class="scanner">Scanner</th>
        <th class="teams">Teams</th>
        <th class="error">Error</th>
      </tr>
    </thead>
    <tbody>
      <tr
        v-for="(scannerError, scannerErrorIndex) in scannerErrors"
        :key="scannerErrorIndex"
      >
        <td class="component table-primary">
          <ComponentName :component="scannerError.component" />
        </td>
        <td class="scanner">{{ scannerError.scannerId }}</td>
        <td class="teams">
          <ComponentTeams :component-teams="scannerError.component.teams" />
        </td>
        <td class="error">
          <ul>
            <li
              v-for="(errorEntry, errorEntryIndex) in scannerError.errorEntries"
              :key="errorEntryIndex"
            >
              {{ errorEntry }}
            </li>
          </ul>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { Component, ScannerError } from '~/types/kronicle-service'
import { compareScannerErrors } from '~/src/scannerErrorComparator'
import ComponentName from '~/components/ComponentName.vue'
import ComponentTeams from '~/components/ComponentTeams.vue'

interface ScannerErrorWithComponent extends ScannerError {
  component: Component
  errorEntries: string[]
}

export default Vue.extend({
  components: {
    ComponentName,
    ComponentTeams,
  },
  props: {
    components: {
      type: Array as PropType<Component[]>,
      required: true,
    },
  },
  computed: {
    scannerErrors(): ScannerErrorWithComponent[] {
      const that = this
      return that.components
        .flatMap((component) => {
          return (component.scannerErrors ?? []).map((scannerError) => {
            return {
              ...scannerError,
              component,
              errorEntries: that.getErrorEntries(scannerError),
            }
          })
        })
        .sort(compareScannerErrors)
    },
  },
  methods: {
    getErrorEntries(scannerError: ScannerError) {
      const errorEntries = []
      do {
        errorEntries.push(scannerError.message)
        scannerError = scannerError.cause
      } while (scannerError)
      return errorEntries
    },
  },
})
</script>
