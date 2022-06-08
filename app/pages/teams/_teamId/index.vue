<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ team.name }} Team</h1>

    <TeamTabs :team-id="team.id" />

    <b-card-group columns>
      <b-card title="Team Name">
        {{ team.name }}
      </b-card>

      <b-card v-if="team.areaId" title="Area">
        <AreaName :area="{ id: team.areaId }" />
      </b-card>

      <b-card v-if="team.links && team.links.length > 0" title="Links">
        <Links :links="team.links" />
      </b-card>

      <b-card v-if="team.description" title="Description">
        <Markdown :markdown="team.description" />
      </b-card>

      <b-card v-if="team.notes" title="Notes">
        <Markdown :markdown="team.notes" :toc="true" />
      </b-card>

      <b-card v-if="team.emailAddress" title="Email Address">
        <EmailAddress :email-address="team.emailAddress" />
      </b-card>
    </b-card-group>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { BCard, BCardGroup } from 'bootstrap-vue'
import { Team } from '~/types/kronicle-service'
import AreaName from '~/components/AreaName.vue'
import EmailAddress from '~/components/EmailAddress.vue'
import Links from '~/components/Links.vue'
import Markdown from '~/components/Markdown.vue'
import TeamTabs from '~/components/TeamTabs.vue'

export default Vue.extend({
  components: {
    AreaName,
    'b-card': BCard,
    'b-card-group': BCardGroup,
    EmailAddress,
    Links,
    Markdown,
    TeamTabs,
  },
  async asyncData({ $config, route }) {
    const team = await fetch(
      `${$config.serviceBaseUrl}/v1/teams/${route.params.teamId}?fields=team(id,areaId,name,emailAddress,description,notes,links)`
    )
      .then((res) => res.json())
      .then((json) => json.team as Team)

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
      title: `Kronicle - ${this.team.name} Team`,
    }
  },
})
</script>
