describe('Homepage', () => {
  it('should load and display the main title', () => {
    cy.visit('/'); // uses baseUrl from cypress.config.js
    cy.contains('Adoption and Child Care').should('exist'); // Adjust text to match your main title
  });
}); 