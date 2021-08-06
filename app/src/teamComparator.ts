import { Team } from '~/types/component-catalog-service'

export function compareTeams(a: Team, b: Team) {
  return a.name.localeCompare(b.name)
}
