<template>
  <div class="m-3">
    <b-alert show="10" dismissible variant="info" class="my-3">
      Click an area's name in the table below to view more information about
      that area
    </b-alert>

    <table
      class="table table-dark table-bordered table-striped mt-2"
      style="width: 100%"
    >
      <thead>
        <tr>
          <th class="area-name">Name</th>
          <th>Description</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="area in areas" :key="area.id">
          <td class="area-name table-primary">
            <AreaName :area="area" />
          </td>
          <td>
            <Markdown :markdown="area.description" />
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { BAlert } from 'bootstrap-vue'
import { Area } from '~/types/kronicle-service'
import AreaName from '~/components/AreaName.vue'
import Markdown from '~/components/Markdown.vue'

export default Vue.extend({
  components: {
    'b-alert': BAlert,
    AreaName,
    Markdown,
  },
  async asyncData({ $config }) {
    const areas = await fetch(
      `${$config.serviceBaseUrl}/v1/areas?fields=areas(id,name,description)`
    )
      .then((res) => res.json())
      .then((json) => json.areas as Area[])

    return {
      areas,
    }
  },
  data() {
    return {
      areas: [] as Area[],
    }
  },
  head(): MetaInfo {
    return {
      title: 'Kronicle - All Areas',
    }
  },
})
</script>
