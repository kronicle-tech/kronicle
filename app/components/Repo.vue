<template>
  <a v-if="repo" :href="repo.url" target="_blank">{{ repo.url }}</a>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { Repo } from '~/types/kronicle-service'

export default Vue.extend({
  props: {
    repo: {
      type: Object as PropType<Repo>,
      default: undefined,
    },
  },
})
</script>
