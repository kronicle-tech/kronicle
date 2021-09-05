<template>
  <div>
    <h1 class="text-info my-3">{{ team.name }} Team - Components</h1>
    <TeamTabs :team-id="team.id" />
    <ComponentsView :components="team.components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Team } from '~/types/kronicle-service'
import ComponentsView from '~/components/ComponentsView.vue'
import TeamTabs from '~/components/TeamTabs.vue'

export default Vue.extend({
  components: {
    ComponentsView,
    TeamTabs,
  },
  async asyncData({ $config, route, store }) {
    const team = await fetch(
      `${$config.serviceBaseUrl}/v1/teams/${route.params.teamId}?fields=team(id,name,components(id,name,typeId,description,tags,teams,platformId))`
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
      title: `Kronicle - ${this.team.name} Team - Components`,
    }
  },
})
</script>
