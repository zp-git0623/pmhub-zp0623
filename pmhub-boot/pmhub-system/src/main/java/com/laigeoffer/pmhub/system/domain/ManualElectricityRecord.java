package com.laigeoffer.pmhub.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.laigeoffer.pmhub.common.core.domain.BaseEntity;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

/** 手工登记的区域每日用电量。 */
public class ManualElectricityRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long recordId;

    @NotNull(message = "登记日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date recordDate;

    @NotBlank(message = "区域名称不能为空")
    @Size(max = 100, message = "区域名称不能超过100个字符")
    private String areaName;

    @NotNull(message = "电能用量不能为空")
    @DecimalMin(value = "0.0", message = "电能用量不能小于0")
    @Digits(integer = 15, fraction = 3, message = "电能用量最多支持15位整数和3位小数")
    private BigDecimal energyKwh;

    /** 列表查询起止日期，不对应数据库字段。 */
    private String beginDate;
    private String endDate;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
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

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}