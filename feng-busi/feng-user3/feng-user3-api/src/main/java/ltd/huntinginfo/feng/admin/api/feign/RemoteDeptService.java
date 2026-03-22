package ltd.huntinginfo.feng.admin.api.feign;

import ltd.huntinginfo.feng.common.core.constant.ServiceNameConstants;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.feign.annotation.NoToken;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 部门服务Feign客户端
 */
@FeignClient(contextId = "remoteDeptService", value = ServiceNameConstants.USER3_SERVICE)
public interface RemoteDeptService {

    @NoToken
    @GetMapping("/dept/{id}")
    R<Map<String, Object>> getDeptById(@PathVariable String id);

    @NoToken
    @PostMapping("/dept/listByIds")
    R<List<Map<String, Object>>> listDeptsByIds(@RequestBody List<String> deptIds);

    @NoToken
    @GetMapping("/dept/treeForFeign")
    R<List<Map<String, Object>>> getDeptTree(@RequestParam(required = false) String deptName);

    @NoToken
    @GetMapping("/dept/getDescendantListForFeign/{deptId}")
    R<List<Map<String, Object>>> getDescendantList(@PathVariable String deptId);
}
