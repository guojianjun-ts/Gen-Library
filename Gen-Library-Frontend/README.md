# Gen-Lib前端开发日志

## 6/23 开发目标

- 完成项目前端的初始化 :ok_hand:
- 完成前端登录板块的开发 :x:
- 通过插件生成调用后端接口的前端代码 :x:

## 6/23 开发详细日志

1. 创建 Gen-Library-Frontend 的 Vue3 项目

   ```shell
   npm create vue@3.12.2
   
   # 支持 TypeScript 语法
   # 引入 Vue-Router
   # 引入 Pinia
   # 引入 ESLint
   # 引入 Prettier
   ```

2. 进行依赖安装，并且引入 `Ant Design Vue` 组件库

   ```shell
   # 依赖安装
   npm install --force
   
   #引入 Ant Design Vue 组件库
   npm i --save ant-design-vue@4.x
   ```

3. 进行 Ant Design Vue 的全局注册

   ```ts
   import './assets/main.css'
   
   import { createApp } from 'vue'
   import { createPinia } from 'pinia'
   
   import App from './App.vue'
   import router from './router'
   import Antd from 'ant-design-vue'
   import 'ant-design-vue/dist/reset.css'
   
   const app = createApp(App)
   
   app.use(createPinia())
   app.use(router)
   app.use(Antd)
   
   app.mount('#app')
   ```

4. 对不必要的文件（样例 demo 或样式 css ）包进行`Delete`

5. 开发全局基本样式 `BasicLayout`

   - 创建 `layouts` 文件夹
   - 随后创建 `BasicLayout.vue` 基本布局组件
     - 前往 ant-design-vue 组件库中寻找 layouts 布局，我采用了 上中下 的经典布局
   - 同时，由于顶部栏开发比较复杂，我们创建 `GlobalHeader.vue` ，并引用 Ant Design 的菜单组件

6. 引入 `Axios` 请求库：当前端需要获取数据时，就需要向后端提供的接口发送请求，而发送请求最常用的技术就是 AJAX 技术，但是代码复杂，因此采用第三方的请求封装库（`Axiox`），来简化发送请求的代码。

7. 引入自动生成请求代码的 `umijs-openapi` 工具库

   ```shell
   npm i --save-dev @umijs/openapi
   ```

   并在根目录建立 `openapi.config.ts`

   ```ts
   import { generateService } from '@umijs/openapi'
   
   generateService({
     requestLibPath: "import request from '@/request'",
     schemaPath: 'http://localhost:8123/api/v2/api-docs',
     serversPath: './src',
   })
   
   ```

   同时还可以在 `package.json` 中添加执行代码

   ```json
   "openapi": "node openapi.config.js"
   ```

8. 全局状态管理

   - 缘故：开发页面时，可能会有所有页面都共享的变量，比如已登录用户的信息，而我们需要对这个全局信息进行状态管理，这时就可以使用一个主流的状态管理库—`Pinia`
   - 1.引入：之前使用 `create-vue` 脚手架时，就已经整合了 `Pinia`，因此我们不需要再引入了
   - 2.在 `Pinia` 的 `src/stores` 目录下定义 `user` 模块（用户的存储，远程获取，修改等）

