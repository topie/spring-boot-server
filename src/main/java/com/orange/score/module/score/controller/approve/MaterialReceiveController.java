package com.orange.score.module.score.controller.approve;

import com.github.pagehelper.PageInfo;
import com.orange.score.common.core.Result;
import com.orange.score.common.exception.AuthBusinessException;
import com.orange.score.common.tools.freemarker.FreeMarkerUtil;
import com.orange.score.common.tools.plugins.FormItem;
import com.orange.score.common.utils.PageConvertUtil;
import com.orange.score.common.utils.ResponseUtil;
import com.orange.score.database.score.model.*;
import com.orange.score.module.core.service.ICommonQueryService;
import com.orange.score.module.core.service.IDictService;
import com.orange.score.module.score.service.*;
import com.orange.score.module.security.SecurityUtil;
import com.orange.score.module.security.service.RoleService;
import com.orange.score.module.security.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by chenJz1012 on 2018-04-16.
 */
@RestController
@RequestMapping("/api/score/materialReceive")
public class MaterialReceiveController {

    @Autowired
    private IScoreRecordService iScoreRecordService;

    @Autowired
    private ICommonQueryService iCommonQueryService;

    @Autowired
    private IDictService iDictService;

    @Autowired
    private UserService userService;

    @Autowired
    private IIdentityInfoService iIdentityInfoService;

    @Autowired
    private IHouseOtherService iHouseOtherService;

    @Autowired
    private IHouseMoveService iHouseMoveService;

    @Autowired
    private IHouseProfessionService iHouseProfessionService;

    @Autowired
    private IHouseRelationshipService iHouseRelationshipService;

    @Autowired
    private IMaterialInfoService iMaterialInfoService;

    @Autowired
    private IIndicatorService iIndicatorService;

    @Autowired
    private ICompanyInfoService iCompanyInfoService;

    @Autowired
    private IMaterialAcceptRecordService iMaterialAcceptRecordService;

    @Autowired
    private RoleService roleService;

    @GetMapping(value = "/receiving")
    @ResponseBody
    public Result receiving(ScoreRecord scoreRecord,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize) {
        Condition condition = new Condition(ScoreRecord.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        List<Integer> roles = userService.findUserRoleByUserId(userId);
        criteria.andEqualTo("status", 2);
        criteria.andIn("opRoleId", roles);
        if (StringUtils.isNotEmpty(scoreRecord.getPersonIdNum())) {
            criteria.andEqualTo("personIdNum", scoreRecord.getPersonIdNum());
        }
        if (scoreRecord.getBatchId() != null) {
            criteria.andEqualTo("batchId", scoreRecord.getBatchId());
        }
        if (scoreRecord.getIndicatorId() != null) {
            criteria.andEqualTo("indicatorId", scoreRecord.getIndicatorId());
        }
        PageInfo<ScoreRecord> pageInfo = iScoreRecordService.selectByFilterAndPage(condition, pageNum, pageSize);
        return ResponseUtil.success(PageConvertUtil.grid(pageInfo));
    }

    @GetMapping(value = "/received")
    @ResponseBody
    public Result received(ScoreRecord scoreRecord,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize) {
        Condition condition = new Condition(ScoreRecord.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        criteria.andGreaterThanOrEqualTo("status", 3);
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        List<Integer> roles = userService.findUserRoleByUserId(userId);
        criteria.andIn("opRoleId", roles);
        if (StringUtils.isNotEmpty(scoreRecord.getPersonIdNum())) {
            criteria.andEqualTo("personIdNum", scoreRecord.getPersonIdNum());
        }
        if (scoreRecord.getBatchId() != null) {
            criteria.andEqualTo("batchId", scoreRecord.getBatchId());
        }
        if (scoreRecord.getIndicatorId() != null) {
            criteria.andEqualTo("indicatorId", scoreRecord.getIndicatorId());
        }
        PageInfo<ScoreRecord> pageInfo = iScoreRecordService.selectByFilterAndPage(condition, pageNum, pageSize);
        return ResponseUtil.success(PageConvertUtil.grid(pageInfo));
    }

    @GetMapping("/detailPerson")
    public Result detailPerson(@RequestParam Integer identityInfoId) throws FileNotFoundException {
        Map params = new HashMap();
        Condition condition = new Condition(ScoreRecord.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        List<Integer> roles = userService.findUserRoleByUserId(userId);
        criteria.andEqualTo("personId", identityInfoId);
        criteria.andIn("opRoleId", roles);
        List<ScoreRecord> records = iScoreRecordService.findByCondition(condition);
        List<Map> mList = new ArrayList<>();
        for (ScoreRecord record : records) {
            Map msMap = new HashMap();
            Indicator indicator = iIndicatorService.findById(record.getIndicatorId());
            msMap.put("indicator", indicator);
            List<MaterialInfo> materialInfos = iMaterialInfoService.findByIndicatorId(record.getIndicatorId());
            if (materialInfos.size() > 0) msMap.put("materialInfos", materialInfos);
            mList.add(msMap);
        }
        params.put("mlist", mList);
        IdentityInfo person = iIdentityInfoService.findById(identityInfoId);
        if (person == null) {
            person = new IdentityInfo();
        }
        params.put("person", person);
        CompanyInfo companyInfo = iCompanyInfoService.findById(person.getCompanyId());
        if (companyInfo == null) {
            companyInfo = new CompanyInfo();
        }
        params.put("company", companyInfo);
        HouseOther other = iHouseOtherService.findBy("identityInfoId", identityInfoId);
        if (other == null) {
            other = new HouseOther();
        }
        params.put("other", other);
        HouseProfession profession = iHouseProfessionService.findBy("identityInfoId", identityInfoId);
        if (profession == null) {
            profession = new HouseProfession();
        }
        params.put("profession", profession);
        HouseMove houseMove = iHouseMoveService.findBy("identityInfoId", identityInfoId);
        if (houseMove == null) {
            houseMove = new HouseMove();
        }
        params.put("move", houseMove);
        condition = new Condition(HouseRelationship.class);
        criteria = condition.createCriteria();
        criteria.andEqualTo("identityInfoId", identityInfoId);
        List<HouseRelationship> relationshipList = iHouseRelationshipService.findByCondition(condition);
        if (CollectionUtils.isEmpty(relationshipList)) {
            relationshipList = new ArrayList<>();
        }
        params.put("relation", relationshipList);
        String templatePath = ResourceUtils.getFile("classpath:templates/").getPath();
        String html = FreeMarkerUtil.getHtmlStringFromTemplate(templatePath, "material_info.ftl", params);
        Map result = new HashMap();
        List<String> mCheckList = new ArrayList<>();
        condition = new Condition(MaterialAcceptRecord.class);
        criteria = condition.createCriteria();
        criteria.andEqualTo("roleId", roles.get(0));
        criteria.andEqualTo("personId", identityInfoId);
        criteria.andEqualTo("batchId", person.getBatchId());
        List<MaterialAcceptRecord> materials = iMaterialAcceptRecordService.findByCondition(condition);
        for (MaterialAcceptRecord item : materials) {
            mCheckList.add(item.getIndicatorId() + "_" + item.getMaterialId());
        }
        result.put("mCheckList", mCheckList);
        result.put("html", html);
        return ResponseUtil.success(result);
    }

    @GetMapping("/detailAll")
    public Result detailAll(@RequestParam Integer identityInfoId, @RequestParam Integer indicatorId)
            throws FileNotFoundException {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        List<Integer> roles = userService.findUserRoleByUserId(userId);
        if (CollectionUtils.isEmpty(roles)) throw new AuthBusinessException("用户未设置角色");
        Map params = new HashMap();
        List<Map> mlist = new ArrayList<>();
        Map msMap = new HashMap();
        Indicator indicator = iIndicatorService.findById(indicatorId);
        msMap.put("indicator", indicator);
        List<MaterialInfo> materialInfos = iMaterialInfoService.findByIndicatorId(indicatorId);
        msMap.put("materialInfos", materialInfos);
        mlist.add(msMap);
        params.put("mlist", mlist);
        IdentityInfo person = iIdentityInfoService.findById(identityInfoId);
        if (person == null) {
            person = new IdentityInfo();
        }
        params.put("person", person);
        CompanyInfo companyInfo = iCompanyInfoService.findById(person.getCompanyId());
        if (companyInfo == null) {
            companyInfo = new CompanyInfo();
        }
        params.put("company", companyInfo);
        HouseOther other = iHouseOtherService.findBy("identityInfoId", identityInfoId);
        if (other == null) {
            other = new HouseOther();
        }
        params.put("other", other);
        HouseProfession profession = iHouseProfessionService.findBy("identityInfoId", identityInfoId);
        if (profession == null) {
            profession = new HouseProfession();
        }
        params.put("profession", profession);
        HouseMove houseMove = iHouseMoveService.findBy("identityInfoId", identityInfoId);
        if (houseMove == null) {
            houseMove = new HouseMove();
        }
        params.put("move", houseMove);
        Condition condition = new Condition(HouseRelationship.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("identityInfoId", identityInfoId);
        List<HouseRelationship> relationshipList = iHouseRelationshipService.findByCondition(condition);
        if (CollectionUtils.isEmpty(relationshipList)) {
            relationshipList = new ArrayList<>();
        }
        params.put("relation", relationshipList);
        Map result = new HashMap();
        List<String> mCheckList = new ArrayList<>();
        condition = new Condition(MaterialAcceptRecord.class);
        criteria = condition.createCriteria();
        criteria.andEqualTo("roleId", roles.get(0));
        criteria.andEqualTo("personId", identityInfoId);
        criteria.andEqualTo("batchId", person.getBatchId());
        criteria.andEqualTo("indicatorId", indicatorId);
        List<MaterialAcceptRecord> materials = iMaterialAcceptRecordService.findByCondition(condition);
        for (MaterialAcceptRecord item : materials) {
            mCheckList.add(item.getIndicatorId() + "_" + item.getMaterialId());
        }
        result.put("mCheckList", mCheckList);
        String templatePath = ResourceUtils.getFile("classpath:templates/").getPath();
        String html = FreeMarkerUtil.getHtmlStringFromTemplate(templatePath, "material_info.ftl", params);
        result.put("html", html);
        return ResponseUtil.success(result);
    }

    @GetMapping(value = "/formItems")
    @ResponseBody
    public Result formItems() {
        List<FormItem> formItems = iCommonQueryService.selectFormItemsByTable("t_person_batch_score_record");
        List searchItems = iCommonQueryService.selectSearchItemsByTable("t_person_batch_score_record");
        Map result = new HashMap<>();
        result.put("formItems", formItems);
        result.put("searchItems", searchItems);
        Map scoreRecordStatus = iDictService.selectMapByAlias("scoreRecordStatus");
        result.put("scoreRecordStatus", scoreRecordStatus);
        return ResponseUtil.success(result);
    }

    @PostMapping("/confirmReceived")
    public Result update(@RequestParam("personId") Integer personId, String[] mIds) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        List<Integer> roles = userService.findUserRoleByUserId(userId);
        if (CollectionUtils.isEmpty(roles)) throw new AuthBusinessException("用户未设置角色");
        IdentityInfo person = iIdentityInfoService.findById(personId);
        Set<Integer> indicatorIdSet = new HashSet<>();
        for (String mId : mIds) {
            String[] mIdArr = mId.split("_");
            MaterialInfo materialInfo = iMaterialInfoService.findById(Integer.valueOf(mIdArr[1]));
            indicatorIdSet.add(Integer.valueOf(mIdArr[0]));
            MaterialAcceptRecord materialAcceptRecord = new MaterialAcceptRecord();
            materialAcceptRecord.setIndicatorId(Integer.valueOf(mIdArr[0]));
            materialAcceptRecord.setBatchId(person.getBatchId());
            materialAcceptRecord.setPersonId(personId);
            materialAcceptRecord.setRoleId(roles.get(0));
            materialAcceptRecord.setMaterialName(materialInfo.getName());
            materialAcceptRecord.setMaterialId(materialInfo.getId());
            materialAcceptRecord.setStatus(1);
            iMaterialAcceptRecordService.save(materialAcceptRecord);
        }
        Condition condition = new Condition(ScoreRecord.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        criteria.andIn("indicatorId", indicatorIdSet);
        criteria.andEqualTo("personId", personId);
        criteria.andEqualTo("batchId", person.getBatchId());
        criteria.andEqualTo("opRoleId", roles.get(0));
        List<ScoreRecord> scoreRecords = iScoreRecordService.findByCondition(condition);
        for (ScoreRecord scoreRecord : scoreRecords) {
            scoreRecord.setStatus(3);
            scoreRecord.setSubmitDate(new Date());
            iScoreRecordService.update(scoreRecord);
        }
        return ResponseUtil.success();
    }

}