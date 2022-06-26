<template>
  <div class="m-3">
    <h1 class="text-info my-3">
      {{ component.name }} - {{ doc.name }} - {{ docFile.path }}
    </h1>

    <ComponentTabs
      :component-id="component.id"
      :component-available-data="componentAvailableData"
    />

    <b-breadcrumb>
      <b-breadcrumb-item :to="`/components/${component.id}/docs`">
        Docs
      </b-breadcrumb-item>
      <b-breadcrumb-item :to="`/components/${component.id}/docs/${doc.id}`">
        {{ doc.name }}
      </b-breadcrumb-item>
    </b-breadcrumb>

    <div class="content p-3">
      <div v-if="content">{{ content }}</div>

      <Markdown v-if="markdown" :markdown="markdown" />
    </div>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { promisify } from 'es6-promisify'
import { Component, DocFile, DocState } from '~/types/kronicle-service'
import {
  ComponentAvailableData,
  fetchComponentAvailableData,
} from '~/src/fetchComponentAvailableData'
import { findComponentState } from '~/src/componentStateUtils'
import Markdown from '~/components/Markdown.vue'
import { NuxtError } from '~/src/nuxtError'

export default Vue.extend({
  components: {
    Markdown,
  },
  async asyncData({ $config, route, res }) {
    const componentAvailableData = await fetchComponentAvailableData(
      $config,
      route
    )

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?stateType=doc&stateId=${route.params.docId}&fields=component(id,name,teams,states)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component)

    const doc: DocState | undefined = findComponentState(component, 'doc')

    if (!doc) {
      throw new NuxtError('Doc not found', 404)
    }

    const docFile = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${
        route.params.componentId
      }/docs/${route.params.docId}/files/file?docFilePath=${encodeURIComponent(
        route.params.pathMatch
      )}&fields=docFile`
    )
      .then((res) => res.json())
      .then((json) => json.docFile as DocFile)

    if (!docFile) {
      throw new NuxtError('Doc File not found', 404)
    }

    if (docFile.contentType === 'Binary') {
      res.writeHead(200, { 'Content-Type': docFile.contentType })
      const responseWrite = promisify(res.write)
      await responseWrite(atob(docFile.content))
    }

    let content
    let markdown

    switch (docFile.mediaType) {
      case 'text/markdown':
        markdown = docFile.content
        break
      default:
        content = docFile.content
    }

    return {
      componentAvailableData,
      component,
      doc,
      docFile,
      content,
      markdown,
    }
  },
  data() {
    return {
      componentAvailableData: {} as ComponentAvailableData,
      component: {} as Component,
      doc: {} as DocState,
      docFile: {} as DocFile,
      markdown: '' as string,
      content: '' as string,
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.$route.params.componentId} - ${this.$route.params.docId}`,
    }
  },
})
</script>

<style scoped>
.content {
  background-color: white;
  color: black;
}
</style>
