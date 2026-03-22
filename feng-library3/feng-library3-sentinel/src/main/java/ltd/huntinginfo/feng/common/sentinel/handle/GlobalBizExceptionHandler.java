package ltd.huntinginfo.feng.common.sentinel.handle;

import com.alibaba.csp.sentinel.Tracer;
import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.common.core.util.R;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * <p>
 * 全局异常处理器结合sentinel 全局异常处理器不能作用在 oauth server
 * </p>
 *
 * @author edison
 * @date 2020-06-29
 */
@Slf4j
@RestController
@RestControllerAdvice
@Tag(name = "全局异常处理器")
public class GlobalBizExceptionHandler {

    // 使用 Jackson 3.x 推荐的建造者模式创建 ObjectMapper
    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    /**
     * 全局异常.
     * @param e the e
     * @return R
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R handleGlobalException(Exception e) {

        log.error("全局异常信息 ex={}", e.getMessage(), e);

        // 业务异常交由 sentinel 记录
        Tracer.trace(e);
        return R.failed(e.getLocalizedMessage());
    }

    @SneakyThrows
    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R handleGlobalException(FeignException e) {
        log.error("全局异常信息 ex={}", e.getMessage(), e);

        // 业务异常交由 sentinel 记录
        Tracer.trace(e);

        if (e.responseBody().isPresent()) {
            // readValue 在 Jackson 3.x 中可能抛出 JacksonException（运行时异常），但 @SneakyThrows 仍适用
            return objectMapper.readValue(e.responseBody().get().array(), R.class);
        }

        return R.failed(e.getLocalizedMessage());
    }

    /**
     * AccessDeniedException
     * @param e the e
     * @return R
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R handleAccessDeniedException(AccessDeniedException e) {
        log.error("拒绝授权异常信息 ex={}", e.getMessage());
        return R.failed("权限不足，不允许访问");
    }

    /**
     * validation Exception
     * @param exception
     * @return R
     */
    @ExceptionHandler({ BindException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R handleBodyValidException(BindException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        // 插入log 的逻辑
        return R.failed(String.format("%s %s", fieldErrors.get(0).getField(), fieldErrors.get(0).getDefaultMessage()));
    }

    /**
     * 避免 404 重定向到 /error 导致NPE ,ignore-url 需要配置对应端点
     * @return R
     */
    @DeleteMapping("/error")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @Operation(summary = "返回失败，消除潜在NPE（空指针异常）")
    public R noHandlerFoundException() {
        return R.failed(HttpStatus.NOT_FOUND.getReasonPhrase());
    }

}