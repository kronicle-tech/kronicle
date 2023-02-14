describe('components spec', () => {
  it('can load components from the backend service', () => {
    cy.visit('http://localhost:3000/')
    cy.get('[data-cy="nav-link-components"]').click()
    cy.get('a[href="/components/kronicle-app"]').contains('Kronicle App')
  })
})
