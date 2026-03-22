package ltd.huntinginfo.feng.common.sentinel.handle;

import cn.hutool.json.JSONUtil;

import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import ltd.huntinginfo.feng.common.core.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author edison
 * @date 2019-10-11
 * <p>
 * 降级 限流策略
 */
@Slf4j
public class FengUrlBlockHandler implements BlockExceptionHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, String resourceName, BlockException e)
			throws Exception {
		log.error("sentinel 降级 资源名称{}", resourceName);
		response.setContentType("application/json");
		response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
		response.getWriter().print(JSONUtil.toJsonStr(R.failed(e.getMessage())));
		
	}

}
