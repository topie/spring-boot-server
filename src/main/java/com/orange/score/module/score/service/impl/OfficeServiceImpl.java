package com.orange.score.module.score.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.SqlUtil;
import com.orange.score.common.core.BaseService;
import com.orange.score.common.utils.MethodUtil;
import com.orange.score.common.utils.SearchItem;
import com.orange.score.common.utils.SearchUtil;
import com.orange.score.database.core.model.ColumnJson;
import com.orange.score.database.score.dao.OfficeMapper;
import com.orange.score.database.score.model.Office;
import com.orange.score.module.core.service.IColumnJsonService;
import com.orange.score.module.score.service.IOfficeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenJz1012 on 2018-06-26.
 */
@Service
@Transactional
public class OfficeServiceImpl extends BaseService<Office> implements IOfficeService {

    @Autowired
    private OfficeMapper officeMapper;

    @Autowired
    private IColumnJsonService iColumnJsonService;

    @Override
    public PageInfo<Office> selectByFilterAndPage(Office office, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Office> list = selectByFilter(office);
        return new PageInfo<>(list);
    }

    @Override
    public List<Office> selectByFilter(Office office) {
        Page<Office> tmp = SqlUtil.getLocalPage();
        SqlUtil.clearLocalPage();
        Condition condition = new Condition(Office.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        if (office != null) {
            if (office.getParentId() != null) {
                criteria.andEqualTo("parentId", office.getParentId());
            }
            ColumnJson columnJson = new ColumnJson();
            columnJson.setTableName("t_office_2");
            List<ColumnJson> list = iColumnJsonService.selectByFilter(columnJson);
            if (list.size() > 0) {
                List<SearchItem> searchItems = new ArrayList<>();
                columnJson = list.get(0);
                JSONArray jsonArray = JSONArray.parseArray(columnJson.getSearchConf());
                if (StringUtils.isNotEmpty(columnJson.getSearchConf())) {
                    for (Object o : jsonArray) {
                        o = (JSONObject) o;
                        SearchItem searchItem = new SearchItem();
                        searchItem.setLabel(((JSONObject) o).getString("label"));
                        searchItem.setName(((JSONObject) o).getString("name"));
                        searchItem.setType(((JSONObject) o).getString("type"));
                        searchItem.setSearchType(((JSONObject) o).getString("searchType"));
                        if (StringUtils.isNotEmpty(searchItem.getName())) {
                            Object value = MethodUtil.invokeGet(office, searchItem.getName());
                            if (value != null) {
                                if (value instanceof String) {
                                    if (StringUtils.isNotEmpty((String) value)) searchItem.setValue(value);
                                } else {
                                    searchItem.setValue(value);
                                }
                            }
                        }
                        searchItems.add(searchItem);
                    }
                }
                SearchUtil.convert(criteria, searchItems);
            }
        }
        if (tmp != null) SqlUtil.setLocalPage(tmp);
        return officeMapper.selectByCondition(condition);
    }
}

