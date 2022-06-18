<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ team.name }} - Visualizations</h1>

    <TeamTabs :team-id="team.id" />

    <ComponentDependenciesView
      :all-components="allComponents"
      :components="team.components"
      :diagrams="diagrams"
    />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import TeamTabs from '~/components/TeamTabs.vue'
import ComponentDependenciesView from '~/components/ComponentDependenciesView.vue'
import { Team, Component, Diagram } from '~/types/kronicle-service'

export default Vue.extend({
  components: {
    ComponentDependenciesView,
    TeamTabs,
  },
  async asyncData({ $config, route, store }) {
    const team = await fetch(
      `${$config.serviceBaseUrl}/v1/teams/${route.params.teamId}?fields=team(id,name,components(id,name,typeId,tags,description,notes,responsibilities,teams,platformId,states(environmentId,pluginId)))`
    )
      .then((res) => res.json())
      .then((json) => json.team as Team)

    store.commit('componentFilters/initialize', {
      components: team.components,
      route,
    })

    const allComponents = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,name,typeId,tags,description,notes,responsibilities,teams,platformId)`
    )
      .then((res) => res.json())
      .then((json) => json.components as Component[])

    const diagrams = await fetch(`${$config.serviceBaseUrl}/v1/diagrams`)
      .then((res) => res.json())
      .then((json) => json.diagrams as Diagram[])

    return {
      team,
      allComponents,
      diagrams,
    }
  },
  data() {
    return {
      team: {} as Team,
      allComponents: [] as Component[],
      diagrams: [] as Diagram[],
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.team.name} - Visualizations`,
    }
  },
})
</script>
