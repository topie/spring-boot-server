package com.orange.score.module.score.service;

import com.github.pagehelper.PageInfo;
import com.orange.score.common.core.IService;
import com.orange.score.database.score.model.ScoreRecord;
import java.util.List;

/**
* Created by chenJz1012 on 2018-04-16.
*/
public interface IScoreRecordService extends IService<ScoreRecord> {

    PageInfo<ScoreRecord> selectByFilterAndPage(ScoreRecord scoreRecord, int pageNum, int pageSize);

    List<ScoreRecord> selectByFilter(ScoreRecord scoreRecord);
}
