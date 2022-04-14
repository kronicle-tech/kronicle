<template>
  <div class="m-3">
    <h1 class="text-info my-3">
      {{ team.name }} Team - {{ $route.params.testId }} Test
    </h1>

    <TeamTabs :team-id="team.id" />

    <TestView :test="test" :components="team.components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Team, Test } from '~/types/kronicle-service'
import TestView from '~/components/TestView.vue'
import TeamTabs from '~/components/TeamTabs.vue'

export default Vue.extend({
  components: {
    TeamTabs,
    TestView,
  },
  async asyncData({ $config, route, store }) {
    const test = await fetch(
      `${$config.serviceBaseUrl}/v1/tests/${route.params.testId}?fields=test(id,description,priority)`
    )
      .then((res) => res.json())
      .then((json) => json.test as Test)

    const team = await fetch(
      `${$config.serviceBaseUrl}/v1/teams/${route.params.teamId}?fields=team(id,name,components(id,name,typeId,tags,teams,platformId,testResults))`
    )
      .then((res) => res.json())
      .then((json) => json.team as Team)

    store.commit('componentFilters/initialize', {
      components: team.components,
      route,
    })

    return {
      test,
      team,
    }
  },
  data() {
    return {
      test: {} as Test,
      team: {} as Team,
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.team.name} Team - ${this.$route.params.testId} Test`,
    }
  },
})
</script>
