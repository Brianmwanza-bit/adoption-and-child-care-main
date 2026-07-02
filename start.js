module.exports = {
  daemon: true,
  run: [
    {
      method: "shell.run",
      params: {
        message: "npm start",
        path: "backend",
        on: [{
          event: "/(http:\\/\\/[0-9.:]+)/",
          done: true
        }]
      }
    },
    {
      method: "local.set",
      params: {
        url: "{{input.event[1]}}"
      }
    }
  ]
}
