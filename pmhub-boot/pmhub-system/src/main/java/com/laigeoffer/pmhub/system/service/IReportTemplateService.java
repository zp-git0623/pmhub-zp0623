package com.laigeoffer.pmhub.system.service;

import com.laigeoffer.pmhub.system.domain.ReportTemplate;

import java.util.List;
import java.util.Map;

public interface IReportTemplateService {
    List<ReportTemplate> selectTemplateList(ReportTemplate template);

    ReportTemplate selectTemplateById(Long templateId);

    List<Map<String, Object>> selectTemplateOptions(String reportCode);

    int insertTemplate(ReportTemplate template);

    int updateTemplate(ReportTemplate template);

    int deleteTemplateByIds(Long[] templateIds);
}
