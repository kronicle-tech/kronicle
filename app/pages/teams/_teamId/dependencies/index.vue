<template>
  <div>
    <b-container fluid>
      <b-row>
        <b-col>
          <h1 class="text-info my-3">{{ team.name }} Team - Dependencies</h1>

          <TeamTabs :team-id="team.id" />
        </b-col>
      </b-row>
    </b-container>

    <ComponentDependenciesView
      :all-components="allComponents"
      :components="team.components"
      :component-dependencies="summary.componentDependencies"
      :sub-component-dependencies="summary.subComponentDependencies"
    />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import {BCol, BContainer, BRow} from "bootstrap-vue";
import TeamTabs from '~/components/TeamTabs.vue'
import ComponentDependenciesView from '~/components/ComponentDependenciesView.vue'
import { Team, Component, Summary } from '~/types/kronicle-service'

export default Vue.extend({
  components: {
    'b-col': BCol,
    'b-container': BContainer,
    'b-row': BRow,
    ComponentDependenciesView,
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

    const allComponents = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,name,typeId,tags,description,notes,responsibilities,teams,platformId)`
    )
      .then((res) => res.json())
      .then((json) => json.components as Component[])

    const summary = await fetch(
      `${$config.serviceBaseUrl}/v1/summary?fields=summary(componentDependencies,subComponentDependencies)`
    )
      .then((res) => res.json())
      .then((json) => json.summary as Summary)

    return {
      team,
      allComponents,
      summary,
    }
  },
  data() {
    return {
      team: {} as Team,
      allComponents: [] as Component[],
      summary: {} as Summary,
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.team.name} Team - Dependencies`,
    }
  },
})
</script>
