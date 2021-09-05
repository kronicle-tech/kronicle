<template>
  <table v-if="teams && teams.length > 0" class="table table-dark">
    <thead>
      <tr>
        <th class="team-id">Id</th>
        <th class="team-name">Name</th>
        <th class="team-email-address">Email Address</th>
        <th class="team-area-id">Area Id</th>
        <th class="team-description">Description</th>
        <th class="team-links">Links</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="(team, teamIndex) in sortedTeams" :key="teamIndex">
        <td class="team-id">{{ team.id }}</td>
        <td class="team-name table-primary">
          <TeamName :team="team" />
        </td>
        <td class="team-email-address">{{ team.emailAddress }}</td>
        <td class="team-area-id">{{ team.areaId }}</td>
        <td class="team-description">
          <Markdown :markdown="team.description" />
        </td>
        <td class="team-links">
          <Links :links="team.links" />
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { Team } from '~/types/kronicle-service'
import Links from '~/components/Links.vue'
import Markdown from '~/components/Markdown.vue'
import TeamName from '~/components/TeamName.vue'
import { compareTeams } from '~/src/teamComparator'

export default Vue.extend({
  components: {
    Links,
    Markdown,
    TeamName,
  },
  props: {
    teams: {
      type: Array as PropType<Team[]>,
      default: undefined,
    },
  },
  computed: {
    sortedTeams() {
      return [...this.teams].sort(compareTeams)
    },
  },
})
</script>
