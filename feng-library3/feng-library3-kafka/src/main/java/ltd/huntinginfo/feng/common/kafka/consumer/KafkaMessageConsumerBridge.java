//package ltd.huntinginfo.feng.common.kafka.consumer;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
//import ltd.huntinginfo.feng.common.core.mq.consumer.MqMessageConsumer;
//import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
///**
// * Kafka 消费者桥接器
// * <p>
// * 监听统一常量中定义的队列名（作为 Topic），调用业务层实现的 {@link MqMessageConsumer} 接口。
// * 仅在 mc.mq.type = kafka 且存在 MqMessageConsumer Bean 时生效。
// * </p>
// */
//@Slf4j
//@Component
//@ConditionalOnProperty(name = MqMessageEventConstants.ConfigKeys.MQ_TYPE, havingValue = "kafka")
//@ConditionalOnBean(MqMessageConsumer.class)
//@RequiredArgsConstructor
//public class KafkaMessageConsumerBridge {
//
//    private final MqMessageConsumer messageConsumer;
//
//    // -------------------- 消息状态事件 --------------------
//    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_RECEIVED,
//                   groupId = MqMessageEventConstants.ConsumerGroups.MESSAGE_STATE)
//    public void handleMessageReceived(MqMessage<Map<String, Object>> message) {
//        messageConsumer.handleMessageReceived(message);
//    }
//
//    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_DISTRIBUTED,
//                   groupId = MqMessageEventConstants.ConsumerGroups.MESSAGE_STATE)
//    public void handleMessageDistributed(MqMessage<Map<String, Object>> message) {
//        messageConsumer.handleMessageDistributed(message);
//    }
//
//    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_SENT,
//                   groupId = MqMessageEventConstants.ConsumerGroups.MESSAGE_STATE)
//    public void handleMessageSent(MqMessage<Map<String, Object>> message) {
//        messageConsumer.handleMessageSent(message);
//    }
//
//    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_READ,
//                   groupId = MqMessageEventConstants.ConsumerGroups.MESSAGE_STATE)
//    public void handleMessageRead(MqMessage<Map<String, Object>> message) {
//        messageConsumer.handleMessageRead(message);
//    }
//
//    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_EXPIRED,
//                   groupId = MqMessageEventConstants.ConsumerGroups.MESSAGE_STATE)
//    public void handleMessageExpired(MqMessage<Map<String, Object>> message) {
//        messageConsumer.handleMessageExpired(message);
//    }
//
//    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_FAILED,
//                   groupId = MqMessageEventConstants.ConsumerGroups.MESSAGE_STATE)
//    public void handleMessageFailed(MqMessage<Map<String, Object>> message) {
//        messageConsumer.handleMessageFailed(message);
//    }
//
//    // -------------------- 异步任务 --------------------
//    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_SEND_TASK,
//                   groupId = MqMessageEventConstants.ConsumerGroups.TASK)
//    public void handleSendTask(MqMessage<Map<String, Object>> message) {
//        messageConsumer.handleSendTask(message);
//    }
//
//    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_CALLBACK_TASK,
//                   groupId = MqMessageEventConstants.ConsumerGroups.TASK)
//    public void handleCallbackTask(MqMessage<Map<String, Object>> message) {
//        messageConsumer.handleCallbackTask(message);
//    }
//
//    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_RETRY_TASK,
//                   groupId = MqMessageEventConstants.ConsumerGroups.TASK)
//    public void handleRetryTask(MqMessage<Map<String, Object>> message) {
//        messageConsumer.handleRetryTask(message);
//    }
//
//    @KafkaListener(topics = MqMessageEventConstants.Queues.BROADCAST_DISPATCH_TASK,
//                   groupId = MqMessageEventConstants.ConsumerGroups.TASK)
//    public void handleBroadcastDispatchTask(MqMessage<Map<String, Object>> message) {
//        messageConsumer.handleBroadcastDispatchTask(message);
//    }
//
//    // -------------------- 延迟任务 --------------------
//    @KafkaListener(topics = MqMessageEventConstants.Queues.DELAYED_SEND,
//                   groupId = MqMessageEventConstants.ConsumerGroups.DELAYED)
//    public void handleDelayedSendTask(MqMessage<Map<String, Object>> message) {
//        messageConsumer.handleDelayedSendTask(message);
//    }
//
//    @KafkaListener(topics = MqMessageEventConstants.Queues.DELAYED_EXPIRE,
//                   groupId = MqMessageEventConstants.ConsumerGroups.DELAYED)
//    public void handleDelayedExpireTask(MqMessage<Map<String, Object>> message) {
//        messageConsumer.handleDelayedExpireTask(message);
//    }
//}