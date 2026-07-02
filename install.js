module.exports = {
  run: [
    {
      method: "shell.run",
      params: {
        message: "npm install",
        path: "."
      }
    },
    {
      method: "shell.run",
      params: {
        message: "npm install",
        path: "backend"
      }
    },
    {
      method: "shell.run",
      params: {
        message: "npx cap sync android",
        path: "."
      }
    }
  ]
}
