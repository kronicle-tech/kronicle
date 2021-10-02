<template>
  <b-alert v-if="message" show dismissible :variant="message.variant">
    <Markdown :markdown="message.markdown" />
  </b-alert>
</template>

<script lang="ts">
import Vue from 'vue'
import { BAlert } from 'bootstrap-vue'
import Markdown from '~/components/Markdown.vue'

interface Message {
  markdown: string
  variant?: string
}

export default Vue.extend({
  components: {
    'b-alert': BAlert,
    Markdown,
  },
  data() {
    return {
      message: this.$config.message as Message | undefined,
    }
  },
})
</script>
