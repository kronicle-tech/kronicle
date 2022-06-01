import {Component, ComponentState} from "~/types/kronicle-service";

export function findComponentState<T>(component: Component, type: string): T | undefined {
  return component.states.find(state => state.type === type) as T | undefined
}

export function findComponentStates<T>(component: Component, type: string): ReadonlyArray<T> {
  return component.states.filter(state => state.type === type) as unknown as ReadonlyArray<T>
}
