<template>
  <div>
    <b-container fluid>
      <b-row>
        <b-col>
          <h1 class="text-info my-3">
            {{ team.name }} Team - Cross Functional Requirements
          </h1>

          <TeamTabs :team-id="team.id" />
        </b-col>
      </b-row>
    </b-container>

    <CrossFunctionalRequirementsView :components="team.components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import {BCol, BContainer, BRow} from "bootstrap-vue";
import { Team } from '~/types/kronicle-service'
import CrossFunctionalRequirementsView from '~/components/CrossFunctionalRequirementsView.vue'
import TeamTabs from '~/components/TeamTabs.vue'

export default Vue.extend({
  components: {
    'b-col': BCol,
    'b-container': BContainer,
    'b-row': BRow,
    CrossFunctionalRequirementsView,
    TeamTabs,
  },
  async asyncData({ $config, route, store }) {
    const team = await fetch(
      `${$config.serviceBaseUrl}/v1/teams/${route.params.teamId}?fields=team(id,name,components(id,name,typeId,tags,teams,platformId,crossFunctionalRequirements))`
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
      title: `Kronicle - ${this.team.name} Team - Cross Functional Requirements`,
    }
  },
})
</script>
