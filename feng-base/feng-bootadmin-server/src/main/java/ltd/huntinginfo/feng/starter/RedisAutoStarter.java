//package ltd.huntinginfo.feng.starter;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import ltd.huntinginfo.feng.config.EmbeddedRedisConfig;
//
//@Component
//@Profile("!test") // 在非测试环境下启用
//@Slf4j
//@RequiredArgsConstructor
//public class RedisAutoStarter implements CommandLineRunner {
//    
//    private final EmbeddedRedisConfig embeddedRedisConfig;
//
//    @Override
//    public void run(String... args) throws Exception {
//        try {
//            embeddedRedisConfig.startRedis();
//            log.info("Embedded Redis server started successfully on port {}", 
//                   embeddedRedisConfig.getRedisServer().ports());
//        } catch (Exception e) {
//            log.error("Failed to start embedded Redis server", e);
//        }
//    }
//}