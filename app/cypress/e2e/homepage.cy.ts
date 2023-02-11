describe('homepage spec', () => {
  it('can load the homepage', () => {
    cy.visit('http://localhost:3000/')
    cy.get('h1').contains('Kronicle')
  })
})
