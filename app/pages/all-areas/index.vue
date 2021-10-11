<template>
  <div>
    <b-container fluid>
      <b-row>
        <b-col>
          <AllAreasTabs />

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
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import {BAlert, BCol, BContainer, BRow} from 'bootstrap-vue'
import { Area } from '~/types/kronicle-service'
import AllAreasTabs from '~/components/AllAreasTabs.vue'
import AreaName from '~/components/AreaName.vue'
import Markdown from '~/components/Markdown.vue'

export default Vue.extend({
  components: {
    AllAreasTabs,
    'b-alert': BAlert,
    'b-col': BCol,
    'b-container': BContainer,
    'b-row': BRow,
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
