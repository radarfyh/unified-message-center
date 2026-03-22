package ltd.huntinginfo.feng.admin.api.feign;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ltd.huntinginfo.feng.common.core.constant.ServiceNameConstants;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.feign.annotation.NoToken;

/**
 * 区域服务Feign客户端
 */
@FeignClient(contextId = "remoteDivisionService", value = ServiceNameConstants.USER3_SERVICE)
public interface RemoteDivisionService {

    @NoToken
    @GetMapping("/administrative-division/code/{divisionCode}")
    R<Map<String, Object>> getDivisionByCode(@PathVariable String divisionCode);

    @NoToken
    @PostMapping("/administrative-division/list-by-codes")
    R<List<Map<String, Object>>> listDivisionsByCodes(@RequestBody List<String> divisionCodes);

}
