package com.orange.score.module.score.service;

import com.github.pagehelper.PageInfo;
import com.orange.score.common.core.IService;
import com.orange.score.database.score.model.PersonBatchStatusRecord;

import java.util.List;

/**
 * Created by chenJz1012 on 2018-04-14.
 */
public interface IPersonBatchStatusRecordService extends IService<PersonBatchStatusRecord> {

    PageInfo<PersonBatchStatusRecord> selectByFilterAndPage(PersonBatchStatusRecord personBatchStatusRecord,
            int pageNum, int pageSize);

    List<PersonBatchStatusRecord> selectByFilter(PersonBatchStatusRecord personBatchStatusRecord);

    void insertStatus(Integer batchId, Integer personId, String alias, Integer status);

    void insertStatus(Integer batchId, Integer personId, String alias, Integer status ,String dept);

    void insertStatus2(Integer batchId, Integer personId, String alias, Integer status ,String dept, String statusReaseon);

    PersonBatchStatusRecord getPassPreCheck(PersonBatchStatusRecord personBatchStatusRecord);
}
