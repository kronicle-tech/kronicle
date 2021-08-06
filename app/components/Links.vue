<template>
  <ul v-if="links && links.length > 0">
    <li v-for="(link, linkIndex) in links" :key="linkIndex">
      <a :href="link.url" target="_blank">{{ link.url }}</a>
      <br />
      <Markdown v-if="link.description" :markdown="link.description" />
    </li>
  </ul>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { Link } from '~/types/component-catalog-service'
import Markdown from '~/components/Markdown.vue'

export default Vue.extend({
  components: {
    Markdown,
  },
  props: {
    links: {
      type: Array as PropType<Link[]>,
      default: undefined,
    },
  },
})
</script>
