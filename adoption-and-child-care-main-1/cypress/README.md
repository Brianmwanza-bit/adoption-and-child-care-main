# Cypress E2E Testing

## Running Tests

1. Start your backend and frontend servers.
2. In a new terminal, run:
   ```
   npx cypress open
   ```
   or for headless:
   ```
   npx cypress run
   ```
3. Select and run the desired test (e.g., `sample.cy.js`).

## Load Testing
- For load/stress testing, consider using tools like [k6](https://k6.io/) or [Artillery](https://artillery.io/).
- Example command:
  ```
  npx k6 run loadtest.js
  ``` 