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

   ```apl
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

2. 开发业务通用代码（重在了解）

   - 自定义异常类 `exception`

     - ErrorCode
     - BusinessException

     - ThrowUtils

   - 自定义通用返回类 `common`

     - BaseResponse
     - ResultUtils

   - 全局异常处理器 `exception`

     - GlobalExceptionHandler

   - 请求包装类

     - PageRequest
     - DeleteRequest

   - 全局跨域配置 `config`

     - CorsConfig

## 6/24开发目标

1. 进行登录板块的前后端开发（要求全部自己手写）
2. 先进行需求分析
3. 进行数据库库表设计
4. 进行后端接口设计
5. 前端开发

### 6/24详细开发日志

#### 1.分析阶段：

对于登录板块，一共可以有6个需求：

- 用户注册：`userName`，`userPassword`，`checkPassword` 进行注册
- 用户登录：`username`，`userPassword` 进行登录
- 退出登录：（取消登录态）
- 获取当前登录用户：
- 管理员的用户管理功能：（搜索用户、修改用户信息、删除用户等）
- 用户的权限分配：将用户分为 `admin` 与普通 `user`

#### 2.库表设计：

- 数据库名称：gen_lib
- 数据库表：user（用户表）

#### 3.进行数据模型的开法

1. 实体类（`model.entity`）

    由于 Mybatis-X generator 生产的数据库实体类，可能不符我们的要求，比如：

   1. 可以替换 id 的连续生成策略，使用 `ASSIGN_ID` 雪花算法进行生成
   2. 对于 `isDelete` 字段，增添 @`TableLogic` 注解，这样就会拥有逻辑删除的效果

2. 枚举类（`model.enums`）

   对于数量很确定，类型很确定的数据类型，可以进行一个枚举类的封装，便于在项目中获取值、减少枚举值的输入错误等情况

   1. 例如可以在`enums`包下建立`UserRoleEnum`枚举类

3. 接收请求参数的`model.dto.xxx`类：

   在进行接口开发时，最好为板块中每一个接口都定义一个专门的类来接收请求参数，以提高代码的可读性和维护性。

#### 4.开始进行各功能接口的开发

##### 4.1用户注册

1. 数据模型

   在 `model.dto.user` 下创建用于接收注册请求参数的类 `UserRegisterRequest`

2. 服务开发

   - 在 `service.UserService` 中增加方法声明 `userRegister`

   - 在 `service.impl.UserServiceImpl` 中增加 `userRegister` 的实现方法

     而在 `userRegister` 的实现中，我们需要增加以下注册的检验条件：

     - 参数为空
     - `userAccount` 过短：< 4 
     - `userPassword`  过短：<8
       - 两次输入的密码不一致

3. 加密处理

   同时为了防止用户的密码泄露，我们将用户的密码进行加密存储，而这一个过程就可以封装为一个方法 `getEncryptPassword(String userPassword)`

   1. 具体是 定义一个 `salt` 盐值
   2. 然后再使用 `hutool` 的 `DigestUtil.md5Hex` 单向加密算法进行加密

4. 接口开发

   在 `controller.UserController` 中新建用户注册接口 `userRegister`

##### 4.2用户登录

1. 数据模型

   在 `model.dto.user` 下创建用于接收注册请求参数的类 `UserLoginRequest`

2. 服务开发

   - 在 `service.UserService` 中增加方法声明 `userLogin`

   - 在 `service.impl.UserServiceImpl` 中增加 `userLogin` 的实现方法

     同4.1用户注册一样，也需要增加对应的参数校验

     1. 先对参数进行对应的校验
     2. 对登录的密码也进行加密
     3. 查询用户是否存在
        1. 不存在，则抛出一个 `BusinessException` 和一个报错信息
        2. 若存在，则记录用户的登录态

3. 统一声明常量类

   在 `constant.UserConstant` 下统一声明用户相关的常量

   - 用户登录态键
   - region 权限
     - `DEFAULT_ROLE`
     - `ADMIN_ROLE`

4. 数据脱敏

   在 `model.vo` 包中新建一个 `LoginUserVO` 类，这表示脱敏后的登录用户信息，因为我们尝试着不去暴露更多信息返回给前端，因此对登录成功的用户信息进行一个数据脱敏的封装。

5. 接口开发

在 `controller.UserController` 中新建用户登录接口 `userRegister`

##### 4.3获取当前登录用户

1. 数据模型：

   ​	因为我们可以直接从 `request` 请求对象对应的 `Session` 中直接获取到之前保存的登陆用户信息，而不需要其它额外的请求参数，因此不进行 `dto` 数据模型的封装

2. 服务开发

   - 在 `service.UserService` 中新增方法声明 `getLoginUser`
   - 在 `service.impl.UserServiceImpl` 中增加对应的实现代码
     - 先从 Session 中获得登录用户的 `id`
     - 再从数据库中去查询最新的结果（也可以直接省略不写）

3. 接口开发

   在 `controller.UserController` 中新增获取当前登录用户的接口 `getLoginUser`

   1. 需要注意，因为这里是直接返回数据库中所查到的所有信息，因此要对返回的信息进行一个脱敏处理，使用我们刚刚创建的 `LoginUserVO` 封装类进行返回

##### 4.4用户注销

1. 数据模型：

   ​	因为这里也是直接通过 `request` 的 `Session` 来完成注销，无需额外的请求参数，所以也不建立新的数据模型

2. 服务开发：

   - 先在 `service.UserService` 中新增方法声明：`userLogout`
   - 再到 `service.impl.UserServiceImpl` 中对方法进行实现

3. 接口开发

   - 最终到 `controller.UserController` 中新增用户注销接口：`userLogout`

##### 4.5用户权限控制

我们为了方便，不想在每个需要进行权限校验的业务代码前写个10行代码进行权限校验，因此，我们想到使用切面编程，Spring AOP 切面+自定义权限校验注解，来实现统一的接口拦截和权限校验

1. 权限校验注解
   - `annotation.AuthCheck` 接口类中，编写权限校验注解
   
2. 权限校验切面
   - `aop.AuthInterceptor` 中编写权限校验AOP，使用`@Around("@annotation(authCheck)")` 环绕通知
   
3. 使用注解
   - 现在只要给对应的 `controller` 方法上添加了 `@AuthCheck` 注解，就相当于有了权限校验的功能。
   
     例如：
   
     ```java
     @GetMapping("/health")
     @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
     public BaseResponse<String> health() {
         return ResultUtils.success("ok");
     }
     
     ```
   
     

##### 4.6用户管理

1. 功能拆分分析：
   1. 【管理员】创建用户
   2. 【管理员】根据 id 删除用户
   3. 【管理员】更新用户
   4. 【管理员】分页获取用户列表（需要脱敏）
   5. 【管理员】根据 id 获取用户（未脱敏）
   6. 根据 id 获取用户（脱敏）
2. 数据模型
   - 在 `dto.user` 中添加用户创建请求 `UserAddRequest` 
   - 在 `dto.user` 中添加用户更新请求 `UserUpdateRequest` 
   - 在 `dto.user` 中添加用户查询请求 `UserQueryRequest`，并继承 `PageRequest`完成分页查询
3. 数据脱敏
   - 在 `model.vo` 包中新增 `UserVo`，表示脱敏后的用户
4. 服务开发

   ```java
   //增添用户
   Long addUser(UserAddRequest userAddRequest);
   
   //根据 ID 获取用户信息
   User getUserById(long id);
   
   //根据 ID 获取用户脱敏信息
   UserVO getUserVOById(long id);
   
   //删除用户
   boolean deleteUser(long id);
   
   //更新用户
   boolean updateUser(UserUpdateRequest userUpdateRequest);
   
   //获取用户脱敏信息
   UserVO getUserVO(User user);
   
   //批量获取用户脱敏信息列表
   List<UserVO> getUserVOList(List<User> userList);
   
   //获取查询条件
   QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
   ```

   
5. 接口开发 

   ```java
   @PostMapping("/add")
   @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)// 以下每一个管理员接口都需要去写注解校验
   public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
       ThrowUtils.throwIf(userAddRequest == null , ErrorCode.PARAMS_ERROR);
       return ResultUtils.success(userService.addUser(userAddRequest));
   }
   
   @GetMapping("/get")
   public BaseResponse<User> getUserById(long id) {
       ThrowUtils.throwIf(id <= 0 , ErrorCode.PARAMS_ERROR);
       return ResultUtils.success(userService.getUserById(id));
   }
   
   @GetMapping("/get/vo")
   public BaseResponse<UserVO> getUserVOById(long id) {
       ThrowUtils.throwIf(id <= 0 , ErrorCode.PARAMS_ERROR);
       return ResultUtils.success(userService.getUserVOById(id));
   }
   
   @PostMapping("/delete")
   public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
       ThrowUtils.throwIf(deleteRequest == null , ErrorCode.PARAMS_ERROR);
       return ResultUtils.success(userService.deleteUser(deleteRequest.getId()));
   }
   
   @PostMapping("/update")
   public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
       ThrowUtils.throwIf(userUpdateRequest == null , ErrorCode.PARAMS_ERROR);
       return ResultUtils.success(userService.updateUser(userUpdateRequest));
   }
   
   @PostMapping("/list/page/vo")
   public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
       ThrowUtils.throwIf(userQueryRequest == null , ErrorCode.PARAMS_ERROR);
       long current = userQueryRequest.getCurrent();  //获取当前页号
       long pageSize = userQueryRequest.getPageSize();
       Page<User> userPage = userService.page(new Page<>(current , pageSize) ,
               userService.getQueryWrapper(userQueryRequest));
       Page<UserVO> userVOPage = new Page<>(current , pageSize , userPage.getTotal());
       List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
       userVOPage.setRecords(userVOList);
       return ResultUtils.success(userVOPage);
   }
   
   ```

## 6/29开发日志

### 开发目标

- #### 看完图片板块

- 亲自敲完图片板块代码

- 搬运前端代码

### 开发详细日志
