-- =============================================
-- 描述：统一消息平台注册和配置中心数据库
-- MySQL版本：8.4.7
-- 创建时间：2026年1月
-- =============================================

DROP DATABASE IF EXISTS `feng_register`;

CREATE DATABASE `feng_register` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE feng_register;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for config_info
-- ----------------------------
DROP TABLE IF EXISTS `config_info`;
CREATE TABLE `config_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NULL DEFAULT NULL COMMENT 'group_id',
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text NULL COMMENT 'source user',
  `src_ip` varchar(50) NULL DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) NULL DEFAULT NULL COMMENT 'app_name',
  `tenant_id` varchar(128) NULL DEFAULT '' COMMENT '租户字段',
  `c_desc` varchar(256) NULL DEFAULT NULL COMMENT 'configuration description',
  `c_use` varchar(64) NULL DEFAULT NULL COMMENT 'configuration usage',
  `effect` varchar(64) NULL DEFAULT NULL COMMENT '配置生效的描述',
  `type` varchar(64) NULL DEFAULT NULL COMMENT '配置的类型',
  `c_schema` text NULL COMMENT '配置的模式',
  `encrypted_data_key` varchar(1024) NOT NULL DEFAULT '' COMMENT '密钥',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfo_datagrouptenant`(`data_id`, `group_id`, `tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = 'config_info' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config_info
-- ----------------------------
INSERT INTO `config_info` VALUES (1, 'application-dev.yml', 'DEFAULT_GROUP', 'jasypt:\n  encryptor:\n    password: pig\n    algorithm: PBEWithMD5AndDES\n    iv-generator-classname: org.jasypt.iv.NoIvGenerator\n    \nspring:\n  cache:\n    type: redis\n    redis:\n      time-to-live: 180m # 默认 60分钟\n  data:\n    redis:\n      host: ${REDIS_HOST:127.0.0.1}\n      password: ${REDIS_PASSWORD:}\n      port: ${REDIS_PORT:6379}\n      database: ${REDIS_DATABASE:0}\n  cloud:\n    sentinel:\n      eager: true\n      transport:\n        dashboard: feng-sentinel:5003\n    openfeign:\n      sentinel:\n        enabled: true\n      okhttp:\n        enabled: true\n      httpclient:\n        enabled: false\n      compression:\n        request:\n          enabled: true\n        response:\n          enabled: true\n\nmanagement:\n  endpoints:\n    web:\n      exposure:\n        include: \"*\"  \n  endpoint:\n    health:\n      show-details: ALWAYS\n\nmybatis-plus:\n  mapper-locations: classpath:/mapper/*Mapper.xml\n  global-config:\n    banner: false\n    db-config:\n      id-type: auto\n      table-underline: true\n      logic-delete-value: 1\n      logic-not-delete-value: 0\n  type-handlers-package: ltd.huntinginfo.feng.common.mybatis.handler\n  configuration:\n    map-underscore-to-camel-case: true\n    shrink-whitespaces-in-sql: true\n\nsms:\n  is-print: false \n  config-type: yaml\nlogging:\n  charset:\n    console: UTF-8\nfeign:\n  client:\n    config:\n      default:\n        decodeCharset: UTF-8\n        encodeCharset: UTF-8\nsecurity:\n  oauth2:\n    ignore:\n      urls:\n        - /open/app/authenticate', '29cb8731748ab23547fd36c3dab83443', '2026-02-08 11:55:11', '2026-03-01 00:48:52', 'nacos', '0:0:0:0:0:0:0:1', '', 'feng_dev', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (2, 'feng-auth3-dev.yml', 'DEFAULT_GROUP', 'spring:\n  freemarker:\n    allow-request-override: false\n    allow-session-override: false\n    cache: true\n    charset: UTF-8\n    check-template-location: true\n    content-type: text/html\n    enabled: true\n    request-context-attribute: request\n    expose-request-attributes: false\n    expose-session-attributes: false\n    expose-spring-macro-helpers: true\n    prefer-file-system-access: true\n    suffix: .ftl\n    template-loader-path: classpath:/templates/\n\n\nsecurity:\n  encode-key: \'thanks,pig4cloud\'\n  ignore-clients:\n    - test\n    - client\n    - open\n    - app\n    - DEVICE_MGMT_PLATFORM\n\nlogging:\n  level:\n    org.springframework.boot.autoconfigure: INFO\n    org.springframework.cloud.context.scope.GenericScope: INFO\n    org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer: INFO\n', '60fc7a583f981b3aaa20fe4e2d852acb', '2026-02-08 11:55:11', '2026-02-11 11:32:41', 'nacos', '0:0:0:0:0:0:0:1', '', 'feng_dev', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (3, 'feng-codegen-dev.yml', 'DEFAULT_GROUP', 'spring:\n  datasource:\n    type: com.zaxxer.hikari.HikariDataSource\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    username: ${USR_DB_USERNAME:root}\n    password: ${USR_DB_PASSWORD:123456}\n    url: jdbc:mysql://${USR_DB_HOST:127.0.0.1}:${USR_DB_PORT:13306}/${USR_DB_NAME:feng_user3_biz}?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true&allowPublicKeyRetrieval=true\n  resources:\n    static-locations: classpath:/static/,classpath:/views/\n', '89846e6c2936b0b5dbdf67ed19ae11a2', '2026-02-08 11:55:11', '2026-02-08 12:00:08', 'nacos', '0:0:0:0:0:0:0:1', '', 'feng_dev', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (4, 'feng-gateway3-dev.yml', 'DEFAULT_GROUP', 'spring:\n  cloud:\n    gateway:\n      server:\n        webflux:\n          routes:\n            - id: feng-message-center-biz\n              uri: lb://feng-message-center-biz\n              predicates:\n                - Path=/ump/**\n            - id: feng-auth\n              uri: lb://feng-auth\n              predicates:\n                - Path=/auth/**\n            - id: feng-user3-biz\n              uri: lb://feng-user3-biz\n              predicates:\n                - Path=/admin/**\n              filters:\n                # 限流配置\n                - name: RequestRateLimiter\n                  args:\n                    key-resolver: \'#{@remoteAddrKeyResolver}\'\n                    redis-rate-limiter.replenishRate: 100\n                    redis-rate-limiter.burstCapacity: 200\n            - id: feng-codegen\n              uri: lb://feng-codegen\n              predicates:\n                - Path=/gen/**\n            - id: feng-quartz\n              uri: lb://feng-quartz\n              predicates:\n                - Path=/job/**\n            - id: openapi\n              uri: lb://feng-gateway\n              predicates:\n                - Path=/v3/api-docs/**\n              filters:\n                - RewritePath=/v3/api-docs/(?<path>.*), /$\\{path}/$\\{path}/v3/api-docs\nlogging:\n  level:\n    ltd.huntinginfo.feng.gateway: DEBUG\n    ltd.huntinginfo.feng.common.security: DEBUG', '06be2b80389d023a89dd6965dd4ba2fc', '2026-02-08 11:55:11', '2026-02-28 05:06:29', 'nacos', '0:0:0:0:0:0:0:1', '', 'feng_dev', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (5, 'feng-monitor-dev.yml', 'DEFAULT_GROUP', 'spring:\n  autoconfigure:\n    exclude: ltd.huntinginfo.feng.common.core.config.JacksonConfiguration\n  security:\n    user:\n      name: ENC(8Hk2ILNJM8UTOuW/Xi75qg==)   \n      password: ENC(8Hk2ILNJM8UTOuW/Xi75qg==)\n', '640c79517dc763576b71c70507d70876', '2026-02-08 11:55:11', '2026-02-08 18:01:51', 'nacos', '0:0:0:0:0:0:0:1', '', 'feng_dev', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (6, 'feng-user3-biz-dev.yml', 'DEFAULT_GROUP', 'spring:\n  datasource:\n    type: com.zaxxer.hikari.HikariDataSource\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    username: ${USR_DB_USERNAME:root}\n    password: ${USR_DB_PASSWORD:123456}\n    url: jdbc:mysql://${USR_DB_HOST:127.0.0.1}:${USR_DB_PORT:13306}/${USR_DB_NAME:feng_user3_biz}?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true&allowPublicKeyRetrieval=true\n\nfile:\n  bucketName: feng_bucket \n  local:\n    enable: true\n    base-path: /upload', 'edbd8ffd069411b002c6d48e2d6f512e', '2026-02-08 11:55:11', '2026-02-09 08:22:34', 'nacos', '0:0:0:0:0:0:0:1', '', 'feng_dev', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (7, 'feng-quartz-dev.yml', 'DEFAULT_GROUP', 'spring:\n  datasource:\n    type: com.zaxxer.hikari.HikariDataSource\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    username: ${USR_DB_USERNAME:root}\n    password: ${USR_DB_PASSWORD:123456}\n    url: jdbc:mysql://${USR_DB_HOST:127.0.0.1}:${USR_DB_PORT:13306}/${USR_DB_NAME:feng_user3_biz}?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true&allowPublicKeyRetrieval=true\n', '0588025aad688c7d6656491b589cf368', '2026-02-08 11:55:11', '2026-02-08 12:13:49', 'nacos', '0:0:0:0:0:0:0:1', '', 'feng_dev', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (8, 'application-test.yml', 'DEFAULT_GROUP', '\njasypt:\n  encryptor:\n    password: ${JEP}\n    algorithm: PBEWithMD5AndDES\n    iv-generator-classname: org.jasypt.iv.NoIvGenerator\n    \n\nspring:\n  cache:\n    type: redis\n  data:\n    redis:\n      host: ${REDIS_HOST:127.0.0.1}\n      password: ${REDIS_PASSWORD:}\n      port: ${REDIS_PORT:6379}\n      database: ${REDIS_DATABASE:0}\n  cloud:\n    sentinel:\n      eager: true\n      transport:\n        dashboard: feng-sentinel:5003\n    openfeign:\n      sentinel:\n        enabled: true\n      okhttp:\n        enabled: true\n      httpclient:\n        enabled: false\n      compression:\n        request:\n          enabled: true\n        response:\n          enabled: true\n\n\nmanagement:\n  endpoints:\n    web:\n      exposure:\n        include: \"*\"  \n  endpoint:\n    health:\n      show-details: ALWAYS\n\n\nmybatis-plus:\n  mapper-locations: classpath:/mapper/*Mapper.xml\n  global-config:\n    banner: false\n    db-config:\n      id-type: auto\n      table-underline: true\n      logic-delete-value: 1\n      logic-not-delete-value: 0\n  type-handlers-package: ltd.huntinginfo.feng.common.mybatis.handler\n  configuration:\n    map-underscore-to-camel-case: true\n    shrink-whitespaces-in-sql: true\n\n\nsms:\n  is-print: false \n  config-type: yaml ', 'f03e1196d70b1530260fc2bee8528a17', '2026-02-08 11:55:21', '2026-02-08 12:18:16', 'nacos', '0:0:0:0:0:0:0:1', '', 'feng_test', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (9, 'feng-auth3-test.yml', 'DEFAULT_GROUP', '\nspring:\n  freemarker:\n    allow-request-override: false\n    allow-session-override: false\n    cache: true\n    charset: UTF-8\n    check-template-location: true\n    content-type: text/html\n    enabled: true\n    request-context-attribute: request\n    expose-request-attributes: false\n    expose-session-attributes: false\n    expose-spring-macro-helpers: true\n    prefer-file-system-access: true\n    suffix: .ftl\n    template-loader-path: classpath:/templates/\n\n\nsecurity:\n  encode-key: \'thanks,pig4cloud\'\n  ignore-clients:\n    - test\n    - client\n    - open\n    - app', '65ce13d8b99f931a001f14d3324c6cd1', '2026-02-08 11:55:21', '2026-02-08 17:59:54', 'nacos', '0:0:0:0:0:0:0:1', '', 'feng_test', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (10, 'feng-codegen-test.yml', 'DEFAULT_GROUP', '\nspring:\n  datasource:\n    type: com.zaxxer.hikari.HikariDataSource\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    username: ${USR_DB_USERNAME:root}\n    password: ${USR_DB_PASSWORD:123456}\n    url: jdbc:mysql://${USR_DB_HOST:127.0.0.1}:${USR_DB_PORT:13306}/${USR_DB_NAME:feng_user3_biz}?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true&allowPublicKeyRetrieval=true\n  resources:\n    static-locations: classpath:/static/,classpath:/views/\n', 'a9457691b1f098d095a43262af2a1227', '2026-02-08 11:55:21', '2026-02-08 12:16:50', 'nacos', '0:0:0:0:0:0:0:1', '', 'feng_test', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (11, 'feng-gateway3-test.yml', 'DEFAULT_GROUP', 'spring:\n  cloud:\n    gateway:\n      server:\n        webflux:\n          routes:\n\n            - id: feng-auth\n              uri: lb://feng-auth\n              predicates:\n                - Path=/auth/**\n\n            - id: feng-upms-biz\n              uri: lb://feng-upms-biz\n              predicates:\n                - Path=/admin/**\n              filters:\n\n                - name: RequestRateLimiter\n                  args:\n                    key-resolver: \'#{@remoteAddrKeyResolver}\'\n                    redis-rate-limiter.replenishRate: 100\n                    redis-rate-limiter.burstCapacity: 200\n\n            - id: feng-codegen\n              uri: lb://feng-codegen\n              predicates:\n                - Path=/gen/**\n\n            - id: feng-quartz\n              uri: lb://feng-quartz\n              predicates:\n                - Path=/job/**\n\n            - id: openapi\n              uri: lb://feng-gateway\n              predicates:\n                - Path=/v3/api-docs/**\n              filters:\n                - RewritePath=/v3/api-docs/(?<path>.*), /$\\{path}/$\\{path}/v3/api-docs', '779bcd8bf7c43633f014314368eced1d', '2026-02-08 11:55:21', '2026-02-08 12:16:29', 'nacos', '0:0:0:0:0:0:0:1', '', 'feng_test', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (12, 'feng-monitor-test.yml', 'DEFAULT_GROUP', 'spring:\n  autoconfigure:\n    exclude: ltd.huntinginfo.feng.common.core.config.JacksonConfiguration\n  # 安全配置\n  security:\n    user:\n      name: ENC(YD6XteDsniiEEDzC6oF5IQ==)     # feng\n      password: ENC(YD6XteDsniiEEDzC6oF5IQ==) \n', 'fd787fccc5d78cb2a03fa06de44e7d66', '2026-02-08 11:55:21', '2026-02-08 12:15:03', 'nacos', '0:0:0:0:0:0:0:1', '', 'feng_test', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (13, 'feng-user3-biz-test.yml', 'DEFAULT_GROUP', '# 数据源\nspring:\n  datasource:\n    type: com.zaxxer.hikari.HikariDataSource\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    username: ${USR_DB_USERNAME:root}\n    password: ${USR_DB_PASSWORD:123456}\n    url: jdbc:mysql://${USR_DB_HOST:127.0.0.1}:${USR_DB_PORT:13306}/${USR_DB_NAME:feng_user3_biz}?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true&allowPublicKeyRetrieval=true\n\n# 文件上传相关 支持阿里云、华为云、腾讯、minio\nfile:\n  bucketName: s3demo \n  local:\n    enable: true\n    base-path: /upload', '1b3db754657c36e2dc71af40fa52f862', '2026-02-08 11:55:21', '2026-02-08 12:15:34', 'nacos', '0:0:0:0:0:0:0:1', '', 'feng_test', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (14, 'feng-quartz-test.yml', 'DEFAULT_GROUP', 'spring:\n  datasource:\n    type: com.zaxxer.hikari.HikariDataSource\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    username: ${USR_DB_USERNAME:root}\n    password: ${USR_DB_PASSWORD:123456}\n    url: jdbc:mysql://${USR_DB_HOST:127.0.0.1}:${USR_DB_PORT:13306}/${USR_DB_NAME:feng_user3_biz}?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true&allowPublicKeyRetrieval=true\n', '0588025aad688c7d6656491b589cf368', '2026-02-08 11:55:21', '2026-02-08 12:15:52', 'nacos', '0:0:0:0:0:0:0:1', '', 'feng_test', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (15, 'feng-message-center-biz-dev.yml', 'DEFAULT_GROUP', 'spring:\n  datasource:\n    type: com.zaxxer.hikari.HikariDataSource\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    username: ${UMP_DB_USERNAME:root}\n    password: ${UMP_DB_PASSWORD:123456}\n    url: jdbc:mysql://${UMP_DB_HOST:127.0.0.1}:${UMP_DB_PORT:13306}/${UMP_DB_NAME:unified_message_platform}?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true&allowPublicKeyRetrieval=true\n  # RabbitMQ配置\n  rabbitmq:\n    host: ${RABBITMQ_HOST:localhost}\n    port: ${RABBITMQ_PORT:5672}\n    username: ${RABBITMQ_USERNAME:guest}\n    password: ${RABBITMQ_PASSWORD:guest}\n    virtual-host: ${RABBITMQ_VIRTUAL_HOST:/}\n    \n    # 连接配置\n    connection-timeout: 5000\n    cache:\n      channel:\n        size: 60\n        checkout-timeout: 2000\n    \n    # 确认模式\n    publisher-confirm-type: correlated\n    publisher-returns: true\n    listener:\n      simple:\n        acknowledge-mode: auto\n        concurrency: 3\n        max-concurrency: 10\n        prefetch: 10\n        retry:\n          enabled: true\n          max-attempts: 3\n          initial-interval: 1000\n          multiplier: 2.0\n          max-interval: 10000\n  kafka:\n    # 必配：Kafka 服务器地址\n    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}\n    \n    # ---------- 生产者配置 ----------\n    producer:\n      # 序列化器（必须配置）\n      key-serializer: org.apache.kafka.common.serialization.StringSerializer\n      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer\n      \n      # 可靠性配置（推荐）\n      acks: all                 # 所有副本确认\n      retries: 3               # 重试次数\n      linger.ms: 0            # 立即发送（可调优）\n      \n    # ---------- 消费者配置 ----------\n    consumer:\n      # 组 ID（建议使用应用名）\n      group-id: ${spring.application.name:ump-app}\n      \n      # 反序列化器（必须配置）\n      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer\n      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer\n      \n      # 偏移量重置策略（earliest/latest/none）\n      auto-offset-reset: earliest\n      \n      # 反序列化信任包（必须配置，否则无法反序列化自定义对象）\n      properties:\n        spring.json.trusted.packages: >\n          ltd.huntinginfo.feng.common.core.mq.dto,\n          ltd.huntinginfo.feng.center.dto\n          \n        # 类型映射（可选，简化反序列化时类型推断）\n        spring.json.type.mapping: |\n          mqMessage:ltd.huntinginfo.feng.common.core.mq.dto.MqMessage,\n          yourDto:ltd.huntinginfo.feng.center.dto.YourDto\nfile:\n  bucketName: feng_bucket \n  local:\n    enable: true\n    base-path: /upload\n        \n# 消息中心配置\nmc:\n  mq:\n    # 可选 rabbitmq / kafka\n    type: rabbitmq   \n  # 消息分发配置\n  distribution:\n    # 广播阈值（超过此值使用广播模式）\n    broadcast-threshold: 1000\n    # 最大重试次数\n    max-retry-count: 3\n    # 重试间隔（毫秒）\n    retry-interval: 5000\n  \n  # 定时任务配置\n  schedule:\n    # 未分发消息处理间隔（毫秒）\n    undistributed-interval: 30000\n    # 待推送消息处理间隔（毫秒）\n    pending-push-interval: 60000\n    # 待分发广播处理间隔（毫秒）\n    pending-broadcast-interval: 60000\n    # 每次处理数量\n    batch-size:\n      inbox: 100\n      broadcast: 50\n  \n  # 推送配置\n  push:\n    # HTTP请求超时时间（毫秒）\n    http-timeout: 5000\n    # 最大重试次数\n    max-retry-count: 3\n    # 重试间隔（毫秒）\n    retry-interval: 10000\njwt:\n  secret: abcdeeeeeeeUUU!!KKKK    \n  expiration: 1771682272\n  uri: http://127.0.0.1:11100/oauth2/jwks\nlogging:\n  level:\n    ltd.huntinginfo.feng.msg.mapper: DEBUG\n    com.baomidou.dynamic.datasource: DEBUG\n    com.alibaba.druid: DEBUG\n    ltd.huntinginfo.feng.common: DEBUG\n\n', '2e481891c165f8855f0201b7af5633ac', '2026-02-09 08:21:54', '2026-02-28 06:31:52', 'nacos', '0:0:0:0:0:0:0:1', '', 'feng_dev', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (16, 'feng-bootadmin-server-dev.yml', 'DEFAULT_GROUP', 'info:\n  version: 1.0.0\n  groupId: metanet\n  artifactId: redis-server\nspring:\n  main:\n    allow-bean-definition-overriding: false\n  security:\n    user:\n      name: admin\n      password: 123456\n      roles: ADMIN\n  boot:\n    admin:\n      client:\n        url: http://localhost:12006\n        username: admin\n        password: 123456\n        instance:\n          metadata:\n            user.name: ${spring.security.user.name}\n            user.password: ${spring.security.user.password}\n        webclient:\n          enabled: true\n      ui:\n        extension-resource-locations: classpath:/static/\n  web: \n    resources:\n      static-locations: classpath:/static/\n\nredis:\n  embedded:\n    enabled: true\n    maxHeap: 16MB\n    port: 6379\n    requirepass: true\n    password: 123456\n    ip: 127.0.0.1', '775556fe553bf42c410f91ef160acb37', '2026-03-03 19:33:44', '2026-03-03 19:36:43', 'nacos', '0:0:0:0:0:0:0:1', '', 'feng_dev', '', NULL, NULL, 'yaml', NULL, '');

-- ----------------------------
-- Table structure for config_info_beta
-- ----------------------------
DROP TABLE IF EXISTS `config_info_beta`;
CREATE TABLE `config_info_beta`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `beta_ips` varchar(1024) NULL DEFAULT NULL COMMENT 'betaIps',
  `md5` varchar(32) NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text NULL COMMENT 'source user',
  `src_ip` varchar(50) NULL DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) NULL DEFAULT '' COMMENT '租户字段',
  `encrypted_data_key` varchar(1024) NOT NULL DEFAULT '' COMMENT '密钥',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfobeta_datagrouptenant`(`data_id`, `group_id`, `tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci  COMMENT = 'config_info_beta' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config_info_beta
-- ----------------------------

-- ----------------------------
-- Table structure for config_info_gray
-- ----------------------------
DROP TABLE IF EXISTS `config_info_gray`;
CREATE TABLE `config_info_gray`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) NULL DEFAULT NULL COMMENT 'md5',
  `src_user` text NULL COMMENT 'src_user',
  `src_ip` varchar(100) NULL DEFAULT NULL COMMENT 'src_ip',
  `gmt_create` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'gmt_create',
  `gmt_modified` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'gmt_modified',
  `app_name` varchar(128) NULL DEFAULT NULL COMMENT 'app_name',
  `tenant_id` varchar(128) NULL DEFAULT '' COMMENT 'tenant_id',
  `gray_name` varchar(128) NOT NULL COMMENT 'gray_name',
  `gray_rule` text NOT NULL COMMENT 'gray_rule',
  `encrypted_data_key` varchar(256) NOT NULL DEFAULT '' COMMENT 'encrypted_data_key',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfogray_datagrouptenantgray`(`data_id`, `group_id`, `tenant_id`, `gray_name`) USING BTREE,
  INDEX `idx_dataid_gmt_modified`(`data_id`, `gmt_modified`) USING BTREE,
  INDEX `idx_gmt_modified`(`gmt_modified`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = 'config_info_gray' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config_info_gray
-- ----------------------------

-- ----------------------------
-- Table structure for config_info_tag
-- ----------------------------
DROP TABLE IF EXISTS `config_info_tag`;
CREATE TABLE `config_info_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) NULL DEFAULT '' COMMENT 'tenant_id',
  `tag_id` varchar(128) NOT NULL COMMENT 'tag_id',
  `app_name` varchar(128) NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text NULL COMMENT 'source user',
  `src_ip` varchar(50) NULL DEFAULT NULL COMMENT 'source ip',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfotag_datagrouptenanttag`(`data_id`, `group_id`, `tenant_id`, `tag_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci  COMMENT = 'config_info_tag' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config_info_tag
-- ----------------------------

-- ----------------------------
-- Table structure for config_tags_relation
-- ----------------------------
DROP TABLE IF EXISTS `config_tags_relation`;
CREATE TABLE `config_tags_relation`  (
  `id` bigint NOT NULL COMMENT 'id',
  `tag_name` varchar(128) NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) NULL DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) NULL DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint NOT NULL AUTO_INCREMENT COMMENT 'nid, 自增长标识',
  PRIMARY KEY (`nid`) USING BTREE,
  UNIQUE INDEX `uk_configtagrelation_configidtag`(`id`, `tag_name`, `tag_type`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci  COMMENT = 'config_tag_relation' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config_tags_relation
-- ----------------------------

-- ----------------------------
-- Table structure for group_capacity
-- ----------------------------
DROP TABLE IF EXISTS `group_capacity`;
CREATE TABLE `group_capacity`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
  `quota` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '配额，0表示使用默认值',
  `usage` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用量',
  `max_size` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '聚合子配置最大个数，，0表示使用默认值',
  `max_aggr_size` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_group_id`(`group_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci  COMMENT = '集群、各Group容量信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of group_capacity
-- ----------------------------
INSERT INTO `group_capacity` VALUES (1, '', 0, 16, 0, 0, 0, 0, '2026-02-08 11:55:03', '2026-03-18 16:35:28');

-- ----------------------------
-- Table structure for his_config_info
-- ----------------------------
DROP TABLE IF EXISTS `his_config_info`;
CREATE TABLE `his_config_info`  (
  `id` bigint UNSIGNED NOT NULL COMMENT 'id',
  `nid` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'nid, 自增标识',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text NULL COMMENT 'source user',
  `src_ip` varchar(50) NULL DEFAULT NULL COMMENT 'source ip',
  `op_type` char(10) NULL DEFAULT NULL COMMENT 'operation type',
  `tenant_id` varchar(128) NULL DEFAULT '' COMMENT '租户字段',
  `encrypted_data_key` varchar(1024) NOT NULL DEFAULT '' COMMENT '密钥',
  `publish_type` varchar(50) NULL DEFAULT 'formal' COMMENT 'publish type gray or formal',
  `gray_name` varchar(50) NULL DEFAULT NULL COMMENT 'gray name',
  `ext_info` longtext NULL COMMENT 'ext info',
  PRIMARY KEY (`nid`) USING BTREE,
  INDEX `idx_gmt_create`(`gmt_create`) USING BTREE,
  INDEX `idx_gmt_modified`(`gmt_modified`) USING BTREE,
  INDEX `idx_did`(`data_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci  COMMENT = '多租户改造' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions`  (
  `role` varchar(50) NOT NULL COMMENT 'role',
  `resource` varchar(128) NOT NULL COMMENT 'resource',
  `action` varchar(8) NOT NULL COMMENT 'action',
  UNIQUE INDEX `uk_role_permission`(`role`, `resource`, `action`) USING BTREE
) ENGINE = InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of permissions
-- ----------------------------

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `username` varchar(50) NOT NULL COMMENT 'username',
  `role` varchar(50) NOT NULL COMMENT 'role',
  UNIQUE INDEX `idx_user_role`(`username`, `role`) USING BTREE
) ENGINE = InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES ('nacos', 'ROLE_ADMIN');

-- ----------------------------
-- Table structure for tenant_capacity
-- ----------------------------
DROP TABLE IF EXISTS `tenant_capacity`;
CREATE TABLE `tenant_capacity`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '配额，0表示使用默认值',
  `usage` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用量',
  `max_size` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '聚合子配置最大个数',
  `max_aggr_size` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci  COMMENT = '租户容量信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tenant_capacity
-- ----------------------------
INSERT INTO `tenant_capacity` VALUES (1, 'feng_dev', 0, 9, 0, 0, 0, 0, '2026-02-08 11:55:03', '2026-03-18 16:35:29');
INSERT INTO `tenant_capacity` VALUES (2, 'public', 0, 0, 0, 0, 0, 0, '2026-02-08 11:55:31', '2026-03-18 16:35:29');

-- ----------------------------
-- Table structure for tenant_info
-- ----------------------------
DROP TABLE IF EXISTS `tenant_info`;
CREATE TABLE `tenant_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) NULL DEFAULT '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) NULL DEFAULT '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) NULL DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) NULL DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint NOT NULL COMMENT '创建时间',
  `gmt_modified` bigint NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_info_kptenantid`(`kp`, `tenant_id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci  COMMENT = 'tenant_info' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tenant_info
-- ----------------------------
INSERT INTO `tenant_info` VALUES (2, '1', 'feng_test', 'feng_test', 'feng_test', 'nacos', 1770522767127, 1770522767127);
INSERT INTO `tenant_info` VALUES (3, '1', 'feng_dev', 'feng_dev', 'feng_dev', 'nacos', 1770522787383, 1770522787383);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `username` varchar(50) NOT NULL COMMENT 'username',
  `password` varchar(500) NOT NULL COMMENT 'password',
  `enabled` tinyint(1) NOT NULL COMMENT 'enabled',
  PRIMARY KEY (`username`) USING BTREE
) ENGINE = InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('nacos', '$2a$10$6sEELW0vP2xpFtU5Ki6R3Olg7yvQO1coY4HbY5pQdT6.FbBLdKb..', 1);

SET FOREIGN_KEY_CHECKS = 1;
