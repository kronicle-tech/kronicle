import { mount } from '@vue/test-utils'
import ScannerErrorTable from '@/components/ScannerErrorTable.vue'
import { createComponentWithScannerErrors } from '~/test/testDataUtils'
import { expectTextsInTableRows } from '~/test/components/tableUtils'

describe('ScannerErrorTable', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(ScannerErrorTable, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when components prop is set to an empty array', () => {
    beforeEach(() => {
      propsData.components = []
    })

    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when components is set to multiple components with multiple scanner errors', () => {
    beforeEach(() => {
      propsData.components = [
        createComponentWithScannerErrors({ componentNumber: 1 }),
        createComponentWithScannerErrors({ componentNumber: 2 }),
        createComponentWithScannerErrors({ componentNumber: 3 }),
      ]
    })

    test('renders the scanner errors', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.scanner', [
        'test-scanner-id-1-1',
        'test-scanner-id-1-2',
        'test-scanner-id-2-1',
        'test-scanner-id-2-2',
        'test-scanner-id-3-1',
        'test-scanner-id-3-2',
      ])
    })
  })
})
