<template>
  <div class="m-3">
    <table
      class="table table-dark table-bordered table-striped mt-2"
      style="width: 100%"
    >
      <thead>
        <tr>
          <th class="scanner-id">Id</th>
          <th class="scanner-description">Description</th>
          <th class="scanner-notes">Notes</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="scanner in scanners" :key="scanner.id">
          <td class="scanner-id">
            {{ scanner.id }}
          </td>
          <td class="scanner-description">
            <Markdown :markdown="scanner.description" />
          </td>
          <td class="scanner-notes">
            <Markdown :markdown="scanner.notes" />
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Scanner } from '~/types/kronicle-service'
import Markdown from '~/components/Markdown.vue'

export default Vue.extend({
  components: {
    Markdown,
  },
  async asyncData({ $config }) {
    const scanners = await fetch(
      `${$config.serviceBaseUrl}/v1/scanners?fields=scanners(id,description,notes)`
    )
      .then((res) => res.json())
      .then((json) => json.scanners as Scanner[])

    return {
      scanners,
    }
  },
  data() {
    return {
      scanners: [] as Scanner[],
    }
  },
  head(): MetaInfo {
    return {
      title: 'Kronicle - All Scanners',
    }
  },
})
</script>
