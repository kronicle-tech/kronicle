<template>
  <a v-if="team" class="team-name" :href="`/teams/${team.id}`">{{
    team.name ? team.name : team.id
  }}</a>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { Team } from '~/types/kronicle-service'

export default Vue.extend({
  props: {
    team: {
      type: Object as PropType<Team>,
      default: undefined,
    },
  },
})
</script>
