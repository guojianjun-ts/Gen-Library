CREATE DATABASE IF NOT EXISTS `gen_lib` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

use gen_lib;
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
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户信息表'