# 统一消息中心feng-cloud3

[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot 4.0.3](https://img.shields.io/badge/Spring%20Boot-4.0.3-green.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud 2025.1.1](https://img.shields.io/badge/Spring%20Cloud-2025.1.1-brightgreen.svg)](https://spring.io/projects/spring-cloud)
[![Spring Cloud Alibaba 2025.1.0.0](https://img.shields.io/badge/Spring%20Cloud%20Alibaba-2025.1.0.0-orange.svg)](https://github.com/alibaba/spring-cloud-alibaba)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

## 简介

统一消息中心`feng-cloud3` 是一个基于 Spring Boot 4、Spring Cloud 2025 和 Spring Cloud Alibaba 构建的微服务基础平台，专注于**统一消息中心**的解决方案。它提供了完整的微服务基础设施，包括服务注册与发现、配置中心、网关路由、认证授权、分布式日志、消息队列集成、文件存储等核心功能。

本项目在 [lengleng/pig](https://gitee.com/log4j/pig) 项目基础上进行了二次开发，升级了核心框架版本（目前支持Java 21、Spring Boot 4.0.3、Spring Cloud 2025.1.1、Cloud Alibaba 2025.1.0），并新增了统一消息中心业务模块，同时保持了原有的灵活性和扩展性。

## 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Java | 21 | 运行环境 |
| Spring Boot | 4.0.3 | 基础框架 |
| Spring Cloud | 2025.1.1 | 微服务生态 |
| Spring Cloud Alibaba | 2025.1.0.0 | 服务注册/配置 |
| Nacos | 3.1.1 | 服务注册与配置中心 |
| MySQL | 8.4.7 | 数据库 |
| MyBatis Plus | 3.5.16 | ORM 增强 |
| Redis | 7.2 | 缓存与分布式会话 |
| RabbitMQ / Kafka | 4.2.3 / 3.9.2 | 消息队列 |
| Spring Boot Admin | 4.0.1 | 监控管理 |

## 模块结构

```
feng-cloud3
├── feng-bom                         # 项目 BOM，统一依赖版本
├── feng-library3                    # 公共组件聚合模块
│   ├── feng-library3-core           # 核心工具类
│   ├── feng-library3-feign          # Feign 增强
│   ├── feng-library3-mybatis        # MyBatis Plus 集成
│   ├── feng-library3-excel          # Excel 导入导出
│   ├── feng-library3-seata          # 分布式事务（Seata）
│   ├── feng-library3-swagger        # 接口文档
│   ├── feng-library3-gray           # 灰度发布
│   ├── feng-library3-sentinel       # 流量控制
│   ├── feng-library3-datasource     # 多数据源
│   ├── feng-library3-idempotent     # 幂等控制
│   ├── feng-library3-oss            # 对象存储
│   ├── feng-library3-xss            # XSS 防护
│   ├── feng-library3-websocket      # WebSocket 支持
│   ├── feng-library3-security       # 安全模块
│   ├── feng-library3-log            # 日志模块
│   ├── feng-library3-rabbitmq       # RabbitMQ 集成
│   └── feng-library3-kafka          # Kafka 集成
├── feng-base                        # 基础服务聚合模块
│   ├── feng-auth3                   # 认证授权中心（OAuth2）
│   ├── feng-gateway3                # 网关（Spring Cloud Gateway）
│   ├── feng-config                  # Nacos 配置中心服务（独立部署）
│   └── feng-bootadmin-server        # Spring Boot Admin 监控服务和REIDS服务
└── feng-busi                        # 业务模块聚合
    ├── feng-user3                   # 用户管理模块
    │   ├── feng-user3-api           # 用户管理 API/Model
    │   └── feng-user3-biz           # 用户管理业务实现
    └── feng-message-center          # 统一消息中心模块 （** 业务主模块 **）
        ├── feng-message-center-api  # 消息中心 API/Model
        └── feng-message-center-biz  # 消息中心业务实现

```

## 快速开始

### 环境要求
- JDK 21
- Maven 3.6+
- MySQL 8.4.7 或更高版本
- Redis 7.2 （使用feng-bootadmin-server，也可以单独下载运行）
- Nacos 3.1.1 或更高版本（使用feng-config，也可单独下载运行）

### 本地运行步骤

1. **创建数据库**
   - 安装mysql 8.4.7
   
   下载地址： https://downloads.mysql.com/archives/community/
   
   以下是在CentOS或者Rocky Linux下的安装步骤：
   
   ```bash
   #检查操作系统版本
   cat /etc/redhat-release
   #检查glibc的版本
   ldd --version
   #查看已安装的glibc
   rpm -qa | grep glibc
   #检查gcc版本
   gcc --version
   # x86架构解压 for 操作系统为7
   tar -Jxvf mysql-8.4.7-linux-glibc2.17-x86_64.tar.xz -C /usr/local/
   # 解压 for 操作系统为8/9/10
   #tar -Jxvf mysql-8.4.8-linux-glibc2.28-x86_64.tar.xz -C /usr/local/
   cd /usr/local/
   mv mysql-8.4.7-linux-glibc2.17-x86_64 mysql
   
   # 创建mysql用户组和用户并修改权限
   groupadd mysql
   useradd -r -g mysql mysql
   mkdir -p /usr/local/mysql/data/ /etc/my.cnf.d/ /etc/init.d/
   chown mysql:mysql -R /usr/local/mysql/data
   
   # 修改配置
   vim /etc/my.cnf
   # 配置文件内容：
   [client-server]   
   # include all files from the config directory
   !includedir /etc/my.cnf.d
   [mysqld]
   bind-address=0.0.0.0
   port=13306
   user=mysql
   basedir=/usr/local/mysql
   datadir=/usr/local/mysql/data
   socket=/tmp/mysql.sock
   log-error=/usr/local/mysql/data/mysql.err
   pid-file=/usr/local/mysql/data/mysql.pid
   #character config
   character_set_server=utf8mb4
   symbolic-links=0
   explicit_defaults_for_timestamp=true
   
   # 初始化
   /usr/local/mysql/bin/mysqld --defaults-file=/etc/my.cnf --basedir=/usr/local/mysql/ --datadir=/usr/local/mysql/data --user=mysql --initialize
   # 查看密码
   cat /usr/local/mysql/data/mysql.err
   # 启动数据库
   cp /usr/local/mysql/support-files/mysql.server /etc/init.d/mysql
   service mysql start
   #设置开机自启动
   chkconfig --add /etc/init.d/mysql
   chkconfig mysql on
   #查看服务器自启动情况
   chkconfig
   
   # 进入数据库客户端
   /usr/local/mysql/bin/mysql -u root --port 13306 -p
   
   # 执行nacos数据库脚本, 脚本里面自带数据库创建指令
   source doc/sql/feng_register.sql
   
   # 执行用户中心数据库脚本, 脚本里面自带数据库创建指令
   source doc/sql/feng_user3_biz.sql
   
   # 执行统一消息中心数据库脚本, 脚本里面自带数据库创建指令
   source doc/sql/feng_message_center_biz.sql
   ```   
   
2. **编译项目**

   ```bash
   mvn clean install -DskipTests
   ```

3. 执行systemd服务安装脚本

   ```bash
   # 安装各个模块的systemd服务： 包含六个模块，注意config3模块封装了nacos 3.1.1，admin3模块包含了springboot amin，并封装了redis7.2
   # 注意检查并修改脚本头部的mysql、nacos、reids环境变量
   bash doc/shell/install_systemd.sh
   
   # 查看模块日志，例如查看admin3模块日志
   tail -f /var/log/ump/admin3.log
   
   # 查看systemd服务状态，例如admin3，注意服务名称要加ump-前缀
   systemctl status ump-admin3
   ```
   
   注：systemd服务说明：
   - config3 对应feng-config后台JAVA工程，封装了nacos 3.1.1
   - admin3 对应eng-bootadmin-server后台JAVA工程，封装了springboot amin 4和redis7.2
   - gateway3 对应feng-gateway3后台JAVA工程，封装了spring cloud gateway
   - auth3 对应feng-auth3后台JAVA工程，封装了OAUTH2
   - user3 对应feng-user3-biz后台JAVA工程，提供用户管理中心基础功能和机关代码、组织机构代码、统一角色代码、统一用户代码管理功能
   - center3 对应feng-message-center-biz后台JAVA工程，提供了统一消息中心基础功能
   
4. **启动 Nacos**
   - 确认systemd服务ump_config3已经启动
   - 如果启动失败，除了调整环境变量外，还可以调整feng-config工程内配置文件：feng-base/feng-config/src/main/resources/application.properties，或者通过指定外置config文件启动
   - 管理端要访问 `http://127.0.0.1:2080`，默认用户名/密码：nacos/Feng2025Cloud
   
   ```bash
   # 查看日志
   tail -f /var/log/ump/config3.log
   
   # 查看systemd服务状态
   systemctl status ump-config3
   ```

5. **启动 redis 和 springboot admin**
   - 确认systemd服务ump_admin3已经启动
   - 如果启动失败，除了调整环境变量外，还可以通过指定外置config文件启动
   - 如果调整环境变量还不能解决，那么修改nacos配置文件feng-bootadmin-server-biz-dev.yml文件中嵌入式redis配置
   - 微服务监控管理端要访问 `http://127.0.0.1:12006`，默认用户名/密码：admin/radar
  
   ```bash
   # 查看日志
   tail -f /var/log/ump/admin3.log
   
   # 查看systemd服务状态
   systemctl status ump-admin3
   
   # 连接redis服务器，密码位于nacos配置文件feng-bootadmin-server-biz-dev.ym嵌入式redis配置（最后一段）
   redis-cli -h 192.168.137.115 -p 6379
   > auth 123456
   > set name feng
   > get name
   ```

6. **查看NACOS配置**
   - 进入nacos管理端后，可以查看配置文件（不用导入配置文件，导入feng_register.sql后就已经包含了）
   - 可以调整数据库连接等参数
   - 可以调整redis连接等参数（位于application-dev.yml文件中）

7. **基础服务**
   - 进入nacos管理端后，可以查看服务启动情况
   - 也可以通过以下指令查看
   
   ```bash
   # 查看gateway3日志
   tail -f /var/log/ump/gateway3.log
   
   # 查看systemd服务gateway3状态
   systemctl status ump-gateway3
   
   # 查看auth3日志
   tail -f /var/log/ump/auth3.log
   
   # 查看systemd服务auth3状态
   systemctl status ump-auth3
   
   # 查看user3日志
   tail -f /var/log/ump/user3.log
   
   # 查看systemd服务user3状态
   systemctl status ump-user3
   ```

8. **安装rabbitmq**
   - 统一消息中心业务系统要使用MQ，可以使用RabbitMQ或者kafka，我们选择在Rocky Linux（CentOS略有不同，但区别不大）安装RabbitMQ：
   
   ```bash
   # 更新Rocky Linux的yum源到阿里云
   sed -e 's|^mirrorlist=|#mirrorlist=|g' \
       -e 's|^#baseurl=http://dl.rockylinux.org/$contentdir|baseurl=https://mirrors.aliyun.com/rockylinux|g' \
       -i.bak \
       /etc/yum.repos.d/rocky-*.repo
   # 刷新缓存
   dnf makecache
   # 查看yum源仓库
   dnf repolist
   
   # 使用 Rocky Linux 9 自带 Erlang
   sudo dnf install -y epel-release
   sudo dnf install -y erlang
   # 查看erlang版本
   erl -version
   # 安装wget
   dnf install wget
   # 下载4.2.3版本
   wget https://github.com/rabbitmq/rabbitmq-server/releases/download/v4.2.3/rabbitmq-server-4.2.3-1.el8.noarch.rpm
   
   # 使用rpm安装
   rpm -ivh rabbitmq-server-4.2.3-1.el8.noarch.rpm
   
   # 启动服务
   sudo systemctl enable --now rabbitmq-server
   systemctl status rabbitmq-server
   #验证
   rabbitmqctl status
   
   # 安装后台管理界面
   rabbitmq-plugins enable rabbitmq_management
   systemctl restart rabbitmq-server

   #设置远程登录用户
   sudo rabbitmqctl add_user admin 123456
   #授权为管理员
   sudo rabbitmqctl set_user_tags admin administrator
   #设置vhost权限
   sudo rabbitmqctl set_permissions -p / admin ".*" ".*" ".*"
   # 本机测试
   dnf install net-tools
   netstat -lntp | grep 15672   
   curl http://192.168.137.115:15672
   
   #异地启动浏览器测试，输入http://192.168.137.115:15672，再输入admin/123456 登录（不要输入guest，这个账户不允许远程登录）
   ```
	   
9. **统一消息中心业务**
   - 进入nacos管理端后，可以查看统一消息中心业务系统启动情况
   - 也可以通过以下指令查看
   
   ```bash
   # 查看center3日志
   tail -f /var/log/ump/center3.log
   
   # 查看systemd服务center3状态
   systemctl status ump-center3
    ```

10. **验证**
   - 验证目标为用户的消息发送操作
   
   ```bash
   bash doc/shell/test_send_user.sh
   ```   
   
   - 验证目标为部门的消息发送操作
   
   ```bash
   bash doc/shell/test_send_dept.sh
   ```   

   - 验证目标为用户的消息拉取操作
   
   ```bash
   bash doc/shell/test_query_poll.sh
   ```   

   - 验证目标为部门的消息拉取操作
   
   ```bash
   bash doc/shell/test_query_poll_dept.sh
   ```   

   - 验证目标为用户的未读消息查询操作
   
   ```bash
   bash doc/shell/test_query_unread.sh
   ```   

   - 验证目标为部门的未读消息查询操作
   
   ```bash
   bash doc/shell/test_query_unread_dept.sh
   ```   

   - 验证目标为用户的上报消息已阅读状态
   
   ```bash
   bash doc/shell/test_read.sh
   ```   
   
   - 验证目标为部门的上报消息已阅读状态
   
   ```bash
   bash doc/shell/test_read_dept.sh
   ```   

### 使用 Maven Profile 构建不同版本
- 微服务版（默认）：`mvn clean package -Pcloud`
- 单体版： 暂不支持

## 配置说明

所有配置文件均通过 Nacos 进行管理，各服务在启动时从 Nacos 拉取对应的配置。配置文件名规则为 `{spring.application.name}-{profile}.yaml`。默认 profile 为 `dev`。

主要配置项包括：
- 数据库连接：`spring.datasource`
- Redis 连接：`spring.redis`
- Nacos 服务发现：`spring.cloud.nacos.discovery`
- 认证中心相关：`security.oauth2.*`
- 消息队列：`spring.rabbitmq` 或 `spring.kafka`

## 生产部署

### Docker 镜像构建

Dockerfile编写模板(以feng-config工程为例）：

```
FROM registry.cn-hangzhou.aliyuncs.com/dockerhub_mirror/java:21-anolis
WORKDIR /feng-cloud/feng-config
COPY target/*.jar app.jar
EXPOSE 2848 3848 2080
ENV TZ=Asia/Shanghai JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom"
CMD java $JAVA_OPTS -jar app.jar
```

项目已集成 docker-maven-plugin，可使用以下命令构建镜像：

```bash
mvn clean package docker:build -Pcloud
```

镜像将推送到配置的阿里云容器仓库（见 `docker.registry`）。


### Kubernetes 部署

可参考各模块的 `Dockerfile` 和 `deploy` 目录下的 YAML 示例进行部署。

## 统一消息中心功能介绍

### 一、系统概述

统一消息中心（Unified Message Platform，UMP）是一个面向多业务系统的消息分发与管理平台，旨在提供统一、可靠、可追溯的消息收发能力。平台支持个人点对点消息、部门广播消息、自定义范围消息等多种消息类型，并提供消息推送、拉取、状态回执等完整生命周期管理。

**核心价值：**
- 解耦业务系统与消息分发逻辑
- 支持读扩散（收件箱）与写扩散（广播筒）两种模式，平衡存储与性能
- 实现消息状态全链路追踪（接收、拉取、阅读）
- 提供灵活的推送/拉取模式，适应不同业务场景

### 二、核心功能模块

#### 1. 消息发送
- 支持发送个人消息（USER）、部门消息（DEPT）、自定义范围消息（CUSTOM）等多种接收对象
- 支持模板渲染、优先级设置、过期时间配置
- 支持回调配置，可自定义请求头、签名等
- 提供消息编码申请接口，生成唯一消息编码及条形码/二维码

#### 2. 消息分发
- 根据接收者类型自动选择存储模式：
  - **个人消息**：写入收件箱表（`ump_msg_inbox`），采用读扩散模式，为每个接收者生成一条记录
  - **部门/自定义消息**：写入广播筒表（`ump_msg_broadcast`），采用写扩散模式，仅存储一条广播记录
- 支持分布式任务队列（`ump_msg_queue`），确保消息可靠分发
- 自动处理大批量接收者（超过阈值时转为广播模式）

#### 3. 消息拉取与推送
- **推送模式**：平台主动调用业务方回调地址，实时推送消息
- **拉取模式**：业务方主动调用拉取接口，获取待处理消息（支持游标分页）
- 支持消息重试机制，失败后可自动重试

#### 4. 消息状态上报
业务系统可通过统一接口上报消息处理状态，实现精准状态追踪：
- **已接收**：业务系统确认收到消息（`BIZ_RECEIVED`）
- **已拉取**：业务系统拉取消息后（`BIZ_PULLED`）
- **已阅读**：用户阅读消息（`READ`）

状态上报采用幂等设计，确保数据一致性。

#### 5. 消息查询
- **未读消息查询**：按用户身份证号查询所有未读消息（个人+广播）
- **游标拉取**：支持分页拉取未被业务系统接收的消息，避免重复
- **消息详情查询**：按消息编码或ID查询完整消息内容

#### 6. 应用认证与管理
- 支持基于 AppKey + AppSecret 的应用认证，生成 JWT Token
- 支持应用密钥刷新
- 提供服务器时间戳接口，用于签名防重放

### 三、技术设计要点

#### 1. 读写扩散策略
- **个人消息**：使用收件箱表，每条消息为每个接收者生成一条记录，查询时只需扫描单表，适合小范围精准送达。
- **部门/自定义消息**：使用广播筒表，仅存储一条广播记录，接收记录按需创建（仅当业务方上报状态时），避免海量数据存储。

#### 2. 广播接收记录按需创建
- 接收记录表（`ump_broadcast_receive_record`）仅在业务系统上报接收、拉取或阅读时创建，采用 `UPSERT` 方式保证幂等性。
- 未上报的消息在广播筒表中查询时，通过“候选广播集 - 已有记录”得到未处理消息，无需预创建记录。

#### 3. 游标分页
- 拉取接口使用游标（`cursorId`）代替传统分页，避免深度分页性能问题。
- 游标格式：`时间戳,ID`，确保顺序性与唯一性。

#### 4. 状态机管理
- 消息主表（`ump_msg_main`）状态流转严格遵循状态机，确保状态变更合法。
- 支持重试状态（`DIST_RETRY`、`PUSH_RETRY`），失败消息可自动重试。

#### 5. 重试机制
- 分发失败或推送失败的消息会进入队列任务表，定时调度任务负责重试。
- 重试策略支持指数退避，超过最大次数后标记为永久失败。

### 四、开放式API接口

| 接口路径 | 方法 | 功能描述 | 认证要求 |
|---------|------|----------|----------|
| `/open/message/send` | POST | 发送消息 | JWT Token |
| `/open/message/poll` | POST | 拉取消息（游标分页） | JWT Token |
| `/open/message/unread` | POST | 查询未读消息 | JWT Token |
| `/open/message/received` | POST | 上报消息已接收 | JWT Token |
| `/open/message/pulled` | POST | 上报消息已拉取 | JWT Token |
| `/open/message/read` | POST | 上报消息已阅读 | JWT Token |
| `/open/refreshSecret` | PUT | 刷新应用密钥 | JWT Token |
| `/open/getTimestamp` | GET | 获取服务器时间戳 | JWT Token（需配置） |
| `/open/applyCode` | POST | 申请消息编码 | JWT Token |
| `/oauth2/token` | POST | 获取访问令牌 | AppKey签名认证 |

> 注：部分接口可能需在安全配置中放行，实际以部署环境为准。

### 五、核心数据模型

| 表名 | 描述 | 关键字段 |
|------|------|----------|
| `ump_msg_main` | 消息主表 | id, msg_code, title, content, status, sender_app_key, send_target_type, callback_url |
| `ump_msg_inbox` | 收件箱表 | id, msg_id, receiver_id, receiver_type, read_status, distribute_time |
| `ump_msg_broadcast` | 广播筒表 | id, msg_id, broadcast_type, receiving_unit_code, receiving_scope, total_receivers |
| `ump_broadcast_receive_record` | 广播接收记录表 | broadcast_id, receiver_id, receiver_type, receive_status, read_status |
| `ump_msg_queue` | 消息队列任务表 | id, msg_id, queue_type, status, current_retry, next_retry_time |
| `ump_poll_cursor` | 拉取游标表 | app_key, cursor_key, cursor_id, last_poll_time |

### 六、典型业务流程

#### 1. 发送个人消息
1. 业务系统调用 `/open/message/send`，携带 `fsdx=USER`、`jsrzjhm`（接收人身份证号）。
2. 平台验证权限，生成消息主记录，同时向收件箱插入一条记录。
3. 消息进入队列任务，分发后推送至业务方回调地址或等待业务方拉取。

#### 2. 发送部门广播消息
1. 业务系统调用 `/open/message/send`，携带 `fsdx=DEPT`、`jsdwdm`（部门代码）。
2. 平台生成消息主记录和广播筒记录，不预创建接收记录。
3. 业务系统拉取消息时，平台返回所有未处理的广播（即该部门未上报接收的广播）。

#### 3. 拉取消息并上报已接收
1. 业务系统调用 `/open/message/poll`，携带上次返回的游标或首次为空。
2. 平台返回未处理的消息列表（个人消息按未读过滤，广播消息按未上报过滤）。
3. 业务系统处理后调用 `/open/message/received` 上报已接收，平台创建/更新广播接收记录。

### 七、总结

统一消息中心通过灵活的存储策略、完善的状态管理、可靠的队列重试，为多业务系统提供了统一的消息收发能力。平台支持推送与拉取两种模式，满足实时性与主动查询的不同需求，同时通过状态回执确保消息送达可审计、可追溯。该设计已在消防系统等多个业务场景中验证，具备良好的扩展性与稳定性。

## 贡献指南

欢迎提交 Issue 和 Pull Request。

1. Fork 本仓库
2. 创建你的特性分支 (`git checkout -b feature/fooBar`)
3. 提交你的修改 (`git commit -am 'Add some fooBar'`)
4. 推送到分支 (`git push origin feature/fooBar`)
5. 创建新的 Pull Request

请确保代码风格符合 `spring-javaformat-maven-plugin` 的规范，可通过 `mvn spring-javaformat:apply` 自动格式化。

## 许可证

本项目基于 [Apache License 2.0](LICENSE) 开源。

本项目包含了 [lengleng/pig](https://gitee.com/log4j/pig) 的部分代码，原始代码遵循 BSD 3-Clause License。我们在保留原始版权声明的前提下进行了修改和扩展。详情请参阅各文件头部的版权声明及根目录的 `LICENSE` 文件。

## 鸣谢

- [lengleng/pig](https://gitee.com/log4j/pig) - 提供了优秀的微服务基础架构
- [Spring Cloud Alibaba](https://github.com/alibaba/spring-cloud-alibaba) - 强大的微服务生态
- [Nacos](https://nacos.io) - 服务发现与配置管理
- [MyBatis Plus](https://baomidou.com) - 高效的 ORM 工具
- [Hutool](https://hutool.cn) - Java 工具库