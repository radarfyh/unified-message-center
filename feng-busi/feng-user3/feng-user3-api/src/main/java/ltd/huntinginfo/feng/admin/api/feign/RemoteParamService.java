package ltd.huntinginfo.feng.admin.api.feign;

import ltd.huntinginfo.feng.common.core.constant.ServiceNameConstants;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.feign.annotation.NoToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 远程参数服务接口
 * <p>
 * 通过Feign客户端调用USER3服务获取参数配置
 * </p>
 *
 * @author lengleng
 * @date 2025/05/30
 * @see FeignClient
 */
@FeignClient(contextId = "remoteParamService", value = ServiceNameConstants.USER3_SERVICE)
public interface RemoteParamService {

	/**
	 * 通过key 查询参数配置
	 * @param key key
	 * @NoToken 声明成内部调用，避免MQ 等无法调用
	 */
	@NoToken
	@GetMapping("/param/publicValue/{key}")
	R<String> getByKey(@PathVariable("key") String key);

}
