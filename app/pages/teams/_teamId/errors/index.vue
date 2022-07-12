<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ team.name }} - Errors</h1>

    <TeamTabs :team-id="team.id" />

    <ScannerErrorsView :components="team.components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Team } from '~/types/kronicle-service'
import ScannerErrorsView from '~/components/ScannerErrorsView.vue'
import TeamTabs from '~/components/TeamTabs.vue'
import { NuxtError } from '~/src/nuxtError'

export default Vue.extend({
  components: {
    ScannerErrorsView,
    TeamTabs,
  },
  async asyncData({ $config, route, store }) {
    const team = await fetch(
      `${$config.serviceBaseUrl}/v1/teams/${route.params.teamId}?fields=team(id,name,components(id,name,type,tags,teams,platformId,scannerErrors))`
    )
      .then((res) => res.json())
      .then((json) => json.team as Team | undefined)

    if (!team) {
      throw new NuxtError('Team not found', 404)
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
      title: `Kronicle - ${this.team.name} - Errors`,
    }
  },
})
</script>
