package com.laigeoffer.pmhub.web.controller.monitor;

import com.laigeoffer.pmhub.common.annotation.Log;
import com.laigeoffer.pmhub.common.core.controller.BaseController;
import com.laigeoffer.pmhub.common.core.domain.AjaxResult;
import com.laigeoffer.pmhub.common.core.page.TableDataInfo;
import com.laigeoffer.pmhub.common.enums.BusinessType;
import com.laigeoffer.pmhub.system.domain.ManualElectricityRecord;
import com.laigeoffer.pmhub.system.service.IManualElectricityRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/monitor/manualElectricity")
public class ManualElectricityRecordController extends BaseController {
    @Autowired
    private IManualElectricityRecordService recordService;

    @PreAuthorize("@ss.hasPermi('monitor:manualElectricity:list')")
    @GetMapping("/list")
    public TableDataInfo list(ManualElectricityRecord record) {
        startPage();
        List<ManualElectricityRecord> records = recordService.selectRecordList(record);
        return getDataTable(records);
    }

    @PreAuthorize("@ss.hasPermi('monitor:manualElectricity:print')")
    @PostMapping("/printData")
    public AjaxResult printData(@RequestBody Long[] recordIds) {
        return success(recordService.selectPrintData(recordIds));
    }

    @PreAuthorize("@ss.hasPermi('monitor:manualElectricity:query')")
    @GetMapping("/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId) {
        ManualElectricityRecord record = recordService.selectRecordById(recordId);
        return record == null ? error("登记记录不存在") : success(record);
    }

    @PreAuthorize("@ss.hasPermi('monitor:manualElectricity:add')")
    @Log(title = "手工电表登记", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @Validated ManualElectricityRecord record) {
        if (recordService.countByDateAndArea(record) > 0) {
            return error("该区域该日期的电能记录已存在");
        }
        record.setCreateBy(getUsername());
        return toAjax(recordService.insertRecord(record));
    }

    @PreAuthorize("@ss.hasPermi('monitor:manualElectricity:edit')")
    @Log(title = "手工电表登记", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated ManualElectricityRecord record) {
        if (record.getRecordId() == null || recordService.selectRecordById(record.getRecordId()) == null) {
            return error("登记记录不存在");
        }
        if (recordService.countByDateAndArea(record) > 0) {
            return error("该区域该日期的电能记录已存在");
        }
        record.setUpdateBy(getUsername());
        return toAjax(recordService.updateRecord(record));
    }
}