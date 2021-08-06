import ScannerErrorsView from '@/components/ScannerErrorsView.vue'
import { createViewComponentWrapper } from '~/test/components/viewUtils'
import { createComponentWithScannerErrors } from '~/test/testDataUtils'
import { expectTextsInTableRows } from '~/test/components/tableUtils'

describe('ScannerErrorsView', () => {
  let propsData
  let wrapper
  async function createWrapper() {
    wrapper = await createViewComponentWrapper(ScannerErrorsView, {
      propsData,
    })
  }

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when components prop is set to an empty array', () => {
    beforeEach(() => {
      propsData = {
        components: [],
      }
    })

    test('renders no table', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expect(wrapper.find('table').exists()).toBe(false)
    })
  })

  describe('when components prop is set to multiple components with scanner errors', () => {
    beforeEach(() => {
      propsData = {
        components: [
          createComponentWithScannerErrors({
            componentNumber: 1,
          }),
          createComponentWithScannerErrors({
            componentNumber: 2,
          }),
          createComponentWithScannerErrors({
            componentNumber: 3,
          }),
        ],
      }
    })

    test('renders all the scanner errors in a table', async () => {
      await createWrapper()
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

    test('when selecting a filter, the filter is applied to the scanner errors in the table', async () => {
      await createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.scanner', [
        'test-scanner-id-1-1',
        'test-scanner-id-1-2',
        'test-scanner-id-2-1',
        'test-scanner-id-2-2',
        'test-scanner-id-3-1',
        'test-scanner-id-3-2',
      ])
      await wrapper.get('input[value="test-team-id-1-1"]').setChecked()
      expect(wrapper.html()).toMatchSnapshot()
      expectTextsInTableRows(wrapper, 'td.scanner', [
        'test-scanner-id-1-1',
        'test-scanner-id-1-2',
      ])
    })
  })
})
