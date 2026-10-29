package com.laigeoffer.pmhub.system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laigeoffer.pmhub.common.exception.ServiceException;
import com.laigeoffer.pmhub.system.domain.ReportTemplate;
import com.laigeoffer.pmhub.system.mapper.ReportTemplateMapper;
import com.laigeoffer.pmhub.system.service.IReportTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReportTemplateServiceImpl implements IReportTemplateService {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper()
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

    @Autowired
    private ReportTemplateMapper templateMapper;

    @Override
    public List<ReportTemplate> selectTemplateList(ReportTemplate template) {
        return templateMapper.selectTemplateList(template);
    }

    @Override
    public ReportTemplate selectTemplateById(Long templateId) {
        return templateMapper.selectTemplateById(templateId);
    }

    @Override
    public List<Map<String, Object>> selectTemplateOptions(String reportCode) {
        if (!"manualElectricity".equals(reportCode)) {
            throw new ServiceException("不支持该报表编码");
        }
        return templateMapper.selectTemplateOptions(reportCode);
    }

    @Override
    public int insertTemplate(ReportTemplate template) {
        validateTemplateJson(template.getTemplateJson());
        return templateMapper.insertTemplate(template);
    }

    @Override
    public int updateTemplate(ReportTemplate template) {
        validateTemplateJson(template.getTemplateJson());
        return templateMapper.updateTemplate(template);
    }

    @Override
    public int deleteTemplateByIds(Long[] templateIds) {
        return templateMapper.deleteTemplateByIds(templateIds);
    }

    private void validateTemplateJson(String templateJson) {
        if (templateJson == null || templateJson.trim().isEmpty()) {
            throw new ServiceException("模板内容不能为空");
        }
        JsonNode root;
        try {
            root = JSON_MAPPER.readTree(templateJson);
        } catch (JsonProcessingException e) {
            throw new ServiceException("模板内容必须为有效的JSON");
        }
        JsonNode panels = root == null ? null : root.get("panels");
        if (root == null || !root.isObject() || panels == null || !panels.isArray() || panels.size() == 0) {
            throw new ServiceException("HiPrint模板必须包含非空的panels数组");
        }
        for (JsonNode panel : panels) {
            if (!panel.isObject() || !panel.path("printElements").isArray()) {
                throw new ServiceException("HiPrint模板面板必须包含printElements数组");
            }
        }
    }
}
