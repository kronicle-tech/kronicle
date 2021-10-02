<template>
  <b-alert v-if="markdown" show dismissible :variant="variant">
    <Markdown :markdown="markdown" />
  </b-alert>
</template>

<script lang="ts">
import Vue from 'vue'
import { BAlert } from 'bootstrap-vue'
import Markdown from '~/components/Markdown.vue'

export default Vue.extend({
  components: {
    'b-alert': BAlert,
    Markdown,
  },
  data() {
    return {
      markdown: this.$config.messageMarkdown,
      variant: this.$config.messageVariant,
    }
  },
})
</script>
