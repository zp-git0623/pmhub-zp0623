package com.laigeoffer.pmhub.system.domain.vo;

import com.laigeoffer.pmhub.common.annotation.Excel;
import com.laigeoffer.pmhub.system.domain.ManualElectricityRecord;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 手工电表登记导出视图。
 *
 * <p>导出列单独定义，避免在 {@link ManualElectricityRecord} 与其父类上叠加 Excel 注解影响其他场景。</p>
 */
public class ManualElectricityExportVo {
    @Excel(name = "登记日期", sort = 1, dateFormat = "yyyy-MM-dd", width = 14)
    private Date recordDate;

    @Excel(name = "区域", sort = 2, width = 22)
    private String areaName;

    @Excel(name = "电能用量(kWh)", sort = 3, scale = 3, width = 18)
    private BigDecimal energyKwh;

    @Excel(name = "备注", sort = 4, width = 30)
    private String remark;

    @Excel(name = "登记人", sort = 5, width = 14)
    private String createBy;

    @Excel(name = "登记时间", sort = 6, dateFormat = "yyyy-MM-dd HH:mm:ss", width = 22)
    private Date createTime;

    public static ManualElectricityExportVo of(ManualElectricityRecord record) {
        ManualElectricityExportVo vo = new ManualElectricityExportVo();
        vo.setRecordDate(record.getRecordDate());
        vo.setAreaName(record.getAreaName());
        vo.setEnergyKwh(record.getEnergyKwh());
        vo.setRemark(record.getRemark());
        vo.setCreateBy(record.getCreateBy());
        vo.setCreateTime(record.getCreateTime());
        return vo;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public BigDecimal getEnergyKwh() {
        return energyKwh;
    }

    public void setEnergyKwh(BigDecimal energyKwh) {
        this.energyKwh = energyKwh;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
