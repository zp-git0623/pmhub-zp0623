package com.laigeoffer.pmhub.system.service.impl;

import com.laigeoffer.pmhub.system.domain.ManualElectricityRecord;
import com.laigeoffer.pmhub.system.mapper.ManualElectricityRecordMapper;
import com.laigeoffer.pmhub.system.service.IManualElectricityRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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