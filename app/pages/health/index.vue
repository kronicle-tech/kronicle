<template>
  <div></div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'

export default Vue.extend({
  layout: 'Health',
  head(): MetaInfo {
    return {
      title: 'Kronicle - Health',
    }
  },
})
</script>
