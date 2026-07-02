module.exports = {
  run: [{
    method: "fs.rm",
    params: {
      path: "node_modules"
    }
  }, {
    method: "fs.rm",
    params: {
      path: "backend/node_modules"
    }
  }, {
    method: "script.start",
    params: {
      uri: "install.js"
    }
  }]
}
