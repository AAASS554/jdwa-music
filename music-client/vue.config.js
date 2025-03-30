const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  chainWebpack: config => {
    config.plugin('define').tap(definitions => {
      Object.assign(definitions[0]['process.env'], {
        NODE_HOST: '"http://localhost:8888"',
      });
      return definitions;
    });
  },
  pluginOptions: {
    electronBuilder: {
      nodeIntegration: true,
      // 改用JS文件
      mainProcessFile: 'src/background.js',
      // 改用JS文件
      preload: 'src/preload.js',
      // 禁用TypeScript处理
      disableMainProcessTypescript: true,
      mainProcessTypeChecking: false,
      builderOptions: {
        appId: 'com.music.app',
        productName: '音乐播放器',
        copyright: 'Copyright © 2023',
        // Windows配置
        win: {
          icon: './public/favicon.ico',
          target: [
            {
              target: 'nsis',
              arch: ['x64']
            }
          ]
        },
        // Mac配置
        mac: {
          icon: './public/app.icns'
        },
        // 安装程序配置
        nsis: {
          oneClick: false,
          allowToChangeInstallationDirectory: true,
          installerIcon: './public/favicon.ico',
          installerHeaderIcon: './public/favicon.ico',
          createDesktopShortcut: true,
          createStartMenuShortcut: true,
          shortcutName: '音乐播放器'
        }
      }
    }
  }
})
