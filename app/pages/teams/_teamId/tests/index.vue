<template>
  <div>
    <h1 class="text-info my-3">{{ team.name }} Team - Tests</h1>
    <TeamTabs :team-id="team.id" />
    <TestResultsView
      :team-id="team.id"
      :components="team.components"
      :test-outcomes-filter-enabled="false"
    />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Team } from '~/types/component-catalog-service'
import TeamTabs from '~/components/TeamTabs.vue'
import TestResultsView from '~/components/TestResultsView.vue'

export default Vue.extend({
  components: {
    TeamTabs,
    TestResultsView,
  },
  async asyncData({ $config, route, store }) {
    const team = await fetch(
      `${$config.serviceBaseUrl}/v1/teams/${route.params.teamId}?testOutcome=fail&fields=team(id,name,components(id,name,typeId,tags,teams,platformId,testResults))`
    )
      .then((res) => res.json())
      .then((json) => json.team as Team)

    store.commit('componentFilters/initialize', {
      components: team.components,
      route,
    })

    return {
      team,
    }
  },
  data() {
    return {
      team: {} as Team,
    }
  },
  head(): MetaInfo {
    return {
      title: `Component Catalog - ${this.team.name} Team - Tests`,
    }
  },
})
</script>
