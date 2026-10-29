package com.laigeoffer.pmhub.system.mapper;

import com.laigeoffer.pmhub.system.domain.ManualElectricityRecord;

import java.util.List;

public interface ManualElectricityRecordMapper {
    List<ManualElectricityRecord> selectRecordList(ManualElectricityRecord record);

    ManualElectricityRecord selectRecordById(Long recordId);

    List<ManualElectricityRecord> selectRecordsByIds(Long[] recordIds);

    int countByDateAndArea(ManualElectricityRecord record);

    int insertRecord(ManualElectricityRecord record);

    int updateRecord(ManualElectricityRecord record);
}