# Backend

This directory contains the backend server and database for the Adoption and Child Care project.

## Contents
- `server.js`: Express server implementation.
- `adoption_child_care.db`: SQLite database file.
- `package.json` and `package-lock.json`: Node.js dependencies.

## Usage
1. Install dependencies:
   ```bash
   npm install
   ```
2. Start the server:
   ```bash
   node server.js
   ```

---

*Add more backend documentation as needed.* 

You **do** have a full HTML frontend in `src/index.html` with supporting CSS and JavaScript.  
However, if you are running the frontend and cannot access it via port 60870, the issue is likely with **how you are serving the frontend files**.

### Here’s what you need to do to make your frontend accessible from any device:

---

## 1. **Serve the Frontend with a Static Server**

Your frontend is in the `src/` directory. You need to serve it using a static file server.  
**You cannot just open `index.html` directly for multi-device access.**

### **Option A: Use Node.js/Express (Recommended for Integration)**
Add this to your `backend/server.js`:
```js
app.use(express.static(path.join(__dirname, '../src')));
```
- This will serve your frontend at the same port as your backend (e.g., `http://localhost:5000`).

### **Option B: Use a Simple Static Server**
From your project root, run:
```sh
npx serve src
```
or
```sh
npx http-server src
```
- This will serve your frontend at a port like `http://localhost:5000` or `http://localhost:8080`.

---

## 2. **Access from Other Devices**
- Make sure your firewall allows incoming connections on the chosen port.
- Use your computer’s **local IP address** (not `localhost`) on your phone or other devices, e.g., `http://192.168.1.10:5000`.

---

## 3. **Production Deployment**
- Deploy your frontend to a service like Netlify, Vercel, or your own server for global access.

---

**You have the code. The missing step is serving it properly.**  
Would you like me to update your backend to serve the frontend, or do you want a command to run a static server?  
If you want, I can do both right now—just say “continue.” 

Great! You’ve provided a Cypress `projectId` (`tsy88g`). This is used for connecting your Cypress project to the Cypress Dashboard for cloud test recording and analytics.

### How to Use the Cypress `projectId`

1. **Where to Add It:**  
   Cypress expects the `projectId` to be set in your `cypress.config.js` (or `cypress.config.mjs`) file in your project root.

2. **How to Add It:**  
   Open your Cypress config file and add (or update) the `projectId` field:

   ```js
   // cypress.config.js or cypress.config.mjs
   const { defineConfig } = require('cypress');

   module.exports = defineConfig({
     e2e: {
       // ...other config options
     },
     projectId: "tsy88g"
   });
   ```

   Or, if your config is in JSON format:
   ```json
   {
     "projectId": "tsy88g",
     "e2e": {
       // ...other config options
     }
   }
   ```

3. **Benefits:**  
   - Enables test result recording to the Cypress Dashboard (if you run with `--record` and have set up your Cypress account).
   - Useful for CI/CD and team collaboration.

---

**Would you like me to update your Cypress config file to include this `projectId` for you?**  
Just say “yes” and I’ll make the change automatically! 

Here’s what I found about your backend error handling:

### Current State
- **Pattern:** Most endpoints use inline error handling, e.g.:
  ```js
  if (err) return res.status(500).json({ error: err.message });
  ```
- **Authentication errors:** Use status codes 401/403 with simple messages.
- **Validation errors:** Some endpoints (like file upload) use 400 for missing files.
- **General errors:** There is a final error-handling middleware:
  ```js
  app.use((err, req, res, next) => {
    console.error(err.stack);
    res.status(500).json({ error: 'Something went wrong!' });
  });
  ```
- **Async/await:** Not consistently used; some async logic is inside callbacks (e.g., bcrypt in login).
- **No error codes or consistent structure:** Error responses are not standardized (sometimes just `{ error: ... }`).

---

### Plan for Improvement

1. **Standardize error responses:** Always return `{ success: false, error: { code, message, details } }` for errors.
2. **Use try/catch for async logic:** Especially for endpoints using async/await.
3. **Propagate errors to centralized middleware:** Use `next(err)` instead of sending responses directly in every handler.
4. **Add error codes:** For easier frontend handling and debugging.
5. **Improve validation error handling:** Use 400 for bad input, 404 for not found, 403 for forbidden, etc.

---

**I will:**
- Refactor a few representative endpoints (e.g., `/register`, `/login`, one CRUD endpoint) to use this improved pattern.
- Add a utility for error formatting.
- Update the error-handling middleware for richer responses.

Let’s start with these changes. 

Absolutely! Here’s how you can set the ocean image as the background for your primary frontend using the file explorer:

### Step-by-Step File Explorer Path

1. **Save the Image:**
   - Save your ocean image as `c9.jpg`.

2. **Navigate to the Correct Directory:**
   - Open your project folder in your file explorer.
   - Go to:  
     ```
     adoption-and-child-care-main/
       └── src/
           └── icons/
     ```
   - Place your `c9.jpg` file inside the `icons` folder.

3. **Verify the CSS Reference:**
   - The CSS in your project already points to this image:
     ```
     background: url('icons/c9.jpg') no-repeat center center fixed !important;
     ```
   - This means as long as `c9.jpg` is in the `src/icons/` directory, it will be used as the background.

---

**Summary of the path:**
```
adoption-and-child-care-main/src/icons/c9.jpg
```

If you follow these steps, your ocean image will appear as the background for your primary frontend. If you need to change the image, just replace `c9.jpg` in that folder.

Let me know if you need help with any other file paths or setup! 