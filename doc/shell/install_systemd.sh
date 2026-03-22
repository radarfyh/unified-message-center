#!/bin/bash
echo '>>> 安装UMP后台服务'

# 创建用户
sudo useradd -r -s /sbin/nologin ump

# 设置 /feng-cloud 目录权限
sudo chown -R ump:ump /feng-cloud/feng-*

# 确保日志目录存在（如果应用需要写日志到特定位置，可提前创建）
sudo mkdir -p /var/log/ump /feng-cloud/logs
sudo chown ump:ump  -R /var/log/ump /feng-cloud/logs

sudo mkdir -p /feng-cloud/feng-gateway3/1.0.1/logs
sudo mkdir -p /feng-cloud/feng-auth3/1.0.1/logs
sudo mkdir -p /feng-cloud/feng-bootadmin-server/1.0.1/logs
sudo mkdir -p /feng-cloud/feng-config/1.0.1/logs
sudo mkdir -p /feng-cloud/feng-user3-biz/1.0.1/logs
sudo mkdir -p /feng-cloud/feng-message-center-biz/1.0.1/logs

sudo chown ump:ump  -R /feng-cloud/feng-gateway3/1.0.1/logs
sudo chown ump:ump  -R /feng-cloud/feng-auth3/1.0.1/logs
sudo chown ump:ump  -R /feng-cloud/feng-bootadmin-server/1.0.1/logs
sudo chown ump:ump  -R /feng-cloud/feng-config/1.0.1/logs
sudo chown ump:ump  -R /feng-cloud/feng-user3-biz/1.0.1/logs
sudo chown ump:ump  -R /feng-cloud/feng-message-center-biz/1.0.1/logs

# 生成systemd服务文件

# 定义应用列表及其对应的子目录和 JAR 文件名
apps=(
    "config3:feng-config"
    "admin3:feng-bootadmin-server"
    "gateway3:feng-gateway3"
    "auth3:feng-auth3"
    "user3:feng-user3-biz"
    "center3:feng-message-center-biz"
)

# 基础环境变量（所有服务共用）
ENV_VARS='Environment="NACOS_DB_HOST=192.168.137.115"
Environment="NACOS_DB_PORT=13306"
Environment="NACOS_DB_NAME=feng_register"
Environment="NACOS_DB_USER=feng_cloud"
Environment="NACOS_DB_PWD=123456"
Environment="NACOS_HOST=127.0.0.1"
Environment="NACOS_PORT=2848"
Environment="REDIS_HOST=127.0.0.1"
Environment="REDIS_PASSWORD=123456"
Environment="REDIS_PORT=6379"
Environment="REDIS_DATABASE=0"
Environment="USR_DB_HOST=192.168.137.115"
Environment="USR_DB_PORT=13306"
Environment="USR_DB_NAME=feng_user3_biz"
Environment="USR_DB_USERNAME=feng_cloud"
Environment="USR_DB_PASSWORD=123456"
Environment="UMP_DB_HOST=192.168.137.115"
Environment="UMP_DB_PORT=13306"
Environment="UMP_DB_NAME=unified_message_platform"
Environment="UMP_DB_USERNAME=root"
Environment="UMP_DB_PASSWORD=123456"
'

# 循环生成 service 文件
for i in "${!apps[@]}"; do
    app="${apps[$i]}"
    # 拆分服务名和目录名
    IFS=":" read -r service_name dir_name <<< "$app"
    
    # 构建 JAR 路径
    jar_path="/feng-cloud/${dir_name}/1.0.1/${dir_name}-1.0.1.jar"
    
    # 服务文件路径
    service_file="/etc/systemd/system/ump-${service_name}.service"
    
    # 计算延迟时间：config3 优先启动无延迟，其他服务错峰 5 秒
    if [ "$service_name" = "config3" ]; then
        delay=0
    else
        delay=$((i * 5 + 5))
    fi

    # 构建依赖关系：所有非 config3 服务都依赖 config3
    AFTER="network.target"
    WANTS=""
    if [ "$service_name" != "config3" ]; then
        AFTER="$AFTER ump-config3.service"
        WANTS="$WANTS ump-config3.service"
    fi
	
	if [ "$service_name" != "admin3" ]; then
        AFTER="$AFTER ump-admin3.service"
        WANTS="$WANTS ump-admin3.service"
    fi

    # 写入服务文件
    sudo bash -c "cat > $service_file" <<EOF
[Unit]
Description=UMP ${service_name^} Service
After=$AFTER
Wants=$WANTS

[Service]
Type=simple
User=ump
Group=ump
WorkingDirectory=/feng-cloud
$ENV_VARS
ExecStartPre=/bin/sleep $delay
ExecStart=/usr/local/java/jdk-21.0.10/bin/java -Xms128m -Xmx256m -Xmn64m -jar $jar_path
ExecStop=/bin/kill -s TERM \$MAINPID
Restart=on-failure
RestartSec=60
TimeoutStartSec=600
StandardOutput=append:/var/log/ump/${service_name}.log
StandardError=append:/var/log/ump/${service_name}.log
LimitNOFILE=65535

[Install]
WantedBy=multi-user.target
EOF

    echo "Generated $service_file (delay ${delay}s)"
done

# 创建 ump 用户目录
sudo mkdir -p /home/ump
sudo chown ump:ump -R /home/ump

# 重新加载 systemd 配置
sudo systemctl daemon-reload

# 开机自启和启动
for svc in ump-{gateway3,auth3,admin3,config3,user3,center3}; do
    sudo systemctl enable --now "$svc"
done