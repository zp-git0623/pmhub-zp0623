package com.laigeoffer.pmhub.web.controller.report;

import com.laigeoffer.pmhub.common.annotation.Log;
import com.laigeoffer.pmhub.common.core.controller.BaseController;
import com.laigeoffer.pmhub.common.core.domain.AjaxResult;
import com.laigeoffer.pmhub.common.core.page.TableDataInfo;
import com.laigeoffer.pmhub.common.enums.BusinessType;
import com.laigeoffer.pmhub.framework.web.service.PermissionService;
import com.laigeoffer.pmhub.system.domain.ReportTemplate;
import com.laigeoffer.pmhub.system.service.IReportTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report/template")
public class ReportTemplateController extends BaseController {
    @Autowired
    private IReportTemplateService templateService;

    @Autowired
    private PermissionService permissionService;

    @PreAuthorize("@ss.hasPermi('report:template:list')")
    @GetMapping("/list")
    public TableDataInfo list(ReportTemplate template) {
        startPage();
        List<ReportTemplate> templates = templateService.selectTemplateList(template);
        return getDataTable(templates);
    }

    @PreAuthorize("@ss.hasAnyPermi('report:template:list,monitor:manualElectricity:print')")
    @GetMapping("/options")
    public AjaxResult options(@RequestParam("reportCode") String reportCode) {
        return success(templateService.selectTemplateOptions(reportCode));
    }

    @PreAuthorize("@ss.hasAnyPermi('report:template:query,report:template:edit,monitor:manualElectricity:print')")
    @GetMapping("/{templateId}")
    public AjaxResult getInfo(@PathVariable("templateId") Long templateId) {
        ReportTemplate template = templateService.selectTemplateById(templateId);
        if (template == null || (!permissionService.hasAnyPermi("report:template:query,report:template:edit")
                && (!"0".equals(template.getStatus()) || !"manualElectricity".equals(template.getReportCode())))) {
            return error("模板不存在或不可用于当前报表打印");
        }
        return success(template);
    }

    @PreAuthorize("@ss.hasPermi('report:template:add')")
    @Log(title = "报表模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @Validated ReportTemplate template) {
        template.setCreateBy(getUsername());
        return toAjax(templateService.insertTemplate(template));
    }

    @PreAuthorize("@ss.hasPermi('report:template:edit')")
    @Log(title = "报表模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated ReportTemplate template) {
        if (template.getTemplateId() == null || templateService.selectTemplateById(template.getTemplateId()) == null) {
            return error("模板不存在");
        }
        template.setUpdateBy(getUsername());
        return toAjax(templateService.updateTemplate(template));
    }

    @PreAuthorize("@ss.hasPermi('report:template:remove')")
    @Log(title = "报表模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{templateIds}")
    public AjaxResult remove(@PathVariable("templateIds") Long[] templateIds) {
        return toAjax(templateService.deleteTemplateByIds(templateIds));
    }
}
