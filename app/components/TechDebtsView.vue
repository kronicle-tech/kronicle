<template>
  <div>
    <b-container fluid>
      <b-row>
        <b-col>
          <b-card no-body class="my-3">
            <b-list-group>
              <b-list-group-item
                v-if="priorityCounts.length === 0"
                variant="success"
              >
                <span class="display-1">0</span> tech debts
              </b-list-group-item>
              <b-list-group-item
                v-for="priorityCount in priorityCounts"
                :key="priorityCount.priority"
                :variant="priorityCount.priorityVariant"
              >
                <span :class="priorityCount.outcomeCountClass">{{
                  priorityCount.count
                }}</span>
                <b>{{ priorityCount.priorityText }}</b> tech debt{{
                  priorityCount.count === 1 ? '' : 's'
                }}
              </b-list-group-item>
            </b-list-group>
          </b-card>
          <b-card :title="`${count} Tech Debts`" class="my-3">
            <b-card-text>
              <TechDebtTable :components="filteredComponents" />
            </b-card-text>
          </b-card>
        </b-col>
        <b-col md="3">
          <ComponentFilters :components="components" />
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import {BCard, BCardText, BCol, BContainer, BListGroup, BListGroupItem, BRow} from 'bootstrap-vue'
import {
  Component,
  Priority,
  TechDebt,
} from '~/types/kronicle-service'
import {
  getPriorityCountClass,
  getPriorityText,
  getPriorityVariant,
} from '~/src/priorityHelper'
import { compareObjectsWithPriorities } from '~/src/objectWithPriorityComparator'
import ComponentFilters from '~/components/ComponentFilters.vue'
import TechDebtTable from '~/components/TechDebtTable.vue'

interface PriorityCount {
  priority: Priority
  count: number
}

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-card-text': BCardText,
    'b-col': BCol,
    'b-container': BContainer,
    'b-list-group': BListGroup,
    'b-list-group-item': BListGroupItem,
    'b-row': BRow,
    ComponentFilters,
    TechDebtTable,
  },
  props: {
    components: {
      type: Array as PropType<Component[]>,
      required: true,
    },
  },
  computed: {
    filteredComponents(): Component[] {
      return this.$store.state.componentFilters.filteredComponents
    },
    techDebts(): TechDebt[] {
      return this.filteredComponents.flatMap(
        (component) => component.techDebts ?? []
      )
    },
    count(): number {
      return this.techDebts.length
    },
    priorityCounts(): PriorityCount[] {
      const priorityMap = this.techDebts.reduce((accumulator, currentValue) => {
        accumulator.set(
          currentValue.priority,
          (accumulator.get(currentValue.priority) ?? 0) + 1
        )
        return accumulator
      }, new Map())
      return Array.from(priorityMap, ([priority, count]) => ({
        priority,
        priorityVariant: getPriorityVariant(priority),
        priorityText: getPriorityText(priority),
        outcomeCountClass: getPriorityCountClass(priority),
        count,
      })).sort(compareObjectsWithPriorities)
    },
  },
})
</script>
