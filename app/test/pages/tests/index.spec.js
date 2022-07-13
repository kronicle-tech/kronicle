import Index from '~/pages/tests'
import { createPageWrapper } from '~/test/pages/pageUtils'

describe('Index', () => {
  let tests
  let components
  let wrapper
  async function createWrapper() {
    wrapper = await createPageWrapper(Index, {
      serviceRequests: {
        '/v1/tests?fields=tests(id,description,priority)': {
          responseBody: { tests },
        },
        '/v1/components?fields=components(testResults)': {
          responseBody: { components },
        },
      },
    })
  }

  beforeEach(() => {
    tests = []
    components = []
  })

  afterEach(() => {
    wrapper.destroy()
    wrapper = null
  })

  test('has the right page title', async () => {
    await createWrapper()
    expect(wrapper.vm.$metaInfo.title).toBe('Kronicle - All Tests')
  })

  describe('when Get Tests service endpoint returns an empty array', () => {
    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })

  describe('when Get Tests service endpoint returns an array of multiple tests', () => {
    beforeEach(() => {
      tests = [
        {
          id: 'test-test-id-1',
          description: 'Test Description 1',
          priority: 'very-high',
        },
        {
          id: 'test-test-id-2',
          description: 'Test Description 2',
          priority: 'high',
        },
      ]
    })

    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })

  describe('when there are tests with different amounts of outcome counts', () => {
    beforeEach(() => {
      tests = [
        {
          id: 'test-test-id-1',
          description: 'Test Description 1',
          priority: 'very-high',
        },
        {
          id: 'test-test-id-2',
          description: 'Test Description 2',
          priority: 'high',
        },
        {
          id: 'test-test-id-3',
          description: 'Test Description 3',
          priority: 'medium',
        },
      ]
      components = [
        {
          id: 'test-test-id-1',
          type: 'test-type-1',
          name: 'Test Name 1',
          testResults: [
            {
              testId: 'test-test-id-1',
              priority: 'low',
              outcome: 'pass',
              message: 'Test Message 1a',
            },
            {
              testId: 'test-test-id-2',
              priority: 'low',
              outcome: 'pass',
              message: 'Test Message 1b',
            },
            {
              testId: 'test-test-id-3',
              priority: 'low',
              outcome: 'pass',
              message: 'Test Message 1c',
            },
          ],
        },
        {
          id: 'test-test-id-2',
          type: 'test-type-2',
          name: 'Test Name 2',
          testResults: [
            {
              testId: 'test-test-id-1',
              priority: 'low',
              outcome: 'pass',
              message: 'Test Message 2a',
            },
            {
              testId: 'test-test-id-2',
              priority: 'low',
              outcome: 'pass',
              message: 'Test Message 2b',
            },
            {
              testId: 'test-test-id-3',
              priority: 'low',
              outcome: 'fail',
              message: 'Test Message 2c',
            },
          ],
        },
        {
          id: 'test-test-id-3',
          type: 'test-type-3',
          name: 'Test Name 3',
          testResults: [
            {
              testId: 'test-test-id-1',
              priority: 'low',
              outcome: 'pass',
              message: 'Test Message 3a',
            },
            {
              testId: 'test-test-id-2',
              priority: 'low',
              outcome: 'fail',
              message: 'Test Message 3b',
            },
            {
              testId: 'test-test-id-3',
              priority: 'low',
              outcome: 'not-applicable',
              message: 'Test Message 3c',
            },
          ],
        },
      ]
    })

    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })

  describe('when there is a test and a component with no test results', () => {
    beforeEach(() => {
      tests = [
        {
          id: 'test-test-id-1',
          description: 'Test Description 1',
          priority: 'very-high',
        },
      ]
      components = [
        {
          id: 'test-test-id-1',
          type: 'test-type-1',
          name: 'Test Name 1',
        },
      ]
    })

    test('renders the page', async () => {
      await createWrapper()
      expect(wrapper.element).toMatchSnapshot()
    })
  })
})
