import { Component, ScannerError } from '~/types/component-catalog-service'
import { compareObjectsWithComponents } from '~/src/objectWithComponentComparator'

interface ScannerErrorWithComponent extends ScannerError {
  component?: Component
}

export function compareScannerErrors(
  a: ScannerErrorWithComponent,
  b: ScannerErrorWithComponent
) {
  const result = compareObjectsWithComponents(a, b)

  if (result !== 0) {
    return result
  }

  return a.scannerId.localeCompare(b.scannerId)
}
