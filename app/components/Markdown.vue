<template>
  <!-- eslint-disable vue/no-v-html -->
  <div v-html="html" />
  <!-- eslint-enable -->
</template>

<script lang="ts">
import Vue from 'vue'
import remarkParse from 'remark-parse'
import remarkGfm from 'remark-gfm'
import remarkRehype from 'remark-rehype'
import remarkSlug from 'remark-slug'
import rehypeStringify from 'rehype-stringify'
import remarkToc from 'remark-toc'
import rehype from 'rehype'
import rehypeRaw from 'rehype-raw'
import rehypeSanitize from 'rehype-sanitize'
import deepmerge from 'deepmerge'
import unified from 'unified'
import vfile, { VFile } from 'vfile'

const remarkHighlightJs = require('remark-highlight.js')
const vFileReporter = require('vfile-reporter')
const rehypeSanitizeGitHubSchema = require('hast-util-sanitize/lib/github.json')

const rehypeSanitizeSchema = deepmerge(rehypeSanitizeGitHubSchema, {
  attributes: { '*': ['className'] },
})

function generateMarkdownHtml(markdown: String, toc: Boolean): VFile {
  let processor = unified().use(remarkParse).use(remarkGfm)
  if (toc) {
    processor = processor.use(remarkToc, {
      maxDepth: 3,
      prefix: 'user-content-',
    } as any)
    processor = processor.use(remarkSlug)
  }
  return processor
    .use(remarkHighlightJs)
    .use(remarkRehype, { allowDangerousHtml: true })
    .use(rehypeRaw)
    .use(rehypeStringify)
    .processSync(markdown)
}

function sanitizeHtml(html: VFile): VFile {
  return rehype()
    .data('settings', { fragment: true })
    .use(rehypeSanitize, rehypeSanitizeSchema)
    .processSync(html)
}

function generateReportHtml(input: VFile): VFile {
  const report = vFileReporter(input, { quiet: true, color: false })
  return sanitizeHtml(
    vfile(report.length > 0 ? createReportPreTagHtml(report) : '')
  )
}

function createReportPreTagHtml(report: String): String {
  return '<pre>\n' + report + '\n</pre>\n<br />\n'
}

export default Vue.extend({
  props: {
    markdown: {
      type: String,
      default: undefined,
    },
    toc: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    html(): string {
      if (!this.markdown) {
        return ''
      }
      let modifiedMarkdown = this.markdown
      if (this.toc) {
        modifiedMarkdown = '# Table of Contents\n\n' + modifiedMarkdown
      }
      try {
        const html = generateMarkdownHtml(modifiedMarkdown, this.toc)
        const sanitizedHtml = sanitizeHtml(html)
        const output =
          String(generateReportHtml(sanitizedHtml)) +
          '\n' +
          String(sanitizedHtml)
        return output
      } catch (e) {
        const text =
          typeof e === 'object' && e !== null && 'toString' in e
            ? e.toString()
            : 'Error: ' + JSON.stringify(e)
        return String(createReportPreTagHtml(text))
      }
    },
  },
})
</script>

<style scoped>
div >>> h1#user-content-table-of-contents {
  display: none;
}

div >>> p:last-child {
  margin-bottom: 0;
}
</style>
