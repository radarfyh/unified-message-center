//package ltd.huntinginfo.redis.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//
//import de.codecentric.boot.admin.server.web.AdminController;
//
//@Configuration
//public class AdminUiExtensionConfig {
//
//    @Bean
//    public ExtendableAdminUI adminUI(AdminController adminController) {
//        ExtendableAdminUI ui = new ExtendableAdminUI(adminController);
//        
//        // 添加自定义 JS 文件
//        ui.addExtensionScript(new ClassPathResource("static/extension-redis-controls.js"));
//        
//        return ui;
//    }
//}