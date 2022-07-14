<template>
  <div class="m-3">
    <h1 class="text-info my-3">
      {{ team.name }} - Cross Functional Requirements
    </h1>

    <TeamTabs :team-id="team.id" />

    <CrossFunctionalRequirementsView :components="team.components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Team } from '~/types/kronicle-service'
import CrossFunctionalRequirementsView from '~/components/CrossFunctionalRequirementsView.vue'
import TeamTabs from '~/components/TeamTabs.vue'

export default Vue.extend({
  components: {
    CrossFunctionalRequirementsView,
    TeamTabs,
  },
  async asyncData({ $config, route, store, error }) {
    const team = await fetch(
      `${$config.serviceBaseUrl}/v1/teams/${route.params.teamId}?fields=team(id,name,components(id,name,type,tags,teams,platformId,crossFunctionalRequirements))`
    )
      .then((res) => res.json())
      .then((json) => json.team as Team | undefined)

    if (!team) {
      error({
        message: 'Team not found',
        statusCode: 404,
      })
      return
    }

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
      title: `Kronicle - ${this.team.name} - Cross Functional Requirements`,
    }
  },
})
</script>
