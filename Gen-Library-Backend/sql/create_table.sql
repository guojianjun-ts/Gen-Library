CREATE DATABASE IF NOT EXISTS `gen_lib` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


use gen_lib;

-- 用户表
CREATE TABLE IF NOT EXISTS `user`
(
    `id`           bigint       NOT NULL AUTO_INCREMENT COMMENT '用户ID（主键）',
    `userAccount`  varchar(32)  NOT NULL COMMENT '用户账号',
    `userPassword` varchar(128) NOT NULL COMMENT '用户密码',
    `userName`     varchar(32)           DEFAULT NULL COMMENT '用户昵称',
    `userAvatar`   varchar(256)          DEFAULT NULL COMMENT '用户头像URL',
    `userProfile`  text                  DEFAULT NULL COMMENT '用户简介',
    `userRole`     varchar(32)  NOT NULL DEFAULT 'user' COMMENT '用户角色（admin/user/等）',
    `editTime`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
    `createTime`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`     tinyint      NOT NULL DEFAULT '0' COMMENT '是否删除（0-未删除，1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_userAccount` (`userAccount`) COMMENT '用户账号唯一索引',
    KEY `idx_userName` (`userName`) COMMENT '用户昵称索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户信息表';


use gen_lib;

-- 图片表
create table if not exists picture
(
    id           bigint auto_increment comment 'id' primary key,
    url          varchar(512)                       not null comment '图片 url',
    name         varchar(128)                       not null comment '图片名称',
    introduction varchar(512)                       null comment '简介',
    category     varchar(64)                        null comment '分类',
    tags         varchar(512)                       null comment '标签（JSON 数组）',
    picSize      bigint                             null comment '图片体积',
    picWidth     int                                null comment '图片宽度',
    picHeight    int                                null comment '图片高度',
    picScale     double                             null comment '图片宽高比例',
    picFormat    varchar(32)                        null comment '图片格式',
    userId       bigint                             not null comment '创建用户 id',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    editTime     datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    INDEX idx_name (name),                 -- 提升基于图片名称的查询性能
    INDEX idx_introduction (introduction), -- 用于模糊搜索图片简介
    INDEX idx_category (category),         -- 提升基于分类的查询性能
    INDEX idx_tags (tags),                 -- 提升基于标签的查询性能
    INDEX idx_userId (userId)              -- 提升基于用户 ID 的查询性能
) comment '图片' collate = utf8mb4_unicode_ci;
