describe('Login and Dashboard E2E', () => {
  it('should log in and show dashboard', () => {
    cy.visit('http://localhost:50000');
    cy.get('input[name="username"]').type('admin');
    cy.get('input[name="password"]').type('admin');
    cy.get('button[type="submit"]').click();
    cy.contains('Dashboard').should('be.visible');
  });
}); 