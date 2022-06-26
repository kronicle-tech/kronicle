<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ team.name }} - Docs</h1>

    <TeamTabs :team-id="team.id" />

    <DocsView :components="team.components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Team } from '~/types/kronicle-service'
import DocsView from '~/components/DocsView.vue'
import TeamTabs from '~/components/TeamTabs.vue'

export default Vue.extend({
  components: {
    DocsView,
    TeamTabs,
  },
  async asyncData({ $config, route, store }) {
    const team = await fetch(
      `${$config.serviceBaseUrl}/v1/teams/${route.params.teamId}?stateType=doc&fields=team(id,name,components(id,name,typeId,tags,description,notes,responsibilities,teams,platformId,states))`
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
      title: `Kronicle - ${this.team.name} - Docs`,
    }
  },
})
</script>
