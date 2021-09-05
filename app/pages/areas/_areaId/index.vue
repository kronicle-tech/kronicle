<template>
  <div>
    <h1 class="text-info my-3">{{ area.name }} Area</h1>
    <AreaTabs :area-id="area.id" />

    <b-card v-if="area.areaId" title="Area" class="my-3">
      <AreaName :area="{ id: area.areaId }" />
    </b-card>

    <b-card
      v-if="area.links && area.links.length > 0"
      title="Links"
      class="my-3"
    >
      <Links :links="area.links" />
    </b-card>

    <b-card v-if="area.description" title="Description" class="my-3">
      <Markdown :markdown="area.description" />
    </b-card>

    <b-card v-if="area.emailAddress" title="Email Address" class="my-3">
      <EmailAddress :email-address="area.emailAddress" />
    </b-card>

    <b-card v-if="area.notes" title="Notes" class="my-3">
      <Markdown :markdown="area.notes" :toc="true" />
    </b-card>

    <b-card v-if="area.teams" title="Teams" class="my-3">
      <TeamTable :teams="area.teams" />
    </b-card>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { BCard } from 'bootstrap-vue'
import { Area } from '~/types/kronicle-service'
import AreaName from '~/components/AreaName.vue'
import AreaTabs from '~/components/AreaTabs.vue'
import EmailAddress from '~/components/EmailAddress.vue'
import Links from '~/components/Links.vue'
import Markdown from '~/components/Markdown.vue'
import TeamTable from '~/components/TeamTable.vue'

export default Vue.extend({
  components: {
    AreaName,
    AreaTabs,
    'b-card': BCard,
    EmailAddress,
    Links,
    Markdown,
    TeamTable,
  },
  async asyncData({ $config, route }) {
    const area = await fetch(
      `${$config.serviceBaseUrl}/v1/areas/${route.params.areaId}?fields=area(id,name,description,notes,links,teams)`
    )
      .then((res) => res.json())
      .then((json) => json.area as Area)

    return {
      area,
    }
  },
  data() {
    return {
      area: {} as Area,
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.area.name} Area`,
    }
  },
})
</script>
