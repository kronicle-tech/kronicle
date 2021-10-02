<template>
  <b-jumbotron :header="intro.title" class="my-3">
    <Markdown :markdown="intro.markdown" />
  </b-jumbotron>
</template>

<script lang="ts">
import Vue from 'vue'
import { BJumbotron } from 'bootstrap-vue'
import Markdown from '~/components/Markdown.vue'

interface Intro {
  title: string
  markdown: string
}

export default Vue.extend({
  components: {
    'b-jumbotron': BJumbotron,
    Markdown,
  },
  data() {
    return {
      intro: {
        title: this.$config.intro.title || 'Kronicle',
        markdown: this.$config.intro.markdown
          || 'Kronicle contains information on the organisation\'s components, teams and areas.',
      } as Intro
    }
  },
})
</script>
