# Swagger/OpenAPI API Reference Guide

Follow these steps to generate and serve a Swagger/OpenAPI API reference for your Node.js/Express backend:

---

## 1. Install Dependencies
In your `backend/` directory, run:
```sh
npm install swagger-jsdoc swagger-ui-express --save
```

## 2. Add Swagger Setup to server.js
Add the following to your `backend/server.js`:
```js
const swaggerJsdoc = require('swagger-jsdoc');
const swaggerUi = require('swagger-ui-express');

const swaggerOptions = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'Adoption & Child Care API',
      version: '1.0.0',
      description: 'API documentation for the Adoption & Child Care backend.'
    },
    servers: [
      { url: 'http://localhost:3000/api' }
    ]
  },
  apis: ['./server.js'], // Path to the API docs (can add more files)
};

const swaggerSpec = swaggerJsdoc(swaggerOptions);
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec));
```

## 3. Document Your Endpoints
Add JSDoc comments above your Express routes in `server.js`:
```js
/**
 * @swagger
 * /children:
 *   get:
 *     summary: Get all children
 *     responses:
 *       200:
 *         description: List of children
 */
app.get('/children', ...)
```
- See [swagger-jsdoc syntax](https://github.com/Surnet/swagger-jsdoc) for more examples.

## 4. View the API Docs
- Start your backend server.
- Visit [http://localhost:3000/api-docs](http://localhost:3000/api-docs) in your browser.

---

**For more details, see the [swagger-jsdoc](https://github.com/Surnet/swagger-jsdoc) and [swagger-ui-express](https://github.com/scottie1984/swagger-ui-express) documentation.** 