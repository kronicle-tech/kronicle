import { Team } from '~/types/kronicle-service'

export function compareTeams(a: Team, b: Team) {
  return a.name.localeCompare(b.name)
}
