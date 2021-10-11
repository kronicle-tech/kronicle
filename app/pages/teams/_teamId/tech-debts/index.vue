<template>
  <div>
    <b-container fluid>
      <b-row>
        <b-col>
          <h1 class="text-info my-3">{{ team.name }} Team - Tech Debts</h1>

          <TeamTabs :team-id="team.id" />
        </b-col>
      </b-row>
    </b-container>

    <TechDebtsView :components="team.components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import {BCol, BContainer, BRow} from "bootstrap-vue";
import { Team } from '~/types/kronicle-service'
import TeamTabs from '~/components/TeamTabs.vue'
import TechDebtsView from '~/components/TechDebtsView.vue'

export default Vue.extend({
  components: {
    'b-col': BCol,
    'b-container': BContainer,
    'b-row': BRow,
    TeamTabs,
    TechDebtsView,
  },
  async asyncData({ $config, route, store }) {
    const team = await fetch(
      `${$config.serviceBaseUrl}/v1/teams/${route.params.teamId}?fields=team(id,name,components(id,name,typeId,tags,teams,platformId,techDebts))`
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
      title: `Kronicle - ${this.team.name} Team - Tech Debts`,
    }
  },
})
</script>
