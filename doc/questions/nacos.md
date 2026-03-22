# NACOS问题


## 1. ErrCode:-401

### nacos 3.1 告警：

```
2026-02-07 12:25:48.887  INFO 968 --- [-ElectionTimer5] com.alipay.sofa.jraft.core.NodeImpl      : Node <naming_persistent_service/192.168.137.1:7848> term 6 start preVote.
2026-02-07 12:25:48.887  WARN 968 --- [-ElectionTimer5] com.alipay.sofa.jraft.core.NodeImpl      : Node <naming_persistent_service/192.168.137.1:7848> can't do preVote as it is not in conf <ConfigurationEntry [id=LogId [index=6, term=6], conf=192.168.40.2:7848, oldConf=]>.
2026-02-07 12:25:49.972  INFO 968 --- [ElectionTimer11] com.alipay.sofa.jraft.core.NodeImpl      : Node <naming_instance_metadata/192.168.137.1:7848> term 6 start preVote.
2026-02-07 12:25:49.972  INFO 968 --- [-ElectionTimer1] com.alipay.sofa.jraft.core.NodeImpl      : Node <lock_acquire_service_v2/192.168.137.1:7848> term 2 start preVote.
2026-02-07 12:25:49.972  WARN 968 --- [ElectionTimer11] com.alipay.sofa.jraft.core.NodeImpl      : Node <naming_instance_metadata/192.168.137.1:7848> can't do preVote as it is not in conf <ConfigurationEntry [id=LogId [index=6, term=6], conf=192.168.40.2:7848, oldConf=]>.
2026-02-07 12:25:49.972  WARN 968 --- [-ElectionTimer1] com.alipay.sofa.jraft.core.NodeImpl      : Node <lock_acquire_service_v2/192.168.137.1:7848> can't do preVote as it is not in conf <ConfigurationEntry [id=LogId [index=2, term=2], conf=192.168.40.2:7848, oldConf=]>.
2026-02-07 12:25:49.987  INFO 968 --- [ElectionTimer12] com.alipay.sofa.jraft.core.NodeImpl      : Node <naming_service_metadata/192.168.137.1:7848> term 6 start preVote. 
```

### 客户端报错：

```
Caused by: ErrCode:-401, ErrMsg:Client not connected, current status:STARTING 	at com.alibaba.nacos.common.remote.client.RpcClient.request(RpcClient.java:647)
	at com.alibaba.nacos.common.remote.client.RpcClient.request(RpcClient.java:626)
	at com.alibaba.nacos.client.naming.remote.gprc.NamingGrpcClientProxy.requestToServer(NamingGrpcClientProxy.java:482)
	at com.alibaba.nacos.client.naming.remote.gprc.NamingGrpcClientProxy.doRegisterService(NamingGrpcClientProxy.java:267)
	at com.alibaba.nacos.client.naming.remote.gprc.NamingGrpcClientProxy.registerServiceForEphemeral(NamingGrpcClientProxy.java:162)
	at com.alibaba.nacos.client.naming.remote.gprc.NamingGrpcClientProxy.registerService(NamingGrpcClientProxy.java:153)
	at com.alibaba.nacos.client.naming.remote.NamingClientProxyDelegate.registerService(NamingClientProxyDelegate.java:98)
	at com.alibaba.nacos.client.naming.NacosNamingService.registerInstance(NacosNamingService.java:174)
	at com.alibaba.cloud.nacos.registry.NacosServiceRegistry.register(NacosServiceRegistry.java:74)
	... 27 common frames omitted
	
```

### 原因分析：

你的 Nacos 3.1 节点 192.168.137.1:7848 认为自己是集群成员，但实际 Raft 配置里只有 192.168.40.2:7848，导致该节点被 Raft 拒绝参与选举，Nacos 一直处于 STARTING 状态，客户端因此报 -401。

实际上是，IP 发生过变化，我手工修改过ip地址

### 解决办法：

修改本地缓存 （我在windows 10上运行，集群配置本地缓存在：
C:\Users\abbym\nacos\conf\cluster.conf
C:\Users\abbym\nacos\data\protocol\raft\naming_instance_metadata\meta-data\raft_meta 等目录)

删除缓存目录C:\Users\abbym\nacos