package com.laigeoffer.pmhub.system.domain;

import com.laigeoffer.pmhub.common.core.domain.BaseEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/** 可复用的 HiPrint 报表模板。 */
public class ReportTemplate extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Positive(message = "模板ID必须为正整数")
    private Long templateId;

    @NotBlank(message = "模板名称不能为空")
    @Size(max = 100, message = "模板名称不能超过100个字符")
    private String templateName;

    @NotBlank(message = "报表编码不能为空")
    @Pattern(regexp = "manualElectricity", message = "不支持该报表编码")
    private String reportCode;

    @NotBlank(message = "模板内容不能为空")
    private String templateJson;

    @NotBlank(message = "模板状态不能为空")
    @Pattern(regexp = "[01]", message = "模板状态只能为0或1")
    private String status;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getReportCode() {
        return reportCode;
    }

    public void setReportCode(String reportCode) {
        this.reportCode = reportCode;
    }

    public String getTemplateJson() {
        return templateJson;
    }

    public void setTemplateJson(String templateJson) {
        this.templateJson = templateJson;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    @Size(max = 500, message = "备注不能超过500个字符")
    public String getRemark() {
        return super.getRemark();
    }
}
