<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ team.name }} - OpenAPI Specs</h1>

    <TeamTabs :team-id="team.id" />

    <OpenApiSpecsView :components="team.components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Team } from '~/types/kronicle-service'
import TeamTabs from '~/components/TeamTabs.vue'
import OpenApiSpecsView from '~/components/OpenApiSpecsView.vue'

export default Vue.extend({
  components: {
    TeamTabs,
    OpenApiSpecsView,
  },
  async asyncData({ $config, route, store }) {
    const team = await fetch(
      `${$config.serviceBaseUrl}/v1/teams/${route.params.teamId}?stateType=openapi-specs&fields=team(id,name,components(id,name,type,tags,teams,platformId,states))`
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
      title: `Kronicle - ${this.team.name} - OpenAPI Specs`,
    }
  },
})
</script>
