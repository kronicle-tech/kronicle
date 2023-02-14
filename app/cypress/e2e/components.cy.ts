describe('components spec', () => {
  it('can load components from the backend service', () => {
    cy.visit('http://localhost:3000/')
    cy.get('[data-cy=nav-link-components]').click()

    cy.url().should('eq', 'http://localhost:3000/components')
    cy.get('[data-cy-component=example-component]').contains(
      'Example Component'
    )
  })
})
