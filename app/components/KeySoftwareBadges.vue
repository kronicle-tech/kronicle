<template>
  <div v-if="items && items.length > 0"
       class="h5"
  >
    <b-badge v-for="(item, itemIndex) in items"
             :key="itemIndex"
             variant="info"
             class="mr-2 mb-2"
    >
      {{ item.name }} {{ item.versions }}
    </b-badge>
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { KeySoftware } from "~/types/kronicle-service";

interface Item {
  name: String
  versions: String
}

export default Vue.extend({
  props: {
    keySoftware: {
      type: Array as PropType<KeySoftware[]>,
      default: undefined,
    },
  },
  computed: {
    items(): Item[] {
      return (this.keySoftware || [] as KeySoftware[])
        .map(entry => ({
          name: entry.name,
          versions: entry.versions.sort().join(', '),
        }) as Item)
    }
  }
})
</script>
