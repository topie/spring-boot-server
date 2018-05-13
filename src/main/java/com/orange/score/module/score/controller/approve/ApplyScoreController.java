package com.orange.score.module.score.controller.approve;

import com.github.pagehelper.PageInfo;
import com.orange.score.common.core.Result;
import com.orange.score.common.exception.AuthBusinessException;
import com.orange.score.common.tools.plugins.FormItem;
import com.orange.score.common.utils.PageConvertUtil;
import com.orange.score.common.utils.ResponseUtil;
import com.orange.score.database.score.model.ApplyScore;
import com.orange.score.database.score.model.ScoreRecord;
import com.orange.score.database.security.model.Role;
import com.orange.score.module.core.service.ICommonQueryService;
import com.orange.score.module.score.service.IApplyScoreService;
import com.orange.score.module.score.service.IScoreRecordService;
import com.orange.score.module.security.SecurityUser;
import com.orange.score.module.security.SecurityUtil;
import com.orange.score.module.security.service.RoleService;
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
@RequestMapping("/api/score/applyScore")
public class ApplyScoreController {

    @Autowired
    private IApplyScoreService iApplyScoreService;

    @Autowired
    private ICommonQueryService iCommonQueryService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private IScoreRecordService iScoreRecordService;

    @GetMapping(value = "/mine")
    @ResponseBody
    public Result mine(ApplyScore applyScore,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        Condition condition = new Condition(ApplyScore.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("applyUserId", userId);
        PageInfo<ApplyScore> pageInfo = iApplyScoreService.selectByFilterAndPage(condition, pageNum, pageSize);
        return ResponseUtil.success(PageConvertUtil.grid(pageInfo));
    }

    @GetMapping(value = "/ing")
    @ResponseBody
    public Result ing(ApplyScore applyScore,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        Condition condition = new Condition(ApplyScore.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("approveStatus", 0);
        PageInfo<ApplyScore> pageInfo = iApplyScoreService.selectByFilterAndPage(condition, pageNum, pageSize);
        return ResponseUtil.success(PageConvertUtil.grid(pageInfo));
    }

    @GetMapping(value = "/agree")
    @ResponseBody
    public Result agree(ApplyScore applyScore,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        Condition condition = new Condition(ApplyScore.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("approveStatus", 1);
        PageInfo<ApplyScore> pageInfo = iApplyScoreService.selectByFilterAndPage(condition, pageNum, pageSize);
        return ResponseUtil.success(PageConvertUtil.grid(pageInfo));
    }

    @GetMapping(value = "/disAgree")
    @ResponseBody
    public Result disAgree(ApplyScore applyScore,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        Condition condition = new Condition(ApplyScore.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("approveStatus", 2);
        PageInfo<ApplyScore> pageInfo = iApplyScoreService.selectByFilterAndPage(condition, pageNum, pageSize);
        return ResponseUtil.success(PageConvertUtil.grid(pageInfo));
    }

    @GetMapping(value = "/formItems")
    @ResponseBody
    public Result formItems() {
        List<FormItem> formItems = iCommonQueryService.selectFormItemsByTable("t_apply_score");
        List searchItems = iCommonQueryService.selectSearchItemsByTable("t_apply_score");
        Map result = new HashMap<>();
        result.put("formItems", formItems);
        result.put("searchItems", searchItems);
        return ResponseUtil.success(result);
    }

    @PostMapping("/apply")
    public Result apply(@RequestParam("scoreRecordId") Integer scoreRecordId, @RequestParam("reason") String reason) {
        SecurityUser securityUser = SecurityUtil.getCurrentSecurityUser();
        if (securityUser == null) throw new AuthBusinessException("用户未登录");
        List<Integer> roles = userService.findUserRoleByUserId(securityUser.getId());
        ScoreRecord scoreRecord = iScoreRecordService.findById(scoreRecordId);
        ApplyScore applyScore = new ApplyScore();
        applyScore.setBatchId(scoreRecord.getBatchId());
        applyScore.setIndicatorId(scoreRecord.getIndicatorId());
        applyScore.setPersonId(scoreRecord.getPersonId());
        applyScore.setIndicatorName(scoreRecord.getIndicatorName());
        applyScore.setPersonIdNumber(scoreRecord.getPersonIdNum());
        applyScore.setApplyUserId(securityUser.getId());
        applyScore.setApplyUser(securityUser.getDisplayName());
        applyScore.setApplyRoleId(roles.get(0));
        Role role = roleService.findRoleById(roles.get(0));
        applyScore.setApplyRole(role.getRoleName());
        applyScore.setApproveStatus(0);
        applyScore.setApplyReason(reason);
        iApplyScoreService.save(applyScore);
        return ResponseUtil.success();
    }

    @PostMapping("/agree")
    public Result agree(@RequestParam Integer id) {
        String userName = SecurityUtil.getCurrentUserName();
        if (userName == null) throw new AuthBusinessException("用户未登录");
        ApplyScore applyScore = iApplyScoreService.findById(id);
        applyScore.setApproveContent("同意");
        applyScore.setApproveStatus(1);
        applyScore.setApproveUser(userName);
        iApplyScoreService.update(applyScore);
        return ResponseUtil.success();
    }

    @PostMapping("/disAgree")
    public Result disAgree(@RequestParam Integer id, @RequestParam("approveContent") String approveContent) {
        String userName = SecurityUtil.getCurrentUserName();
        if (userName == null) throw new AuthBusinessException("用户未登录");
        ApplyScore applyScore = iApplyScoreService.findById(id);
        applyScore.setApproveContent(approveContent);
        applyScore.setApproveStatus(2);
        applyScore.setApproveUser(userName);
        iApplyScoreService.update(applyScore);
        return ResponseUtil.success();
    }

    @GetMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        ApplyScore applyScore = iApplyScoreService.findById(id);
        return ResponseUtil.success(applyScore);
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        iApplyScoreService.deleteById(id);
        return ResponseUtil.success();
    }

}
