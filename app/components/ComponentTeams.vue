<template>
  <ul v-if="componentTeams && componentTeams.length > 0">
    <li
      v-for="(componentTeam, componentTeamIndex) in componentTeams"
      :key="componentTeamIndex"
    >
      <TeamName :team="{ id: componentTeam.teamId }" />
      <b-badge v-if="componentTeam.type === 'primary'" variant="info">
        Primary Team
      </b-badge>
      <b-badge v-else-if="componentTeam.type === 'previous'" variant="light">
        Previous Team
      </b-badge>
      <Markdown
        v-if="componentTeam.description"
        :markdown="componentTeam.description"
      />
    </li>
  </ul>
</template>

<style>
.team-name + .badge {
  margin-left: 0.6em;
}
</style>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BBadge } from 'bootstrap-vue'
import { ComponentTeam } from '~/types/kronicle-service'
import Markdown from '~/components/Markdown.vue'
import TeamName from '~/components/TeamName.vue'

export default Vue.extend({
  components: {
    'b-badge': BBadge,
    Markdown,
    TeamName,
  },
  props: {
    componentTeams: {
      type: Array as PropType<ComponentTeam[]>,
      default: undefined,
    },
  },
})
</script>
