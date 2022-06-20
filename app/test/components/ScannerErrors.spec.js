import { mount } from '@vue/test-utils'
import ScannerErrors from '@/components/ScannerErrors.vue'

describe('ScannerErrors', () => {
  let propsData
  let wrapper
  const createWrapper = () => {
    wrapper = mount(ScannerErrors, { propsData })
  }

  beforeEach(() => {
    propsData = {}
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  describe('when ScannerErrors prop is not set', () => {
    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when ScannerErrors prop is set to an empty array', () => {
    beforeEach(() => {
      propsData.scannerErrors = []
    })

    test('renders nothing', () => {
      createWrapper()
      expect(wrapper.html()).toEqual('')
    })
  })

  describe('when ScannerErrors prop is set to one scanner error', () => {
    beforeEach(() => {
      propsData.scannerErrors = [
        {
          message: 'Test Message 1',
        },
      ]
    })

    test('renders an unordered list showing the scanner error', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })

    describe('when the scanner error has a cause', () => {
      beforeEach(() => {
        propsData.scannerErrors[0].cause = {
          message: 'Test Cause Message 1',
        }
      })

      test('renders the scanner error including the cause', () => {
        createWrapper()
        expect(wrapper.html()).toMatchSnapshot()
      })

      describe('when the cause has its own cause', () => {
        beforeEach(() => {
          propsData.scannerErrors[0].cause.cause = {
            message: 'Test Cause of Cause Message 1',
          }
        })

        test('renders the scanner error including both causes', () => {
          createWrapper()
          expect(wrapper.html()).toMatchSnapshot()
        })
      })
    })
  })

  describe('when ScannerErrors prop is set to multiple ScannerErrors', () => {
    beforeEach(() => {
      propsData.scannerErrors = [
        {
          message: 'Test Message 1',
        },
        {
          message: 'Test Message 2',
        },
        {
          message: 'Test Message 3',
        },
      ]
    })

    test('renders an unordered list showing the ScannerErrors', () => {
      createWrapper()
      expect(wrapper.html()).toMatchSnapshot()
    })
  })
})
