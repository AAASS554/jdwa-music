# 音乐网站系统

一个基于 Vue3 + SpringBoot + MySQL 的音乐网站系统，包含前台用户界面和后台管理系统。

## 系统架构

- 前端 Web 客户端：Vue 3 + TypeScript (music-client)
- 后台管理系统：Vue 3 + TypeScript (music-manage)
- 后端服务：Spring Boot (music-server)
- 数据库：MySQL
- 缓存：Redis
- 文件存储：MinIO

## 功能特点

### 前台功能
- 用户登录/注册
- 音乐播放、暂停、上一首、下一首
- 歌单浏览与管理
- 歌手页面
- 个人中心
- 音乐搜索
- 歌曲收藏
- 评论功能

### 后台功能
- 用户管理
- 歌手管理
- 歌单管理
- 歌曲管理
- 评论管理
- 轮播图管理

## 环境要求

- Node.js: v14+
- Java: JDK 1.8+
- MySQL: 5.7+
- Redis: 6.0+
- MinIO: 最新版
- Maven: 3.6+

## 快速开始

### 1. 数据库配置
```bash
# 数据库配置信息
端口: 3308
数据库名: music
用户名: root
密码: 123456
```

### 2. 后端配置
修改 `music-server/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3308/music?serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=123456
```

### 3. MinIO 配置
```properties
minio.endpoint=http://localhost:9000
minio.access-key=root
minio.secret-key=123456789
minio.bucket-name=music
```

### 4. 启动服务

方式一：使用启动脚本
```bash
# 运行 start-all.bat 脚本
双击运行 start-all.bat
```

方式二：手动启动各服务
```bash
# 1. 启动 MySQL 服务 (端口 3308)

# 2. 启动 MinIO 服务
.\minio.exe server C:\minio --console-address ":9001"

# 3. 启动后端服务
cd music-server
mvn spring-boot:run

# 4. 启动前端服务
cd music-client
npm install
npm run serve

# 5. 启动管理后台
cd music-manage
npm install
npm run serve
```

## 访问地址

- 前台页面：http://localhost:8080
- 后台管理：http://localhost:8081
- MinIO 控制台：http://localhost:9001
- 后端接口：http://localhost:8888

## 项目结构 
music-website/
├── music-client/ # 前台用户界面
│ ├── src/
│ │ ├── assets/ # 静态资源
│ │ ├── components/ # 公共组件
│ │ ├── views/ # 页面组件
│ │ ├── router/ # 路由配置
│ │ ├── store/ # 状态管理
│ │ └── utils/ # 工具函数
│ └── public/ # 公共文件
├── music-manage/ # 后台管理系统
│ ├── src/
│ │ ├── assets/ # 静态资源
│ │ ├── components/ # 公共组件
│ │ ├── views/ # 页面组件
│ │ ├── router/ # 路由配置
│ │ └── store/ # 状态管理
│ └── public/ # 公共文件
├── music-server/ # 后端服务
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/ # Java 源代码
│ │ │ └── resources/ # 配置文件
│ │ └── test/ # 测试代码
│ └── pom.xml # Maven 配置
├── start-all.bat # 一键启动脚本
└── README.md

## 技术栈

### 前端
- Vue 3
- TypeScript
- Element Plus
- Vuex
- Vue Router
- Axios

### 后端
- Spring Boot
- MyBatis Plus
- Redis
- MinIO
- MySQL

## 注意事项

1. 确保 MySQL 服务运行在 3308 端口
2. MinIO 需要正确配置存储路径
3. 首次运行需要导入数据库脚本
4. 确保所有端口未被占用
5. 建议使用 start-all.bat 脚本一键启动所有服务

## 已知问题

1. 播放音乐时可能需要双击播放键
2. 页面刷新后需要重新登录


## 开发团队

本项目由 记得晚安(JDWA) 开发和维护

- 前端开发：记得晚安(JDWA)
- 后端开发：记得晚安(JDWA)
- UI 设计：记得晚安(JDWA)

## 贡献指南

1. Fork 本仓库
2. 创建新的功能分支
3. 提交你的更改
4. 发起 Pull Request

## 联系方式

如有问题或建议，欢迎通过以下方式联系：

- GitHub: [记得晚安(JDWA)https://github.com/AAASS554]
- Email: [1412800823@qq.com]

## License

MIT License

Copyright (c) 2024 记得晚安(JDWA)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## 数据库设计

### 1. 管理员表(admin)
```sql
CREATE TABLE `admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
)
```

### 2. 用户表(consumer)
```sql
CREATE TABLE `consumer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `sex` tinyint(1) DEFAULT NULL,
  `phone_num` char(15) DEFAULT NULL,
  `email` char(30) DEFAULT NULL,
  `birth` datetime DEFAULT NULL,
  `introduction` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
)
```

### 3. 歌手表(singer)
```sql
CREATE TABLE `singer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `sex` tinyint(1) DEFAULT NULL,
  `pic` varchar(255) DEFAULT NULL,
  `birth` datetime DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `introduction` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
)
```

### 4. 歌单表(song_list)
```sql
CREATE TABLE `song_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `pic` varchar(255) DEFAULT NULL,
  `introduction` text,
  `style` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
)
```

### 5. 收藏表(collect)
```sql
CREATE TABLE `collect` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `type` tinyint(1) DEFAULT NULL,
  `song_id` int(11) DEFAULT NULL,
  `song_list_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
)
```

## 系统配置说明

### Redis 配置
```properties
spring.redis.host=127.0.0.1
spring.redis.port=6379
spring.redis.database=0
spring.redis.timeout=1800000
spring.redis.lettuce.pool.max-active=20
spring.redis.lettuce.pool.max-wait=-1
spring.redis.lettuce.pool.max-idle=5
spring.redis.lettuce.pool.min-idle=0
```

### 邮件服务配置
```yaml
spring:
  mail:
    host: smtp.163.com
    port: 465
    username: [你的邮箱]
    password: [你的授权码]
    properties:
      mail:
        smtp:
          auth: true
          socketFactory:
            class: javax.net.ssl.SSLSocketFactory
          starttls:
            enable: true
```

## 更新日志

### v1.0.0 (2024-03-15)
- 初始版本发布
- 实现基础音乐播放功能
- 完成用户系统开发
- 添加歌单管理功能

### v1.0.1 (2024-03-16)
- 修复头像显示问题
- 优化音乐播放体验
- 改进登录状态持久化

## 常见问题

1. Q: 如何解决播放需要双击的问题？
   A: 这是已知问题，正在修复中。临时解决方案是使用单击触发两次播放事件。

2. Q: 为什么数据库使用 3308 端口？
   A: 为避免与默认的 3306 端口冲突，建议使用 3308 端口。可以在配置文件中修改。



## API 文档

### 用户相关接口

#### 1. 用户登录
- 请求路径：`POST /user/login`
- 请求参数：
```json
{
    "username": "string",
    "password": "string"
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "id": "number",
        "username": "string",
        "token": "string"
    }
}
```

#### 2. 用户注册
- 请求路径：`POST /user/register`
- 请求参数：
```json
{
    "username": "string",
    "password": "string",
    "email": "string",
    "phoneNum": "string"
}
```

### 音乐相关接口

#### 1. 获取歌单列表
- 请求路径：`GET /songList`
- 请求参数：
```json
{
    "pageNum": "number",
    "pageSize": "number"
}
```

#### 2. 获取歌手列表
- 请求路径：`GET /singer`
- 请求参数：
```json
{
    "pageNum": "number",
    "pageSize": "number"
}
```

#### 3. 歌曲搜索
- 请求路径：`GET /song/search`
- 请求参数：
```json
{
    "keywords": "string"
}
```

### 收藏相关接口

#### 1. 收藏歌曲
- 请求路径：`POST /collect/add`
- 请求参数：
```json
{
    "userId": "number",
    "type": "number",
    "songId": "number"
}
```

#### 2. 获取收藏列表
- 请求路径：`GET /collect/list`
- 请求参数：
```json
{
    "userId": "number",
    "type": "number"
}
```

### 评论相关接口

#### 1. 添加评论
- 请求路径：`POST /comment/add`
- 请求参数：
```json
{
    "userId": "number",
    "songId": "number",
    "content": "string"
}
```

#### 2. 获取评论列表
- 请求路径：`GET /comment/list`
- 请求参数：
```json
{
    "songId": "number"
}
```

### 文件上传接口

#### 1. 上传图片
- 请求路径：`POST /file/img/upload`
- 请求参数：
  - Content-Type: multipart/form-data
  - file: 图片文件
- 响应结果：
```json
{
    "code": 200,
    "message": "上传成功",
    "data": {
        "url": "string"
    }
}
```

#### 2. 上传音乐
- 请求路径：`POST /file/music/upload`
- 请求参数：
  - Content-Type: multipart/form-data
  - file: 音乐文件
- 响应结果：
```json
{
    "code": 200,
    "message": "上传成功",
    "data": {
        "url": "string"
    }
}
```

### 通用响应格式
```json
{
    "code": "number",    // 状态码
    "message": "string", // 提示信息
    "data": "object"     // 响应数据
}
```

### 状态码说明
- 200：成功
- 400：请求参数错误
- 401：未授权
- 403：禁止访问
- 404：资源不存在
- 500：服务器内部错误

### 接口认证
- 除登录和注册接口外，其他接口都需要在请求头中携带 token
- 请求头格式：`Authorization: Bearer {token}`

### 注意事项
1. 所有请求默认使用 JSON 格式
2. 文件上传接口需使用 multipart/form-data 格式
3. 分页接口默认从第 1 页开始
4. 时间格式统一使用 ISO 8601 标准


## 致谢

感谢所有为这个项目提供帮助和建议的朋友们。

特别感谢：
- Element Plus 团队提供的优秀 UI 组件库
- Vue.js 核心团队
- Spring Boot 团队