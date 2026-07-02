module.exports = {
  version: "7.0",
  title: "Adoption & Child Care",
  description: "Android app for adoption and child-care management with AI-assisted coding.",
  icon: "icon.png",
  menu: async (kernel, info) => {
    let installed = await info.exists("app/build");
    let running = await info.running("start.js");
    let claudeRunning = await info.running("claude.js");

    if (!installed) {
      return [{
        default: true,
        icon: "fa-solid fa-download",
        text: "Install Dependencies",
        href: "install.js"
      }]
    } else {
      let menu = [{
        icon: "fa-solid fa-play",
        text: "Launch App",
        href: "start.js",
        default: !claudeRunning && !running
      }, {
        icon: "fa-solid fa-robot",
        text: "Chat with Claude (Primary Agent)",
        href: "claude.js",
        default: true
      }, {
        icon: "fa-solid fa-arrows-rotate",
        text: "Update",
        href: "update.js"
      }, {
        icon: "fa-solid fa-trash-can",
        text: "Reset",
        href: "reset.js"
      }]
      if (running) {
        menu.unshift({
          icon: "fa-solid fa-stop",
          text: "Stop App",
          href: "start.js"
        })
      }
      if (claudeRunning) {
        menu.unshift({
          icon: "fa-solid fa-stop",
          text: "Stop Claude",
          href: "claude.js"
        })
      }
      return menu
    }
  }
}
