# Gen-Library 开发日志

## 6/23开发目标

1. #### 首先对项目进行`git`初始化 :ok:

2. #### 进行项目前后端的初始化

3. #### 完成项目的登录模块

### 6/23详细日志

1. 初始化后端项目

   - 创建一个 Spring Boot 项目，然后选择 JDK 8，再选中四个必备的依赖
      - Spring Web（提供内嵌的 Tomcat服务器，处理 HTTP 相关的请求和相应）

      - Mybatis（MySQL数据库访问框架）

      - MySQL Driver（连接 MySQL 数据库的驱动程序）

      - Lombok （通过注解简化 Java 代码）

   - 补充：mybatis-plus | hutool（工具包）| knife4j 接口文档工具 | AOP 切面
   - Knife4j 接口文档地址：http://localhost:8123/api/doc.html#/home
   - 修改后的 application.yml 

   ```properties
   server:
     port: 8123
     servlet:
       context-path: /api
   spring:
     application:
       name: Gen-Library-Backend
     # 数据库配置
     datasource:
       driver-class-name: com.mysql.cj.jdbc.Driver
       url: jdbc:mysql://localhost:3306/gen_lib
       username: root
       password: 123456
   mybatis-plus:
     configuration:
       map-underscore-to-camel-case: false
       log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
     global-config:
       db-config:
         logic-delete-field: isDelete #全局逻辑删除
         logic-not-delete-value: 0    #默认未删除值为 0
         logic-delete-value: 1        #默认删除值为 1
   #接口文档注释
   knife4j:
     enable: true
     openapi:
       title: Gen-Library接口文档
       version: v1.0
       group:
         default:
           group-name: 默认分组
           api-rule: package
           api-rule-resources:
             - com.gjj.genlibrarybackend.controller
   ```

   

1. 开发登录模块后端

   1. 用户登录

   2. 用户注册

   3. 用户注销（取消登录态）

   4. admin管理

      

