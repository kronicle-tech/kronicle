<template>
  <div>
    <b-container fluid>
      <b-row>
        <b-col>
          <AllTeamsTabs />

          <b-alert show="10" dismissible variant="info" class="my-3">
            Click a team's name in the table below to view more information about that
            team
          </b-alert>

          <table
            class="table table-dark table-bordered table-striped mt-2"
            style="width: 100%"
          >
            <thead>
              <tr>
                <th class="team-name">Name</th>
                <th>Description</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="team in teams" :key="team.id">
                <td class="team-name table-primary">
                  <TeamName :team="team" />
                </td>
                <td>
                  <Markdown :markdown="team.description" />
                </td>
              </tr>
            </tbody>
          </table>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import {BAlert, BCol, BContainer, BRow} from 'bootstrap-vue'
import { Team } from '~/types/kronicle-service'
import AllTeamsTabs from '~/components/AllTeamsTabs.vue'
import TeamName from '~/components/TeamName.vue'
import Markdown from '~/components/Markdown.vue'

export default Vue.extend({
  components: {
    AllTeamsTabs,
    'b-alert': BAlert,
    'b-col': BCol,
    'b-container': BContainer,
    'b-row': BRow,
    TeamName,
    Markdown,
  },
  async asyncData({ $config }) {
    const teams = await fetch(
      `${$config.serviceBaseUrl}/v1/teams?fields=teams(id,name,emailAddress,description)`
    )
      .then((res) => res.json())
      .then((json) => json.teams as Team[])

    return {
      teams,
    }
  },
  data() {
    return {
      teams: [] as Team[],
    }
  },
  head(): MetaInfo {
    return {
      title: 'Kronicle - All Teams',
    }
  },
})
</script>
