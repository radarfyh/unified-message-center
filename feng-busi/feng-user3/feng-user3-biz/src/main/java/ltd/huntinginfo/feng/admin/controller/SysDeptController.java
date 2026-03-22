package ltd.huntinginfo.feng.admin.controller;

import ltd.huntinginfo.feng.admin.api.entity.SysDept;
import ltd.huntinginfo.feng.admin.api.vo.DeptExcelVo;
import ltd.huntinginfo.feng.admin.service.SysDeptService;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.feign.annotation.NoToken;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;
import ltd.huntinginfo.feng.common.security.annotation.HasPermission;
import ltd.huntinginfo.feng.common.security.annotation.Inner;

import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.hutool.core.lang.tree.Tree;

/**
 * 部门管理前端控制器
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dept")
@Tag(description = "dept", name = "部门管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysDeptController {

	private final SysDeptService sysDeptService;

	/**
	 * 通过ID查询部门信息
	 * @param id 部门ID
	 * @return 包含部门信息的响应对象
	 */
	@GetMapping("/{id}")
	@Operation(summary = "通过ID查询部门信息", description = "通过ID查询部门信息")
	public R getById(@PathVariable String id) {
		return R.ok(sysDeptService.getById(id));
	}

	/**
	 * 查询全部部门列表
	 * @return 包含全部部门列表的响应结果
	 */
	@GetMapping("/list")
	@Operation(summary = "查询全部部门列表", description = "查询全部部门列表")
	public R listDepts() {
		return R.ok(sysDeptService.list());
	}

	/**
	 * 获取树形菜单
	 * @param deptName 部门名称
	 * @return 包含树形菜单的响应结果
	 */
	@GetMapping(value = "/tree")
	@Operation(summary = "获取树形菜单", description = "获取树形菜单")
	public R getDeptTree(String deptName) {
		return R.ok(sysDeptService.getDeptTree(deptName));
	}

	/**
	 * 保存部门信息
	 * @param sysDept 部门实体
	 * @return 操作结果
	 */
	@SysLog("添加部门")
	@PostMapping
	@HasPermission("sys_dept_add")
	@Operation(summary = "保存部门信息", description = "保存部门信息")
	public R saveDept(@Valid @RequestBody SysDept sysDept) {
		return R.ok(sysDeptService.save(sysDept));
	}

	/**
	 * 根据ID删除部门
	 * @param id 部门ID
	 * @return 操作结果，成功返回true，失败返回false
	 */
	@SysLog("删除部门")
	@DeleteMapping("/{id}")
	@HasPermission("sys_dept_del")
	@Operation(summary = "根据ID删除部门", description = "根据ID删除部门")
	public R removeById(@PathVariable String id) {
		return R.ok(sysDeptService.removeDeptById(id));
	}

	/**
	 * 编辑部门信息
	 * @param sysDept 部门实体对象
	 * @return 操作结果，成功返回success，失败返回false
	 */
	@SysLog("编辑部门")
	@PutMapping
	@HasPermission("sys_dept_edit")
	@Operation(summary = "编辑部门信息", description = "编辑部门信息")
	public R updateDept(@Valid @RequestBody SysDept sysDept) {
		sysDept.setUpdateTime(LocalDateTime.now());
		return R.ok(sysDeptService.updateById(sysDept));
	}

	/**
	 * 获取部门子级列表
	 * @param deptId 部门ID
	 * @return 包含子级部门列表的响应结果
	 */
	@GetMapping(value = "/getDescendantList/{deptId}")
	@Operation(summary = "获取部门子级列表", description = "获取部门子级列表")
	public R getDescendantList(@PathVariable String deptId) {
		return R.ok(sysDeptService.listDescendants(deptId));
	}

	/**
	 * 导出部门数据
	 * @return 部门数据列表
	 */
	@ResponseExcel
	@GetMapping("/export")
	@Operation(summary = "导出部门数据", description = "导出部门数据")
	public List<DeptExcelVo> exportDepts() {
		return sysDeptService.exportDepts();
	}

	/**
	 * 导入部门信息
	 * @param excelVOList 部门Excel数据列表
	 * @param bindingResult 数据校验结果
	 * @return 导入结果
	 */
	@PostMapping("import")
	@Operation(summary = "导入部门信息", description = "导入部门信息")
	public R importDept(@RequestExcel List<DeptExcelVo> excelVOList, BindingResult bindingResult) {
		return sysDeptService.importDept(excelVOList, bindingResult);
	}

//	@Inner
//	@GetMapping("/{id}")
//	public R<Map<String, Object>> getDeptById(@PathVariable String id) {
//	    return R.ok(sysDeptService.getDeptMapById(id));
//	}

	@Inner
	@PostMapping("/listByIds")
	public R<List<Map<String, Object>>> listByIds(@RequestBody List<String> deptIds) {
	    return R.ok(sysDeptService.listDeptMapsByIds(deptIds));
	}
	
	@Inner
	@GetMapping("/treeForFeign")
	public R<List<Map<String, Object>>> getDeptTreeForFeign(
			@RequestParam(required = false) String deptName) {
	    List<Tree<String>> treeList = sysDeptService.getDeptTree(deptName);
	    List<Map<String, Object>> result = treeList.stream()
	            .map(tree -> (Map<String, Object>)new HashMap<String, Object>(tree))
	            .toList();

	    return R.ok(result);
	}

	
	@Inner
	@GetMapping("/getDescendantListForFeign/{deptId}")
	public R<List<Map<String, Object>>> getDescendantListForFeign(@PathVariable String deptId) {
	    return R.ok(sysDeptService.listDescendantDeptMaps(deptId));
	}
}
