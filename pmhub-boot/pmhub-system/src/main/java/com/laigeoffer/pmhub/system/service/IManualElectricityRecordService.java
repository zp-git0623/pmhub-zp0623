package com.laigeoffer.pmhub.system.service;

import com.laigeoffer.pmhub.system.domain.ManualElectricityRecord;

import java.util.List;

public interface IManualElectricityRecordService {
    List<ManualElectricityRecord> selectRecordList(ManualElectricityRecord record);

    ManualElectricityRecord selectRecordById(Long recordId);

    List<ManualElectricityRecord> selectPrintData(Long[] recordIds);

    int countByDateAndArea(ManualElectricityRecord record);

    int insertRecord(ManualElectricityRecord record);

    int updateRecord(ManualElectricityRecord record);
}