//package ltd.huntinginfo.feng.common.rabbitmq.config;
//
//import org.springframework.amqp.rabbit.annotation.EnableRabbit;
//import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
//import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.amqp.autoconfigure.RabbitProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.retry.interceptor.RetryInterceptorBuilder;
//
///**
// * rabbit 侦听配置类，
// * 若删除本类，则若存在spring.rabbitmq.listener.simple，则Spring Boot 自动配置
// */
//@EnableRabbit
//@Configuration
//public class RabbitListenerConfig {
//
//    @Autowired
//    private RabbitProperties rabbitProperties;
//
//    @Bean
//    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(
//            ConnectionFactory connectionFactory,
//            JacksonJsonMessageConverter messageConverter) {
//
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setMessageConverter(messageConverter);
//
//        // 直接从 Spring Boot 的配置属性中读取
//        RabbitProperties.SimpleContainer containerConfig = rabbitProperties.getListener().getSimple();
//        factory.setConcurrentConsumers(containerConfig.getConcurrency());
//        factory.setMaxConcurrentConsumers(containerConfig.getMaxConcurrency());
//        factory.setPrefetchCount(containerConfig.getPrefetch());
//        factory.setAcknowledgeMode(containerConfig.getAcknowledgeMode());
//
//        // 重试配置
//        if (containerConfig.getRetry().isEnabled()) {
//            RetryInterceptorBuilder<?> builder = RetryInterceptorBuilder.stateless()
//                    .maxAttempts((int)containerConfig.getRetry().getMaxRetries())
//                    .backOffOptions(
//                            containerConfig.getRetry().getInitialInterval().getSeconds(),
//                            containerConfig.getRetry().getMultiplier(),
//                            containerConfig.getRetry().getMaxInterval().getSeconds()
//                    );
//            factory.setAdviceChain(builder.build());
//        }
//
//        return factory;
//    }
//}