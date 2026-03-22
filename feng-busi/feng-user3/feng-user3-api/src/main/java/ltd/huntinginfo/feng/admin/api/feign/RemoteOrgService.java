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
 * 组织服务Feign客户端
 */
@FeignClient(contextId = "remoteOrgService", value = ServiceNameConstants.USER3_SERVICE)
public interface RemoteOrgService {

    @NoToken
    @GetMapping("/org/getByIdForFeign/{id}")
    R<Map<String, Object>> getOrgById(@PathVariable String id);

    @NoToken
    @PostMapping("/org/listByIdsForFeign")
    R<List<Map<String, Object>>> listOrgsByIds(@RequestBody List<String> orgIds);

    @NoToken
    @PostMapping("/org/listUsersByOrgForFeign")
    R<List<Map<String, Object>>> listUsersByOrg(@RequestBody Map<String, Object> query);
}