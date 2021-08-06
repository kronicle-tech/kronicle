export interface ItemCount<T extends string> {
  item: T
  count: number
}

export function distinctArrayElements(array: any[]): any[] {
  return Array.from(new Set(array)).sort()
}

export function intRange(
  startInclusive: number,
  endExclusive: number
): number[] {
  const array = []

  for (let value = startInclusive; value < endExclusive; value++) {
    array.push(value)
  }

  return array
}

export function itemCounts<T extends string>(array: T[]): ItemCount<T>[] {
  const map = array.reduce((accumulator, currentValue) => {
    accumulator.set(currentValue, (accumulator.get(currentValue) ?? 0) + 1)
    return accumulator
  }, new Map())
  return Array.from(map)
    .map(
      (entry) =>
        ({
          item: entry[0],
          count: entry[1],
        } as ItemCount<T>)
    )
    .sort((a, b) => {
      const result = b.count - a.count

      if (result !== 0) {
        return result
      }

      if (a.item === undefined && b.item === undefined) {
        return 0
      } else if (a.item === undefined) {
        return -1
      } else if (b.item === undefined) {
        return 1
      } else {
        return a.item.localeCompare(b.item)
      }
    })
}
