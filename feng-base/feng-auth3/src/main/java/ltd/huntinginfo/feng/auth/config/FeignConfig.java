//package ltd.huntinginfo.feng.auth.config;
//
//import feign.FeignException;
//import feign.Response;
//import feign.codec.Decoder;
//import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode; // 注意这里引入的是 HttpStatusCode
//import org.springframework.http.client.ClientHttpResponse;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.converter.StringHttpMessageConverter;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.web.client.HttpMessageConverterExtractor;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.reflect.Type;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Spring Boot 4.0.2 / Spring Framework 7.0.2 兼容配置
// * 修复了 ClientHttpResponse 接口不兼容的问题
// */
//@Configuration(proxyBeanMethods = false)
//public class FeignConfig {
//
//    @Bean
//    public Decoder feignDecoder() {
//        // 1. 准备转换器列表
//        var converters = new ArrayList<HttpMessageConverter<?>>();
//        
//        // String 转换器 (UTF-8)
//        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
//        // Jackson 转换器
//        converters.add(new MappingJackson2HttpMessageConverter());
//
//        // 2. 返回解码器链
//        return new ResponseEntityDecoder(new SimpleSpringDecoder(converters));
//    }
//
//    /**
//     * 自定义解码器：桥接 Feign 与 Spring HttpMessageConverter
//     */
//    public static class SimpleSpringDecoder implements Decoder {
//        private final List<HttpMessageConverter<?>> converters;
//
//        public SimpleSpringDecoder(List<HttpMessageConverter<?>> converters) {
//            this.converters = converters;
//        }
//
//        @Override
//        public Object decode(Response response, Type type) throws IOException, FeignException {
//            if (type == void.class || response.body() == null) {
//                return null;
//            }
//
//            // 包装 Response
//            ClientHttpResponse clientResponse = new FeignClientHttpResponse(response);
//
//            // 使用 Spring 提取器提取数据
//            @SuppressWarnings({"unchecked", "rawtypes"})
//            var extractor = new HttpMessageConverterExtractor(type, this.converters);
//
//            return extractor.extractData(clientResponse);
//        }
//    }
//
//    /**
//     * 内部适配类：修复了 Spring 6/7 中 ClientHttpResponse 接口变更的问题
//     */
//    private static class FeignClientHttpResponse implements ClientHttpResponse {
//        private final Response response;
//
//        public FeignClientHttpResponse(Response response) {
//            this.response = response;
//        }
//
//        @Override
//        public HttpStatusCode getStatusCode() throws IOException {
//            // Spring 6+ 返回 HttpStatusCode 而不是 HttpStatus
//            return HttpStatus.valueOf(response.status());
//        }
//
//        // 注意：getRawStatusCode() 方法已在 Spring 6+ 的接口中移除，因此这里删除了该方法
//
//        @Override
//        public String getStatusText() throws IOException {
//            return response.reason();
//        }
//
//        @Override
//        public void close() {
//            if (response.body() != null) {
//                try {
//                    response.body().close();
//                } catch (IOException e) {
//                    // ignore
//                }
//            }
//        }
//
//        @Override
//        public InputStream getBody() throws IOException {
//            return response.body().asInputStream();
//        }
//
//        @Override
//        public HttpHeaders getHeaders() {
//            var headers = new HttpHeaders();
//            for (var entry : response.headers().entrySet()) {
//                // 将 Collection 转为 List
//                headers.put(entry.getKey(), new ArrayList<>(entry.getValue()));
//            }
//            return headers;
//        }
//    }
//}