import { DocFile } from '~/types/kronicle-service'

export function compareDocFiles(a: DocFile, b: DocFile) {
  return a.path.localeCompare(b.path)
}
