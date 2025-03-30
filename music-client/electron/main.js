const { app, BrowserWindow, ipcMain } = require('electron')
const path = require('path')

// 保持对window对象的全局引用，如果不这么做的话，当JavaScript对象被
// 垃圾回收的时候，window对象将会自动的关闭
let win

function createWindow() {
  // 创建浏览器窗口
  win = new BrowserWindow({
    width: 1080,
    height: 680,
    minWidth: 1080,
    minHeight: 680,
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false,
      enableRemoteModule: true,
      webSecurity: false
    },
    frame: true,
    show: false
  })

  // 加载应用
  const isDev = process.env.NODE_ENV === 'development'
  const url = isDev ? 'http://localhost:8080' : `file://${path.join(__dirname, '../dist/index.html')}`
  win.loadURL(url)

  // 在加载完成后显示窗口
  win.once('ready-to-show', () => {
    win.show()
  })

  // 打开开发者工具
  if (isDev) {
    win.webContents.openDevTools()
  }

  // 当window被关闭时，触发下面的事件
  win.on('closed', () => {
    win = null
  })
}

// 当Electron初始化完成并准备创建浏览器窗口时调用这个方法
app.whenReady().then(createWindow)

// 所有窗口关闭时退出应用
app.on('window-all-closed', () => {
  // 在 macOS 上，除非用户用 Cmd + Q 确定地退出，
  // 否则绝大部分应用及其菜单栏会保持激活。
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

app.on('activate', () => {
  // 在macOS上，当单击dock图标并且没有其他窗口打开时，
  // 通常在应用程序中重新创建一个窗口。
  if (win === null) {
    createWindow()
  }
})

// 在这个文件中，你可以包含应用程序特定的其他代码
// 你也可以将它们放在单独的文件中，然后在这里导入。 