# Gen-Library 开发日志

## 6/23开发目标

1. #### 首先对项目进行`git`初始化 :ok:

2. #### 进行项目前后端的初始化

   1. #### 完成项目的登录模块


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

- #### 写出需求分析

- 完成对应的方案设计

- 后端开发

- 前端搬运调样式

### 开发详细日志

#### 需求分析：

​	在上一个工期中，已经开发完了整个系统的用户模块了，因此，现在开始开发和业务强相关的图片模块，而整个图片模块，我们需要完成的有以下的部分：

- #### 管理员功能

  - 图片上传（此时只是保存，还没有创建出图片的信息）
  - 图片创建
  - 图片管理
  - 图片修改（编辑图片的信息）

- 用户功能

  - 查看并搜索图片列表（主页）
  - 查看图片的详情（详情页）
  - 图片下载

#### 方案设计：

- 库表设计？

  ```mysql
  -- 基础信息
  id           bigint auto_increment comment 'id' primary key,
  url          varchar(512)        not null comment '图片 url',
  name         varchar(128)        not null comment '图片名称',
  introduction varchar(512)        null comment '简介',
  category     varchar(64)         null comment '分类',
  tags         varchar(512)        null comment '标签（JSON 数组）',
  
  -- 图片属性
  picSize      bigint              null comment '图片体积',  
  picWidth     int                 null comment '图片宽度',  
  picHeight    int                 null comment '图片高度',  
  picScale     double              null comment '图片宽高比例',  
  picFormat    varchar(32)         null comment '图片格式',  
  
  -- 用户关联：通过 userId 逻辑上关联用户表，可以知道图是由哪个用户上传的
  userId       bigint              not null comment '创建用户 id',  
  
  -- 4大样
  createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',  
  editTime     datetime default CURRENT_TIMESTAMP not null comment '编辑时间',  
  updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',  
  isDelete     tinyint  default 0  not null comment '是否删除',  
  
  -- 添加索引 (区分度高且查询频率高的字段)
  INDEX idx_name (name),                 -- 提升基于图片名称的查询性能  
  INDEX idx_introduction (introduction), -- 用于模糊搜索图片简介  
  INDEX idx_category (category),         -- 提升基于分类的查询性能  
  INDEX idx_tags (tags),                 -- 提升基于标签的查询性能  
  INDEX idx_userId (userId)              -- 提升基于用户 ID 的查询性能  
  ```

  

- 如何实现图片上传和下载的实现方式？

  1. 最简单的方式可以是：上传到后端的服务器，然后调用 Java 自带的文件读写 API 就可以实现图片的上传与下载
     - 缺点1：这不利于扩展，只能通过增加存储空间或者清理文件进行空间扩展
     - 缺点2：这不利于迁移，如果后端换服务器了，那么所有文件也都需要迁移
     - 缺点3：这不够安全，用户可能会恶心访问服务器上的文件，需要增加控制用户活动的权限
     - 缺点4：这不利于管理，我们只能通过文件资源管理器进行简单的管理操作，缺乏数据处理，流量分析等高级操作。
  2. 因此最好是使用第三方存储服务，对象存储（存储海量文件的分布式存储服务，高扩展性、低成本、高可靠、高安全）
     - 我这里使用了腾讯云的 COS 对象存储服务，新用户有半年 50GB Free 的活动

- 如何创建图片的业务流程？

  1. 用户上传图片时，就直接保存记录：系统立即生成图片的完整数据记录（图片的URL和其它元信息），这样不会浪费因用户不点击提交而残留在COS中的空间。
  2. 用户进行图片信息的编写，这其实就相当于是对图片进行编辑操作，之前上传的图片相当于是已经保存好的草稿了。

- 如何解析图片的信息？

  1. 因为使用了腾讯云的 COS 对象存储服务，因此我选择再采用腾讯的 数据万象 进行图片的解析
  2. 我们需要获取图片的：宽度、高度、宽高比、大小、格式、名称

#### 后端开发： 

- 准备工作：开通腾讯云对象存储以及数据万象服务。

  - 根据文档创建一个自己的 存储桶`bucket` 

    ![image-20250630220219610](../assets/image-20250630220219610.png)

  - 保留好 `bucket` 信息：

    ```yaml
    COS:
      client:
        host: https://genlib-1348838377.COS.ap-chengdu.myqcloud.com
        SecretId: xxx
        SecretKey: xxx
        region: ap-chengdu
        bucket: genlib-1348838377
    ```

  - 再直接开通数据万象的服务（很简单，直接一路下去就好）

- 引入 COS 对象存储服务

  - 引入 COS 依赖
  - 创建 `config.cosClientConfig` 类，用于读取配置文件，并创建一个 COS 客户端的 Bean
  - 新建 `application-local.yml` 配置文件，防止 `bucket` 信息泄露（修改配置）

- 建立通用能力类

  ​	因为 COS 对象存储服务与业务其实无关，因此算是个通用类，可以提供通用的对象存储能力，哪个项目都可适用，因此设为 `COSManager` 类

  ```java
  @Component  
  public class COSManager {  
    
      @Resource  
      private cosClientConfig cosClientConfig;  
    
      @Resource  
      private COSClient COSClient;  
    
      // ... 一些操作 COS 的方法  
      //比如 上传对象
      public PutObjectResult putObject(String	 key,File file){}
  }
  ```

- 测试 COS 功能，比如编写文件上传的代码

  - 创建 `putObject` 方法

  - 同时新建 `FileController` 中的 `testUploadFile` 测试接口

    ```java
    @用户校验注解
    @接口路径配置
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile){
        // 文件目录
        String fileName	= multipleFile.getOriginalFilename();
        String filePath = String.format("/test/%s",fileName);
        
        //上传文件
        File file = File.createTempFile(filePath, null);
        multipleFile.transferTo(file);
        COSManager.putObject(filePath, file);
        
        //返回可访问地址
        return ResultUtils.success(filePath, file);
    } 
    
    //抛出异常等业务自己来写
    ```

  - 在测试文件上传接口成功之后，在测试一下下载功能。

  - 在 `COSManager` 中新增对象下载方法，根据对象的 `key` 获取存储信息

    ```java
    public COSObject getObject(String key){
        GetObjectRequest getObjectRequest = new GetObject Request(cos ClientConfig.getBucket(),key);
        return cosClient.getObject(getObjectRequest);
    }
    ```

  - 开发完下载对象方法后，新建 `FileController` 中 `testDownloadFile` 方法

    ```java
    public void testDownloadFile(String filepath, HttpServletResponse response){
        COSObjectInputStream cosObjectInput = null;
        
        COSObject cosObject = cosManager.getObject(filepath);  
        cosObjectInput = cosObject.getObjectContent();  
        // 处理下载到的流  
        byte[] bytes = IOUtils.toByteArray(cosObjectInput);  
        // 设置响应头  
        response.setContentType("application/octet-stream;charset=UTF-8");  
        response.setHeader("Content-Disposition", "attachment; filename=" + filepath);  
        // 写入响应  
        response.getOutputStream().write(bytes);  
        response.getOutputStream().flush(); 
    }
    ```

#### 后端开发-图片上传

##### 数据模型

- 使用 MyBatisX插件生成 `picture` 表相关的基础代码(`model`、`Mapper`、`Service`)
- 建立图片上传的请求参数类——`model.dto.picture.PictureUploadRequest`
- 之后再新建上传成功后返回给前端的响应封装类 `modle.vo.PictureVO`
  - Picture 的字段
  - 修改标签字段为：`List<String> tags`
  - 增加 UserVO user 用户信息 字段
  - 增加 封装类转对象类 以及 对象类转封装类 的方法

##### 通用文件上传服务--FileManager

​	虽然在 `COSManager` 是通用的对象存储操作类，提供了上传和下载的需求，但是这个需求只是初步的，它有些许缺陷，因此我们要专门建立 `FileManager` 对它进行增强。

1. 对文件进行（非安全）校验，判断是否符合要求（后缀，大小等）

2. 指定文件上传的路径

3. 使用数据万象对文件进行解析，获得文件的属性

   ```java
   @Service  
   @Slf4j  
   public class FileManager {  
     
       @Resource  
       private CosClientConfig cosClientConfig;  
     
       @Resource  
       private CosManager cosManager;  
     
       // ...  
   }
   ```

4. 新增用于接收图片解析信息的包装类——`model.dto.file.uploadPictureResult`

5. 在 COS Manager 中添加上传图片并解析的方法——`putPictureObject()`

   ​	这样就使得图片自己有了附属的图片信息了，不用我们自己去获取。

6. 在 FileManager 中编写上传图片的方法

   ```java
   · uploadPicture(MultipleFile multipleFile, String uploadPathPrefix){
       // 校验图片
   
       // 指定图片上传地址
   
       // 创建临时文件
   
       // 上传图片(带解析的)
   
   	// 封装好返回结果
   }
   ```

##### 服务开发

1. Service 层

   - 声明上传图片方法——`Service.uploadPicture`

   ```java
   PictureVO uploadPicture(MultipleFile multipleFile, PictureUploadRequest pictureUploadRequest, User loginUser);
   ```

   - 实现上传图片方法——`Service.impl.uploadPicture`

2. Controller 层

   - 在 `PictureController` 中编写上传图片的接口，仅管理员可用

   ```java
   @PostMapping("/upload")
   @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
   public BaseResponse<PictureVO> uploadPicture(@RequestPart("file") MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, HttpServletRequest request){
       User loginUser = userService.getLoginUser(request);
       PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureVORequest, loginUser);
       return ResultUtils.success(pictureVO);
   }
   ```

3. 进行接口测试

   - 因为Spring MVC 自己限制了 Tomcat 服务器请求中文件上传的大小(1MB)，所以我们需要自己调大 Tomcat 中允许文件上传的大小

#### 后端开发—图片管理

##### 图片管理功能具体可拆分为：

- 管理员
  - 根据 图片id 删除图片
  - 更新图片
  - 分页获取图片的列表（不需要脱敏和限制条数）
  - 根据 id 获取图片（不需要脱敏）
- 用户
  - 分页获取图片列表（需要脱敏和限制条数）
  - 根据 id 获取图片（需要脱敏）
  - 修改图片（管理员和用户都用）

##### 建立数据模型

1. Picture Edit Request
2. Picture Query Request
3. Picture Update Request
4. Picture Upload Request

##### 服务开发

1. 在`UseService`中添加判断用户是否为管理员的方法——`isAdmin`
2. 对于分页查询接口，在 `PictureService` 中编写 `QueryWrapper<Picture>` 的方法
3. 新增获取图片封装类的方法——`getPictureVO`
4. 新增分页获取图片封装类的方法——`getPictureVOPage`
5. 新增图片数据校验方法——`validPicture`

##### 接口开发

1. `deletePicture`
2. `updatePicture`
3. `getPictureById`
4. `getPictureVOById`
5. `listPictureByPage`
6. `listPictureVOByPage`
7. `editPicture`
8. `listPicutreTagCategory`

之后就是完成前端代码啦！！

## 7/1开发日志

### 开发目标



### 开发详情


