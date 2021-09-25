<template>
  <!-- eslint-disable vue/no-v-html -->
  <div v-html="html" />
  <!-- eslint-enable -->
</template>

<script lang="ts">
import Vue from 'vue'
import deepmerge from 'deepmerge'
import remarkParse from 'remark-parse'
import remarkGfm from 'remark-gfm'
import remarkToc from 'remark-toc'
import rehypeParse from 'rehype-parse'
import rehypeSanitize from 'rehype-sanitize'
import rehypeStringify from 'rehype-stringify'
import {unified} from 'unified'
import vfile, {VFile} from 'vfile'

const remarkHighlightJs = require('remark-highlight.js')
const remarkLintPlugins = [
  require('remark-lint-hard-break-spaces'),
  require('remark-lint-no-duplicate-definitions'),
  require('remark-lint-no-heading-content-indent'),
  require('remark-lint-no-inline-padding'),
  require('remark-lint-no-shortcut-reference-image'),
  require('remark-lint-no-shortcut-reference-link'),
  require('remark-lint-no-undefined-references'),
  require('remark-lint-no-unused-definitions'),
]
const remarkSlug = require('remark-slug')
const vFileReporter = require('vfile-reporter')
const rehypeSanitizeGitHubSchema = require('hast-util-sanitize/lib/github.json')

const rehypeSanitizeSchema = deepmerge(rehypeSanitizeGitHubSchema, {
  attributes: { code: ['className'], span: ['className'] },
})

function generateSanitizedMarkdownHtml(markdown: String, toc: Boolean): VFile {
  let processor = unified()
    .use(remarkParse)
    .use(remarkLintPlugins)
    .use(remarkGfm)
  if (toc) {
    processor = processor.use(remarkToc, {
        maxDepth: 3,
        prefix: 'user-content-',
      } as any)
      .use(remarkSlug)
  }
  return sanitizeHtml(processor
    .use(remarkHighlightJs)
    .use(rehypeParse)
    .data('settings', { fragment: true })
    .use(rehypeSanitize, rehypeSanitizeSchema)
    .use(rehypeStringify)
    .processSync(markdown))
}

function sanitizeHtml(html: VFile): VFile {
  return unified()
    .use(rehypeParse)
    .data('settings', { fragment: true })
    .use(rehypeSanitize, rehypeSanitizeSchema)
    .use(rehypeStringify)
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
        modifiedMarkdown = '#### Table of Contents\n\n' + modifiedMarkdown
      }
      try {
        const sanitizedHtml = generateSanitizedMarkdownHtml(modifiedMarkdown, this.toc)
        return String(generateReportHtml(sanitizedHtml)) +
          '\n' +
          String(sanitizedHtml)
      } catch (e) {
        return String(createReportPreTagHtml(e.toString()))
      }
    },
  },
})
</script>
