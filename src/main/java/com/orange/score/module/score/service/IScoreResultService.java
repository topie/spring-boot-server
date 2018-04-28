package com.orange.score.module.score.service;

import com.github.pagehelper.PageInfo;
import com.orange.score.common.core.IService;
import com.orange.score.database.score.model.ScoreResult;
import java.util.List;

/**
* Created by chenJz1012 on 2018-04-18.
*/
public interface IScoreResultService extends IService<ScoreResult> {

    PageInfo<ScoreResult> selectByFilterAndPage(ScoreResult scoreResult, int pageNum, int pageSize);

    List<ScoreResult> selectByFilter(ScoreResult scoreResult);
}