<template>
  <div class="m-3">
    <AllTeamsTabs />

    <b-card-group v-for="(row, rowIndex) in rows" :key="rowIndex" deck>
      <b-card
        v-for="(item, itemIndex) in row"
        :key="itemIndex"
        bg-variant="dark"
        :header="item.team.name"
        text-variant="white"
        class="my-3"
      >
        <b-card
          v-for="itemPriority in item.priorities"
          :key="itemPriority.priority"
          no-body
          class="mt-1"
        >
          <b-card-header header-tag="header" class="p-1" role="tab">
            <b-button
              v-b-toggle="
          `accordion-${rowIndex}-${itemIndex}-${itemPriority.priority}`
        "
              block
              variant="info"
            >{{
                `${priorityName(
                  itemPriority.priority
                )} (${getItemPriorityTestResultCount(itemPriority)})`
              }}</b-button
            >
          </b-card-header>
          <b-collapse
            :id="`accordion-${rowIndex}-${itemIndex}-${itemPriority.priority}`"
            visible
            :accordion="`accordion-${rowIndex}-${itemIndex}`"
            role="tabpanel"
          >
            <b-card-body>
              <ComponentTestResultTable
                :components-and-test-results="
            itemPriority.componentsAndTestResults
          "
                :teams-visible="false"
              />
            </b-card-body>
          </b-collapse>
        </b-card>
      </b-card>
    </b-card-group>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import {
  BButton,
  BCard,
  BCardBody,
  BCardGroup,
  BCardHeader,
  BCollapse,
  VBToggle,
} from 'bootstrap-vue'
import { MetaInfo } from 'vue-meta'
import { Component, Team, Priority } from '~/types/kronicle-service'
import { ComponentAndTestResults } from '~/types/component-test-results'
import AllTeamsTabs from '~/components/AllTeamsTabs.vue'
import ComponentTestResultTable from '~/components/ComponentTestResultTable.vue'

interface ItemPriority {
  priority: Priority
  componentsAndTestResults: ComponentAndTestResults[]
}

interface Item {
  team: Team
  priorities: ItemPriority[]
}

export default Vue.extend({
  components: {
    AllTeamsTabs,
    'b-button': BButton,
    'b-card': BCard,
    'b-card-body': BCardBody,
    'b-card-group': BCardGroup,
    'b-card-header': BCardHeader,
    'b-collapse': BCollapse,
    ComponentTestResultTable,
  },
  directives: {
    'b-toggle': VBToggle,
  },
  async asyncData({ $config }) {
    const teams = await fetch(
      `${$config.serviceBaseUrl}/v1/teams?fields=teams(id,name)`
    )
      .then((res) => res.json())
      .then((json) => json.teams as Team[])

    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,name,teams,testResults)`
    )
      .then((res) => res.json())
      .then((json) => json.components as Component[])

    return {
      teams,
      components,
    }
  },
  data() {
    return {
      teams: [] as Team[],
      components: [] as Component[],
      priorities: [
        'very-high',
        'high',
        'medium',
        'low',
        undefined,
      ] as Priority[],
    }
  },
  head(): MetaInfo {
    return {
      title: 'Kronicle - All Teams - Tests',
    }
  },
  computed: {
    rows() {
      const that = this
      const rows = [] as Array<Array<Item>>
      const teamCount = this.teams.length
      let startIndex = 0

      while (startIndex < teamCount) {
        const endIndex = Math.min(startIndex + 2, teamCount)
        const row = that.teams.slice(startIndex, endIndex).map(
          (team) =>
            ({
              team,
              priorities: getPriorities(team),
            } as Item)
        )
        rows.push(row)
        startIndex = endIndex
      }
      return rows

      function getPriorities(team: Team) {
        return that.priorities
          .map((priority) => {
            return {
              priority,
              componentsAndTestResults: getComponentsAndTestResults(
                team,
                priority
              ),
            } as ItemPriority
          })
          .filter(
            (itemPriority) => itemPriority.componentsAndTestResults.length > 0
          )
      }

      function getComponentsAndTestResults(team: Team, priority: Priority) {
        return that.components
          .filter((component) => matchComponentTeam(component, team))
          .map((component) => {
            return {
              component,
              testResults: getComponentTestResults(component, priority),
            }
          })
          .filter(
            (componentAndTestResults) =>
              componentAndTestResults.testResults.length > 0
          )
      }

      function matchComponentTeam(component: Component, team: Team) {
        return component.teams.some(
          (componentTeam) =>
            componentTeam.type !== 'previous' &&
            componentTeam.teamId === team.id
        )
      }

      function getComponentTestResults(
        component: Component,
        priority: Priority
      ) {
        if (!component.testResults) {
          return []
        }

        return component.testResults.filter(
          (testResult) =>
            testResult.outcome === 'fail' && testResult.priority === priority
        )
      }
    },
  },
  methods: {
    priorityName(priority: Priority) {
      switch (priority) {
        case 'very-high':
          return 'Very High'
        case 'high':
          return 'High'
        case 'medium':
          return 'Medium'
        case 'low':
          return 'Low'
      }
    },
    getItemPriorityTestResultCount(itemPriority: ItemPriority) {
      return itemPriority.componentsAndTestResults
        .map(
          (componentAndTestResults) =>
            componentAndTestResults.testResults.length
        )
        .reduce((previousValue, currentValue) => previousValue + currentValue)
    },
  },
})
</script>
