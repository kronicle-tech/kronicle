<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ team.name }} - Diagrams</h1>

    <TeamTabs :team-id="team.id" />

    <DiagramsView :diagrams="diagrams" :components="components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Diagram, Team } from '~/types/kronicle-service'
import TeamTabs from '~/components/TeamTabs.vue'
import DiagramsView from '~/components/DiagramsView.vue'

export default Vue.extend({
  components: {
    DiagramsView,
    TeamTabs,
  },
  async asyncData({ $config, route, store }) {
    const team = await fetch(
      `${$config.serviceBaseUrl}/v1/teams/${route.params.teamId}?fields=team(id,name,components(id,name,typeId,tags,description,notes,responsibilities,teams,platformId))`
    )
      .then((res) => res.json())
      .then((json) => json.team as Team)

    store.commit('componentFilters/initialize', {
      components: team.components,
      route,
    })

    const diagrams = await fetch(`${$config.serviceBaseUrl}/v1/diagrams`)
      .then((res) => res.json())
      .then((json) => json.diagrams)

    return {
      team,
      diagrams,
    }
  },
  data() {
    return {
      team: {} as Team,
      diagrams: [] as Diagram[],
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.team.name} - Diagrams`,
    }
  },
})
</script>
