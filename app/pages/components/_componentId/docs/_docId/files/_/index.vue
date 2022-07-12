<template>
  <div v-if="render" class="m-3">
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
import { BBreadcrumb, BBreadcrumbItem } from 'bootstrap-vue'
import { promisify } from 'es6-promisify'
import { Component, DocFile, DocState } from '~/types/kronicle-service'
import {
  ComponentAvailableData,
  fetchComponentAvailableData,
} from '~/src/fetchComponentAvailableData'
import Markdown from '~/components/Markdown.vue'
import { findComponentState } from '~/src/componentStateUtils'
import ComponentTabs from '~/components/ComponentTabs.vue'

export default Vue.extend({
  components: {
    'b-breadcrumb': BBreadcrumb,
    'b-breadcrumb-item': BBreadcrumbItem,
    ComponentTabs,
    Markdown,
  },
  async asyncData({ $config, params, res, route, next }) {
    const componentAvailableData = await fetchComponentAvailableData(
      $config,
      route
    )

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${params.componentId}?stateType=doc&stateId=${params.docId}&fields=component(id,name,teams,states)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component | undefined)

    const doc: DocState | undefined = findComponentState(component, 'doc')

    if (!doc) {
      throw new NuxtError('Doc not found', 404)
    }

    const docFile = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${params.componentId}/docs/${
        params.docId
      }/files/file?docFilePath=${encodeURIComponent(
        params.pathMatch
      )}&fields=docFile`
    )
      .then((res) => res.json())
      .then((json) => json.docFile as DocFile)

    if (!docFile) {
      throw new NuxtError('Doc File not found', 404)
    }

    let render = true

    if (docFile.contentType === 'binary') {
      const resEnd = promisify<void, any>(res.end).bind(res)

      res.setHeader('Content-Type', docFile.mediaType)
      await resEnd(Buffer.from(docFile.content, 'base64'))
      render = false
      if (next) {
        next(false)
        return
      }
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
      render,
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
      render: true as boolean,
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
