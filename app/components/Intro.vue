<template>
  <b-jumbotron :header="effectiveTitle" class="my-3">
    <Markdown :markdown="effectiveMarkdown" />
  </b-jumbotron>
</template>

<script lang="ts">
import Vue from 'vue'
import { BJumbotron } from 'bootstrap-vue'
import Markdown from '~/components/Markdown.vue'

export default Vue.extend({
  components: {
    'b-jumbotron': BJumbotron,
    Markdown,
  },
  props: {
    title: {
      type: String,
      default: undefined,
    },
    markdown: {
      type: String,
      default: undefined,
    },
  },
  data() {
    return {
      effectiveTitle: this.title || 'Kronicle',
      effectiveMarkdown: this.markdown
        || 'Kronicle contains information on the organisation\'s components, teams and areas.',
    }
  },
})
</script>
