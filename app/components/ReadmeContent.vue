<template>
  <div v-if="readme">
    <div v-if="readme.fileName.endsWith('.md')">
      <Markdown :markdown="readme.content" :toc="true" />
    </div>
    <div v-else>
      <pre>{{ readme ? readme.content : '' }}</pre>
    </div>
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { ReadmeState } from '~/types/kronicle-service'
import Markdown from '~/components/Markdown.vue'

export default Vue.extend({
  components: {
    Markdown,
  },
  props: {
    readme: {
      type: Object as PropType<ReadmeState>,
      default: undefined,
    },
  },
})
</script>
