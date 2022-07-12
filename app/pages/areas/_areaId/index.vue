<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ area.name }}</h1>

    <AreaTabs :area-id="area.id" />
    <b-card-group columns>
      <b-card title="Area Name">
        {{ area.name }}
      </b-card>

      <b-card v-if="area.description" title="Description">
        <Markdown :markdown="area.description" />
      </b-card>

      <b-card v-if="area.notes" title="Notes">
        <Markdown :markdown="area.notes" :toc="true" />
      </b-card>

      <b-card v-if="area.links && area.links.length > 0" title="Links">
        <Links :links="area.links" />
      </b-card>
    </b-card-group>

    <b-card-group>
      <b-card v-if="area.teams" title="Area's Teams">
        <TeamTable :teams="area.teams" />
      </b-card>
    </b-card-group>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { BCard, BCardGroup } from 'bootstrap-vue'
import { Area } from '~/types/kronicle-service'
import AreaTabs from '~/components/AreaTabs.vue'
import Links from '~/components/Links.vue'
import Markdown from '~/components/Markdown.vue'
import TeamTable from '~/components/TeamTable.vue'
import { NuxtError } from '~/src/nuxtError'

export default Vue.extend({
  components: {
    AreaTabs,
    'b-card': BCard,
    'b-card-group': BCardGroup,
    Links,
    Markdown,
    TeamTable,
  },
  async asyncData({ $config, route }) {
    const area = await fetch(
      `${$config.serviceBaseUrl}/v1/areas/${route.params.areaId}?fields=area(id,name,description,notes,links,teams)`
    )
      .then((res) => res.json())
      .then((json) => json.area as Area | undefined)

    if (!area) {
      throw new NuxtError('Area not found', 404)
    }

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
      title: `Kronicle - ${this.area.name}`,
    }
  },
})
</script>
