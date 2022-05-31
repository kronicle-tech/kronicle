import {Component} from "~/types/kronicle-service";
import {NuxtRuntimeConfig} from "@nuxt/types/config/runtime";
import {Route} from "vue-router";

export async function fetchComponentStateTypes($config: NuxtRuntimeConfig, route: Route): Promise<string[]> {
  const component = await fetch(
    `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,states(type))`
  )
    .then((res) => res.json())
    .then((json) => json.component as Component)
  return component.states.map((state) => state.type)
}
