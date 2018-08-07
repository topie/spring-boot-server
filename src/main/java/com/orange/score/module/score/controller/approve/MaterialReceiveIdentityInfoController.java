package com.orange.score.module.score.controller.approve;

import com.github.pagehelper.PageInfo;
import com.orange.score.common.core.Result;
import com.orange.score.common.exception.AuthBusinessException;
import com.orange.score.common.tools.freemarker.FreeMarkerUtil;
import com.orange.score.common.tools.plugins.FormItem;
import com.orange.score.common.utils.CamelUtil;
import com.orange.score.common.utils.PageConvertUtil;
import com.orange.score.common.utils.ResponseUtil;
import com.orange.score.database.core.model.Region;
import com.orange.score.database.score.model.*;
import com.orange.score.module.core.service.ICommonQueryService;
import com.orange.score.module.core.service.IDictService;
import com.orange.score.module.core.service.IRegionService;
import com.orange.score.module.score.service.*;
import com.orange.score.module.security.SecurityUser;
import com.orange.score.module.security.SecurityUtil;
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
@RequestMapping("/api/score/materialReceive/identityInfo")
public class MaterialReceiveIdentityInfoController {

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
    private IOnlinePersonMaterialService iOnlinePersonMaterialService;

    @Autowired
    private IRegionService iRegionService;

    @Autowired
    private IOfficeService iOfficeService;

    @Autowired
    private IBatchConfService iBatchConfService;

    @GetMapping(value = "/receiving")
    @ResponseBody
    public Result receiving(ScoreRecord scoreRecord, @RequestParam(value = "sort_", required = false) String sort_,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        List<Integer> roles = userService.findUserDepartmentRoleByUserId(userId);
        if (CollectionUtils.isEmpty(roles)) throw new AuthBusinessException("用户没有任何部门角色");
        if (scoreRecord.getBatchId() == null) {
            BatchConf batchConf = new BatchConf();
            batchConf.setStatus(1);
            List<BatchConf> list = iBatchConfService.selectByFilter(batchConf);
            if (list.size() > 0) {
                scoreRecord.setBatchId(list.get(0).getId());
            }
        }
        Map argMap = new HashMap();
        argMap.put("status", Collections.singletonList(2));
        argMap.put("opRoleId", roles);
        SecurityUser securityUser = SecurityUtil.getCurrentSecurityUser();
        if (securityUser.getUserType() == 0) {
            argMap.put("acceptAddressId", 1);
        } else if (securityUser.getUserType() == 1) {
            argMap.put("acceptAddressId", 2);
        }
        if (StringUtils.isNotEmpty(scoreRecord.getPersonIdNum())) {
            argMap.put("personIdNum", scoreRecord.getPersonIdNum());
        }
        if (StringUtils.isNotEmpty(scoreRecord.getPersonName())) {
            argMap.put("personName", scoreRecord.getPersonName());
        }
        if (scoreRecord.getBatchId() != null) {
            argMap.put("batchId", scoreRecord.getBatchId());
        }
        if (StringUtils.isNotEmpty(sort_)) {
            String[] arr = sort_.split("_");
            if (arr.length == 2) {
                if ("desc".endsWith(arr[1])) {
                    argMap.put("orderBy", CamelUtil.camelToUnderline(arr[0]));
                    argMap.put("orderType", "desc");
                } else {
                    argMap.put("orderBy", CamelUtil.camelToUnderline(arr[0]));
                    argMap.put("orderType", "asc");
                }
            }
        }
        PageInfo<ScoreRecord> pageInfo = iScoreRecordService.selectIdentityInfoByPage(argMap, pageNum, pageSize);
        return ResponseUtil.success(PageConvertUtil.grid(pageInfo));
    }

    @GetMapping(value = "/received")
    @ResponseBody
    public Result received(ScoreRecord scoreRecord, @RequestParam(value = "sort_", required = false) String sort_,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        List<Integer> roles = userService.findUserDepartmentRoleByUserId(userId);
        if (CollectionUtils.isEmpty(roles)) throw new AuthBusinessException("用户没有任何部门角色");
        if (scoreRecord.getBatchId() == null) {
            BatchConf batchConf = new BatchConf();
            batchConf.setStatus(1);
            List<BatchConf> list = iBatchConfService.selectByFilter(batchConf);
            if (list.size() > 0) {
                scoreRecord.setBatchId(list.get(0).getId());
            }
        }
        Map argMap = new HashMap();
        argMap.put("status", Arrays.asList(3, 4));
        argMap.put("opRoleId", roles);
        SecurityUser securityUser = SecurityUtil.getCurrentSecurityUser();
        if (securityUser.getUserType() == 0) {
            argMap.put("acceptAddressId", 1);
        } else if (securityUser.getUserType() == 1) {
            argMap.put("acceptAddressId", 2);
        }
        if (StringUtils.isNotEmpty(scoreRecord.getPersonIdNum())) {
            argMap.put("personIdNum", scoreRecord.getPersonIdNum());
        }
        if (StringUtils.isNotEmpty(scoreRecord.getPersonName())) {
            argMap.put("personName", scoreRecord.getPersonName());
        }
        if (scoreRecord.getBatchId() != null) {
            argMap.put("batchId", scoreRecord.getBatchId());
        }
        if (StringUtils.isNotEmpty(sort_)) {
            String[] arr = sort_.split("_");
            if (arr.length == 2) {
                if ("desc".endsWith(arr[1])) {
                    argMap.put("orderBy", CamelUtil.camelToUnderline(arr[0]));
                    argMap.put("orderType", "desc");
                } else {
                    argMap.put("orderBy", CamelUtil.camelToUnderline(arr[0]));
                    argMap.put("orderType", "asc");
                }
            }
        }
        PageInfo<ScoreRecord> pageInfo = iScoreRecordService.selectIdentityInfoByPage(argMap, pageNum, pageSize);
        return ResponseUtil.success(PageConvertUtil.grid(pageInfo));
    }

    @GetMapping("/detailAll")
    public Result detailAll(@RequestParam Integer identityInfoId) throws FileNotFoundException {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        List<Integer> roles = userService.findUserDepartmentRoleByUserId(userId);
        if (CollectionUtils.isEmpty(roles)) throw new AuthBusinessException("用户未设置角色");
        Map params = new HashMap();
        List<Map> mlist = new ArrayList<>();
        List<ScoreRecord> indicatorIdList = iScoreRecordService
                .selectIndicatorIdsByIdentityInfoIdAndRoleIds(identityInfoId, roles);
        for (ScoreRecord item : indicatorIdList) {
            Map msMap = new HashMap();
            Indicator indicator = iIndicatorService.findById(item.getIndicatorId());
            msMap.put("indicator", indicator);
            List<MaterialInfo> materialInfos = iMaterialInfoService.findByIndicatorId(item.getIndicatorId());
            if (materialInfos.size() == 0) continue;
            msMap.put("materialInfos", materialInfos);
            msMap.put("roleId", item.getOpRoleId());
            msMap.put("opRole", item.getOpRole());
            mlist.add(msMap);
        }
        params.put("mlist", mlist);
        IdentityInfo person = iIdentityInfoService.findById(identityInfoId);
        if (person == null) {
            person = new IdentityInfo();
        }
        params.put("person", person);
        List<MaterialInfo> allMaterialInfos = iMaterialInfoService.findAll();
        Map mMap = new HashMap();
        for (MaterialInfo materialInfo : allMaterialInfos) {
            mMap.put(materialInfo.getId() + "", materialInfo.getName());
        }
        params.put("allMaterialInfos", allMaterialInfos);

        List<Integer> indicatorIds = iIndicatorService.selectIndicatorIdByRoleIds(roles);
        Set<Integer> roleMidSet = new HashSet<>();
        for (Integer itemId : indicatorIds) {
            List<Integer> iIds = iIndicatorService.selectBindMaterialIds(itemId);
            for (Integer iId : iIds) {
                if (!roleMidSet.contains(iId)) {
                    roleMidSet.add(iId);
                }
            }
        }
        List<MaterialInfo> materialInfoList = iMaterialInfoService.findAll();
        List<MaterialInfo> roleMaterialInfoList = new ArrayList<>();
        for (MaterialInfo materialInfo : materialInfoList) {
            if (roles.contains(3) || roles.contains(4)) {
                if (materialInfo.getIsUpload() == 1) {
                    if (roleMidSet.contains(materialInfo.getId())) {
                        roleMaterialInfoList.add(materialInfo);
                    }
                }
                //公安单独处理随迁信息
                if (roles.contains(4) && Arrays.asList(1011, 1017, 1013, 1014, 17).contains(materialInfo.getId())) {
                    roleMaterialInfoList.add(materialInfo);
                }
            }
        }
        Condition condition = new Condition(OnlinePersonMaterial.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("personId", person.getId());
        criteria.andEqualTo("batchId", person.getBatchId());
        List<OnlinePersonMaterial> uploadMaterialList = iOnlinePersonMaterialService.findByCondition(condition);
        List<OnlinePersonMaterial> roleUploadMaterialList = new ArrayList<>();
        for (OnlinePersonMaterial onlinePersonMaterial : uploadMaterialList) {
            onlinePersonMaterial.setMaterialInfoName((String) mMap.get(onlinePersonMaterial.getMaterialInfoId() + ""));
            if (roles.contains(3) || roles.contains(4)) {
                if (roleMidSet.contains(onlinePersonMaterial.getMaterialInfoId())) {
                    onlinePersonMaterial
                            .setMaterialInfoName((String) mMap.get(onlinePersonMaterial.getMaterialInfoId() + ""));
                    roleUploadMaterialList.add(onlinePersonMaterial);
                }
                //公安单独处理随迁信息
                if (roles.contains(4) && Arrays.asList(1011, 1017, 1013, 1014, 17)
                        .contains(onlinePersonMaterial.getId())) {
                    roleUploadMaterialList.add(onlinePersonMaterial);
                }
            }
        }
        params.put("onlinePersonMaterials", roleUploadMaterialList);
        for (MaterialInfo materialInfo : roleMaterialInfoList) {
            for (OnlinePersonMaterial onlinePersonMaterial : roleUploadMaterialList) {
                if (materialInfo.getId().intValue() == onlinePersonMaterial.getMaterialInfoId().intValue()) {
                    materialInfo.setOnlinePersonMaterial(onlinePersonMaterial);
                }
            }
        }
        params.put("materialInfos", roleMaterialInfoList);

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
        condition = new Condition(Region.class);
        criteria = condition.createCriteria();
        criteria.andEqualTo("level", 1);
        List<Region> provinceList = iRegionService.findByCondition(condition);
        params.put("provinceList", provinceList);
        condition = new Condition(Region.class);
        criteria = condition.createCriteria();
        criteria.andEqualTo("level", 2);
        List<Region> cityList = iRegionService.findByCondition(condition);
        params.put("cityList", cityList);
        condition = new Condition(Region.class);
        criteria = condition.createCriteria();
        criteria.andEqualTo("level", 3);
        List<Region> areaList = iRegionService.findByCondition(condition);
        params.put("areaList", areaList);

        condition = new Condition(Office.class);
        criteria = condition.createCriteria();
        criteria.andEqualTo("regionLevel", 1);
        List<Office> officeList1 = iOfficeService.findByCondition(condition);
        params.put("officeList1", officeList1);

        condition = new Condition(Office.class);
        criteria = condition.createCriteria();
        criteria.andEqualTo("regionLevel", 2);
        List<Office> officeList2 = iOfficeService.findByCondition(condition);
        params.put("officeList2", officeList2);
        Map result = new HashMap();
        List<String> mCheckList = new ArrayList<>();
        condition = new Condition(MaterialAcceptRecord.class);
        criteria = condition.createCriteria();
        criteria.andIn("roleId", roles);
        criteria.andEqualTo("personId", identityInfoId);
        criteria.andEqualTo("batchId", person.getBatchId());
        List<MaterialAcceptRecord> materials = iMaterialAcceptRecordService.findByCondition(condition);
        for (MaterialAcceptRecord item : materials) {
            mCheckList.add(item.getIndicatorId() + "_" + item.getMaterialId() + "_" + item.getRoleId());
        }
        result.put("mCheckList", mCheckList);
        String templatePath = ResourceUtils.getFile("classpath:templates/").getPath();
        String html = FreeMarkerUtil.getHtmlStringFromTemplate(templatePath, "material_info_identity.ftl", params);
        result.put("html", html);
        return ResponseUtil.success(result);
    }

    @GetMapping(value = "/formItems")
    @ResponseBody
    public Result formItems() {
        List<FormItem> formItems = iCommonQueryService.selectFormItemsByTable("t_pb_score_record");
        List searchItems = iCommonQueryService.selectSearchItemsByTable("t_pb_score_record");
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
        List<Integer> roles = userService.findUserDepartmentRoleByUserId(userId);
        if (CollectionUtils.isEmpty(roles)) throw new AuthBusinessException("用户未设置角色");
        IdentityInfo person = iIdentityInfoService.findById(personId);
        if (person.getHallStatus() == 8) throw new AuthBusinessException("用户资格已取消");
        for (String mId : mIds) {
            String[] mIdArr = mId.split("_");
            if (mIdArr.length != 3) {
                continue;
            }
            Integer indicatorId = Integer.valueOf(mIdArr[0]);
            Integer materialId = Integer.valueOf(mIdArr[1]);
            Integer roleId = Integer.valueOf(mIdArr[2]);
            MaterialInfo materialInfo = iMaterialInfoService.findById(materialId);
            MaterialAcceptRecord materialAcceptRecord = new MaterialAcceptRecord();
            materialAcceptRecord.setIndicatorId(indicatorId);
            materialAcceptRecord.setBatchId(person.getBatchId());
            materialAcceptRecord.setPersonId(personId);
            materialAcceptRecord.setMaterialId(materialId);
            materialAcceptRecord.setRoleId(roleId);
            List<MaterialAcceptRecord> materialAcceptRecords = iMaterialAcceptRecordService
                    .findByT(materialAcceptRecord);
            if (materialAcceptRecords.size() > 0) {
                continue;
            }
            materialAcceptRecord.setMaterialName(materialInfo.getName());
            materialAcceptRecord.setMaterialId(materialInfo.getId());
            materialAcceptRecord.setStatus(1);
            iMaterialAcceptRecordService.save(materialAcceptRecord);
        }
        Condition condition = new Condition(ScoreRecord.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("personId", personId);
        criteria.andEqualTo("batchId", person.getBatchId());
        criteria.andIn("opRoleId", roles);
        List<ScoreRecord> scoreRecords = iScoreRecordService.findByCondition(condition);
        for (ScoreRecord scoreRecord : scoreRecords) {
            scoreRecord.setStatus(3);
            scoreRecord.setSubmitDate(new Date());
            iScoreRecordService.update(scoreRecord);
        }
        return ResponseUtil.success();
    }

}
