package com.orange.score.module.score.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.orange.score.database.score.dao.IndicatorMapper;
import com.orange.score.database.score.model.Indicator;
import com.orange.score.module.score.service.IIndicatorService;
import com.orange.score.common.core.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;



/**
 * Created by chenJz1012 on 2018-04-02.
 */
@Service
@Transactional
public class IndicatorServiceImpl extends BaseService<Indicator> implements IIndicatorService {

    @Autowired
    private IndicatorMapper indicatorMapper;

    @Override
    public PageInfo<Indicator> selectByFilterAndPage(Indicator indicator, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Indicator> list = selectByFilter(indicator);
        return new PageInfo<>(list);
    }

    @Override
    public List<Indicator> selectByFilter(Indicator indicator) {
        Condition condition = new Condition(Indicator.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        return indicatorMapper.selectByCondition(condition);
    }
}

