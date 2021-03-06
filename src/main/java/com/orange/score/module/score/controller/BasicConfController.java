package com.orange.score.module.score.controller;

import com.github.pagehelper.PageInfo;
import com.orange.score.common.core.Result;
import com.orange.score.common.tools.plugins.FormItem;
import com.orange.score.common.utils.PageConvertUtil;
import com.orange.score.common.utils.ResponseUtil;
import com.orange.score.database.score.model.BasicConf;
import com.orange.score.module.core.service.ICommonQueryService;
import com.orange.score.module.score.service.IBasicConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Created by chenJz1012 on 2018-04-08.
*/
@RestController
@RequestMapping("/api/score/basicConf")
public class BasicConfController {

    @Autowired
    private IBasicConfService iBasicConfService;

    @Autowired
    private ICommonQueryService iCommonQueryService;

    @GetMapping(value = "/list")
    @ResponseBody
    public Result list(BasicConf basicConf,
    @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
    @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize) {
    PageInfo<BasicConf> pageInfo = iBasicConfService.selectByFilterAndPage(basicConf, pageNum, pageSize);
        return ResponseUtil.success(PageConvertUtil.grid(pageInfo));
    }

    @GetMapping(value = "/formItems")
    @ResponseBody
    public Result formItems() {
        List<FormItem> formItems = iCommonQueryService.selectFormItemsByTable("t_basic_conf");
        List searchItems = iCommonQueryService.selectSearchItemsByTable("t_basic_conf");
        Map result = new HashMap<>();
        result.put("formItems", formItems);
        result.put("searchItems", searchItems);
        return ResponseUtil.success(result);
    }

    @PostMapping("/insert")
    public Result insert(BasicConf basicConf) {
        iBasicConfService.save(basicConf);
        return ResponseUtil.success();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        iBasicConfService.deleteById(id);
        return ResponseUtil.success();
    }

    @PostMapping("/update")
    public Result update(BasicConf basicConf) {
        iBasicConfService.update(basicConf);
        return ResponseUtil.success();
    }

    @GetMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        BasicConf basicConf = iBasicConfService.findById(id);
        return ResponseUtil.success(basicConf);
    }
}
