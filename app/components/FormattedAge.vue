<template>
  <span>{{ formattedAge }}</span>
</template>

<script lang="ts">
import Vue from 'vue'
import { DateTime } from 'luxon'

function formatAsYearsAndMonthsAndDays(value: string) {
  if (!value) {
    return ''
  }

  const duration = DateTime.fromJSDate(new Date()).diff(
    DateTime.fromISO(value),
    ['years', 'months', 'days']
  )

  let text = ''
  text = appendTimeUnitText(text, timeUnitText(duration.years, 'year', 'years'))
  text = appendTimeUnitText(
    text,
    timeUnitText(duration.months, 'month', 'months')
  )
  text = appendTimeUnitText(text, timeUnitText(duration.days, 'day', 'days'))

  return text.length === 0 ? '0 days' : text
}

function timeUnitText(value: number, singular: string, plural: string) {
  value = Math.floor(value)
  if (value > 1) {
    return value + ' ' + plural
  } else if (value === 1) {
    return '1 ' + singular
  } else {
    return null
  }
}

function appendTimeUnitText(text: string, timeUnitText: string | null) {
  if (timeUnitText) {
    text = text.length > 0 ? text + ', ' + timeUnitText : timeUnitText
  }

  return text
}

export default Vue.extend({
  props: {
    value: {
      type: String,
      default: undefined,
    },
  },
  computed: {
    formattedAge() {
      return formatAsYearsAndMonthsAndDays(this.value)
    },
  },
})
</script>
