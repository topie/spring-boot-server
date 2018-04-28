package com.orange.score.module.score.controller.approve;

import com.github.pagehelper.PageInfo;
import com.orange.score.common.core.Result;
import com.orange.score.common.exception.AuthBusinessException;
import com.orange.score.common.tools.plugins.FormItem;
import com.orange.score.common.utils.PageConvertUtil;
import com.orange.score.common.utils.ResponseUtil;
import com.orange.score.database.score.model.ApplyCancel;
import com.orange.score.module.core.service.ICommonQueryService;
import com.orange.score.module.score.service.IApplyCancelService;
import com.orange.score.module.security.SecurityUtil;
import com.orange.score.module.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenJz1012 on 2018-04-21.
 */
@RestController
@RequestMapping("/api/score/applyCancel")
public class ApplyCancelController {

    @Autowired
    private IApplyCancelService iApplyCancelService;

    @Autowired
    private ICommonQueryService iCommonQueryService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/mine")
    @ResponseBody
    public Result mine(ApplyCancel applyCancel,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        List<Integer> roles = userService.findUserRoleByUserId(userId);
        Condition condition = new Condition(ApplyCancel.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        if (roles.get(0) != 3) criteria.andEqualTo("applyRoleId", roles.get(0));
        PageInfo<ApplyCancel> pageInfo = iApplyCancelService.selectByFilterAndPage(condition, pageNum, pageSize);
        return ResponseUtil.success(PageConvertUtil.grid(pageInfo));
    }

    @GetMapping(value = "/ing")
    @ResponseBody
    public Result ing(ApplyCancel applyCancel,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        Condition condition = new Condition(ApplyCancel.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("approveStatus", 0);
        PageInfo<ApplyCancel> pageInfo = iApplyCancelService.selectByFilterAndPage(condition, pageNum, pageSize);
        return ResponseUtil.success(PageConvertUtil.grid(pageInfo));
    }

    @GetMapping(value = "/agree")
    @ResponseBody
    public Result agree(ApplyCancel applyCancel,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        Condition condition = new Condition(ApplyCancel.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("approveStatus", 1);
        PageInfo<ApplyCancel> pageInfo = iApplyCancelService.selectByFilterAndPage(condition, pageNum, pageSize);
        return ResponseUtil.success(PageConvertUtil.grid(pageInfo));
    }

    @GetMapping(value = "/disAgree")
    @ResponseBody
    public Result disAgree(ApplyCancel applyCancel,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        Condition condition = new Condition(ApplyCancel.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("approveStatus", 2);
        PageInfo<ApplyCancel> pageInfo = iApplyCancelService.selectByFilterAndPage(condition, pageNum, pageSize);
        return ResponseUtil.success(PageConvertUtil.grid(pageInfo));
    }

    @GetMapping(value = "/formItems")
    @ResponseBody
    public Result formItems() {
        List<FormItem> formItems = iCommonQueryService.selectFormItemsByTable("t_apply_cancel");
        List searchItems = iCommonQueryService.selectSearchItemsByTable("t_apply_cancel");
        Map result = new HashMap<>();
        result.put("formItems", formItems);
        result.put("searchItems", searchItems);
        return ResponseUtil.success(result);
    }

    @PostMapping("/insert")
    public Result insert(ApplyCancel applyCancel) {
        iApplyCancelService.save(applyCancel);
        return ResponseUtil.success();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        iApplyCancelService.deleteById(id);
        return ResponseUtil.success();
    }

    @PostMapping("/update")
    public Result update(ApplyCancel applyCancel) {
        iApplyCancelService.update(applyCancel);
        return ResponseUtil.success();
    }

    @GetMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        ApplyCancel applyCancel = iApplyCancelService.findById(id);
        return ResponseUtil.success(applyCancel);
    }
}