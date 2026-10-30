package com.laigeoffer.pmhub.system.service.impl;

import com.laigeoffer.pmhub.common.exception.ServiceException;
import com.laigeoffer.pmhub.system.domain.ManualElectricityRecord;
import com.laigeoffer.pmhub.system.mapper.ManualElectricityRecordMapper;
import com.laigeoffer.pmhub.system.service.IManualElectricityRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ManualElectricityRecordServiceImpl implements IManualElectricityRecordService {
    @Autowired
    private ManualElectricityRecordMapper recordMapper;

    @Override
    public List<ManualElectricityRecord> selectRecordList(ManualElectricityRecord record) {
        return recordMapper.selectRecordList(record);
    }

    @Override
    public ManualElectricityRecord selectRecordById(Long recordId) {
        return recordMapper.selectRecordById(recordId);
    }

    @Override
    public List<ManualElectricityRecord> selectPrintData(Long[] recordIds) {
        if (recordIds == null || recordIds.length == 0) {
            throw new ServiceException("请选择需要打印的登记记录");
        }
        if (recordIds.length > 500) {
            throw new ServiceException("每次最多打印500条登记记录");
        }
        Set<Long> selectedIds = new HashSet<>();
        for (Long recordId : recordIds) {
            if (recordId == null || recordId <= 0 || !selectedIds.add(recordId)) {
                throw new ServiceException("登记记录ID必须为不重复的正整数");
            }
        }
        List<ManualElectricityRecord> records = recordMapper.selectRecordsByIds(recordIds);
        if (records.size() != recordIds.length) {
            throw new ServiceException("部分登记记录不存在，请刷新后重新选择");
        }
        return records;
    }

    @Override
    public List<ManualElectricityRecord> selectExportRecords(Long[] recordIds) {
        if (recordIds == null || recordIds.length == 0) {
            throw new ServiceException("请选择需要导出的登记记录");
        }
        if (recordIds.length > 1000) {
            throw new ServiceException("单次最多按记录导出1000条");
        }
        Set<Long> selectedIds = new HashSet<>();
        for (Long recordId : recordIds) {
            if (recordId == null || recordId <= 0 || !selectedIds.add(recordId)) {
                throw new ServiceException("登记记录ID必须为不重复的正整数");
            }
        }
        return recordMapper.selectRecordsByIds(recordIds);
    }

    @Override
    public int countByDateAndArea(ManualElectricityRecord record) {
        return recordMapper.countByDateAndArea(record);
    }

    @Override
    public int insertRecord(ManualElectricityRecord record) {
        return recordMapper.insertRecord(record);
    }

    @Override
    public int updateRecord(ManualElectricityRecord record) {
        return recordMapper.updateRecord(record);
    }
}