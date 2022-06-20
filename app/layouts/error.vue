<template>
  <div class="m-3">
    <div v-if="error.statusCode === 404">
      <h1>Page not found</h1>

      <p>Sorry, the page you were looking for could not be found.</p>
    </div>

    <div v-else>
      <h1>An error occurred</h1>
    </div>

    <p>
      Please return to the <NuxtLink to="/">Home Page</NuxtLink> or use the
      navigation links at the top of the page.
    </p>
  </div>
</template>

<script>
export default {
  layout: 'error',
  props: {
    error: {
      type: Object,
      required: true,
    },
  },
}
</script>
