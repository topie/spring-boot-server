package com.orange.score.module.score.controller.info;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.orange.score.common.core.Result;
import com.orange.score.common.exception.AuthBusinessException;
import com.orange.score.common.tools.excel.ExcelFileUtil;
import com.orange.score.common.tools.freemarker.FreeMarkerUtil;
import com.orange.score.common.tools.plugins.FormItem;
import com.orange.score.common.utils.CollectionUtil;
import com.orange.score.common.utils.Option;
import com.orange.score.common.utils.PageConvertUtil;
import com.orange.score.common.utils.ResponseUtil;
import com.orange.score.database.core.model.Log;
import com.orange.score.database.core.model.Region;
import com.orange.score.database.score.model.*;
import com.orange.score.module.core.service.ICommonQueryService;
import com.orange.score.module.core.service.IDictService;
import com.orange.score.module.core.service.ILogService;
import com.orange.score.module.core.service.IRegionService;
import com.orange.score.module.score.service.*;
import com.orange.score.module.score.ws.SOAP3Response;
import com.orange.score.module.score.ws.WebServiceClient;
import com.orange.score.module.security.SecurityUser;
import com.orange.score.module.security.SecurityUtil;
import com.orange.score.module.security.service.UserService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.regexp.RE;
import org.dom4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by chenJz1012 on 2018-04-08.
 */
@RestController
@RequestMapping("/api/score/info/identityInfo")
public class IdentityInfoController {

    @Autowired
    private IIdentityInfoService iIdentityInfoService;

    @Autowired
    private ICommonQueryService iCommonQueryService;

    @Autowired
    private IDictService iDictService;

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
    private ICompanyInfoService iCompanyInfoService;

    @Autowired
    private IOnlinePersonMaterialService iOnlinePersonMaterialService;

    @Autowired
    private IMaterialAcceptRecordService iMaterialAcceptRecordService;

    @Autowired
    private IIndicatorService iIndicatorService;

    @Autowired
    private UserService userService;

    @Autowired
    private IRegionService iRegionService;

    @Autowired
    private IOfficeService iOfficeService;

    @Autowired
    private ILogService iLogService;

    @Autowired
    private IScoreRecordService iScoreRecordService;

    @Autowired
    private IBatchConfService iBatchConfService;

    @Autowired
    private IPersonBatchStatusRecordService iPersonBatchStatusRecordService;

    @Value("${upload.folder}")
    String uploadFolder;

    @Value("${upload.folder}")
    private String uploadPath;

    @GetMapping(value = "/list")
    @ResponseBody
    public Result list(IdentityInfo identityInfo,
                       @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
                       @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize) {
        SecurityUser securityUser = SecurityUtil.getCurrentSecurityUser();
        if (securityUser == null) throw new AuthBusinessException("用户未登录");

        /**
         * 2020年4月29日
         * 增加企业信息查询的字段，因为 IdentityInfo 对象中没有公司名字的字段值，用 rentHouseAddress 来代替
         * 先通过名字查询到公司ID，再把公司ID作为查询条件开始查询
         */
        if (identityInfo.getRentHouseAddress()!=null && identityInfo.getRentHouseAddress()!=""){
            identityInfo.setRentIdNumber(identityInfo.getRentHouseAddress());
//            CompanyInfo companyInfo = new CompanyInfo();
//            companyInfo.setCompanyName(identityInfo.getRentHouseAddress());
//            List<CompanyInfo> list = iCompanyInfoService.selectByFilter(companyInfo);
//            if (list.size()>0){
//                identityInfo.setCompanyId(list.get(list.size()-1).getId());
//            }
        }

        PageInfo<IdentityInfo> pageInfo = iIdentityInfoService.selectByFilterAndPage(identityInfo, pageNum, pageSize);
        if (securityUser.getLoginName().equals("admin")){
            for (IdentityInfo identityInfo1 : pageInfo.getList()){
                // 将admin 占用“曾用名”字段；传到js 端，控制按钮出现消失的机制，只有管理员才能看到“修改材料+打分状态”
                identityInfo1.setFormerName("100");
            }
        }
        return ResponseUtil.success(PageConvertUtil.grid(pageInfo));
    }

    @GetMapping(value = "/formItems")
    @ResponseBody
    public Result formItems() {
        List<FormItem> formItems = iCommonQueryService.selectFormItemsByTable("t_identity_info");
        List searchItems = iCommonQueryService.selectSearchItemsByTable("t_identity_info");
        List<CompanyInfo> companyInfos = iCompanyInfoService.findAll();
        Map result = new HashMap<>();
        result.put("formItems", formItems);
        result.put("searchItems", searchItems);
        Map reservationStatus = iDictService.selectMapByAlias("reservationStatus");
        result.put("reservationStatus", reservationStatus);
        Map hallStatus = iDictService.selectMapByAlias("hallStatus");
        result.put("hallStatus", hallStatus);
        Map companyMap = new HashMap();
//        for (CompanyInfo companyInfo : companyInfos) {
//            companyMap.put(companyInfo.getId(), companyInfo.getCompanyName());
//        }
        result.put("companyNames", companyMap);
        return ResponseUtil.success(result);
    }

    @PostMapping("/insert")
    public Result insert(IdentityInfo identityInfo) {
        iIdentityInfoService.save(identityInfo);
        return ResponseUtil.success(identityInfo.getId());
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        iIdentityInfoService.deleteById(id);
        return ResponseUtil.success();
    }

    @PostMapping("/update")
    public Result update(IdentityInfo identityInfo) {
        iIdentityInfoService.update(identityInfo);
        return ResponseUtil.success();
    }

    /*
    保存后台的数据
     */
    @PostMapping("/updateEdit")
    public Result updateEdit(@RequestParam Integer identityInfoId, @RequestParam String editInfo)
            throws InvocationTargetException, IllegalAccessException {
        SecurityUser securityUser = SecurityUtil.getCurrentSecurityUser();
        if (securityUser == null) throw new AuthBusinessException("用户未登录");
        JSONArray jsonArray = JSONArray.parseArray(editInfo);
        IdentityInfo identityInfo = new IdentityInfo();
        identityInfo.setId(identityInfoId);
        HouseMove houseMove = new HouseMove();
        houseMove.setIdentityInfoId(identityInfoId);
        HouseOther houseOther = new HouseOther();
        houseOther.setIdentityInfoId(identityInfoId);
        for (Object o : jsonArray) {
            String name = ((JSONObject) o).getString("name");
            Object value = ((JSONObject) o).get("value");
            Integer id = ((JSONObject) o).getInteger("id");
            String[] arr = name.split("\\.");
            if (arr.length == 2) {
                switch (arr[0]) {
                    case "person":
                        BeanUtils.copyProperty(identityInfo, arr[1], value);
                        break;
                    case "move":
                        BeanUtils.copyProperty(houseMove, arr[1], value);
                        houseMove.setId(id);
                        break;
                    case "other":
                        BeanUtils.copyProperty(houseOther, arr[1], value);
                        houseOther.setId(id);
                        break;
                    case "relation":
                        HouseRelationship houseRelationship = new HouseRelationship();
                        houseRelationship.setIdentityInfoId(identityInfoId);
                        BeanUtils.copyProperty(houseRelationship, arr[1], value);
                        houseRelationship.setId(id);
                        iHouseRelationshipService.update(houseRelationship);
                        break;
                }

            }
        }
        iIdentityInfoService.update(identityInfo);
        iHouseMoveService.update(houseMove);
        iHouseOtherService.update(houseOther);
        Log log = new Log();
        log.setLogTime(new Date());
        log.setLogContent("修改" + identityInfo.getName() + "信息");
        log.setLogUser(securityUser.getLoginName());
        iLogService.save(log);

        /*
        2020年4月21日
        留痕记录
         */
        PersonBatchStatusRecord personBatchStatusRecord = new PersonBatchStatusRecord();
        personBatchStatusRecord.setPersonId(identityInfo.getId());
        personBatchStatusRecord.setBatchId(identityInfo.getBatchId());
        personBatchStatusRecord.setPersonIdNumber(identityInfo.getIdNumber());
        personBatchStatusRecord.setStatusStr(securityUser.getLoginName()+"修改");
        personBatchStatusRecord.setStatusTime(new Date());
        personBatchStatusRecord.setStatusReason(securityUser.getLoginName()+"修改");
        personBatchStatusRecord.setStatusTypeDesc(securityUser.getLoginName()+"修改");
        personBatchStatusRecord.setStatusInt(1010);
        iPersonBatchStatusRecordService.save(personBatchStatusRecord);

        return ResponseUtil.success();
    }

    @GetMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        IdentityInfo identityInfo = iIdentityInfoService.findById(id);
        return ResponseUtil.success(identityInfo);
    }

    @GetMapping("/detailAll")
    public Result detailAll(@RequestParam Integer identityInfoId,HttpServletRequest request,
                            @RequestParam(value = "template", required = false) String template) throws FileNotFoundException {

        /*
        2020年3月12日
        获得访问者的 IP 地址，用其他内网IP来替换图片地址中的IP
         */
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        Date date = new Date();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf2.format(date)+";审核人员登录的IP地址："+ip);
        String[] strArr = ip.split("\\.");
        String str1 = strArr[0];
        String str2 = strArr[1];
        String str3 = strArr[2];
        String str4 = strArr[3];

//        String str1 = "10";
//        String str2 = "96";
//        String str3 = "49";
//        String str4 = "180";

        boolean flag = false;
        if (Integer.parseInt(str1)==172 && Integer.parseInt(str2)==20 && Integer.parseInt(str3)==211){
            int four = Integer.parseInt(str4);
            if (four==191){
                flag = true;
            }
        }

        if (StringUtils.isEmpty(template)) {
            template = "identity_info";
        }
        Map params = new HashMap();
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        IdentityInfo person = iIdentityInfoService.findById(identityInfoId);


        /**
         * 2020年4月4日
         * 人社受理审核待审核时，将用户名保存下
         */
        SecurityUser securityUser = SecurityUtil.getCurrentSecurityUser();
        List<Integer> roles = userService.findUserRoleByUserId(securityUser.getId());
        if(roles.contains(3)){
            //person.setLockUser2(securityUser.getUsername());
            if(person.getOpuser4()==null || person.getOpuser4()==""){
                person.setOpuser4(securityUser.getUsername());
            }
            iIdentityInfoService.update(person);
        }


        if (person == null) {
            person = new IdentityInfo();
        }
        List<Integer> indicatorIds = iIndicatorService.selectIndicatorIdByRoleIds(roles);
        Set<Integer> roleMidSet = new HashSet<>();
        for (Integer indicatorId : indicatorIds) {
            List<Integer> iIds = iIndicatorService.selectBindMaterialIds(indicatorId);
            for (Integer iId : iIds) {
                if (!roleMidSet.contains(iId)) {
                    roleMidSet.add(iId);
                }
            }
        }

        Condition condition = new Condition(MaterialInfo.class);
        condition.setOrderByClause("sortColumns");
        List<MaterialInfo> materialInfos = iMaterialInfoService.findByCondition(condition);
        List<MaterialInfo> roleMaterialInfoList = new ArrayList<>();
        Map mMap = new HashMap();
        Set<Integer> rolesSet = new HashSet<>(roles);
        //该权限可以查看的所有材料
        for (MaterialInfo materialInfo : materialInfos) {
            mMap.put(materialInfo.getId() + "", materialInfo.getName());
            if (CollectionUtil.isHaveUnionBySet(rolesSet, materialInfo.getMaterialInfoRoleSet())) {
                if (materialInfo.getIsUpload() == 1) {
                    if (roleMidSet.contains(materialInfo.getId())) {
                        roleMaterialInfoList.add(materialInfo);
                    }
                }
            }
        }
        condition = new Condition(OnlinePersonMaterial.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("personId", person.getId());
        criteria.andEqualTo("batchId", person.getBatchId());
        criteria.andNotEqualTo("status", 2);
        List<OnlinePersonMaterial> uploadMaterialList = iOnlinePersonMaterialService.findByCondition(condition);
        List<OnlinePersonMaterial> roleUploadMaterialList = new ArrayList<>();
        //用户上传的材料
        for (OnlinePersonMaterial onlinePersonMaterial : uploadMaterialList) {
            if (roleMidSet.contains(onlinePersonMaterial.getMaterialInfoId())) {
                onlinePersonMaterial.setMaterialInfoName((String) mMap.get(onlinePersonMaterial.getMaterialInfoId() + ""));
                roleUploadMaterialList.add(onlinePersonMaterial);
            }
        }
        params.put("onlinePersonMaterials", roleUploadMaterialList);
        params.put("uploadMaterialList", uploadMaterialList);

        for (MaterialInfo materialInfo : materialInfos) {
            for (OnlinePersonMaterial onlinePersonMaterial : uploadMaterialList) {
                if (materialInfo.getId().intValue() == onlinePersonMaterial.getMaterialInfoId().intValue()) {
                    materialInfo.setOnlinePersonMaterial(onlinePersonMaterial);
                }
            }
        }
        params.put("allMaterialInfos", materialInfos);

        for (MaterialInfo materialInfo : roleMaterialInfoList) {
            for (OnlinePersonMaterial onlinePersonMaterial : roleUploadMaterialList) {
                if (materialInfo.getId().intValue() == onlinePersonMaterial.getMaterialInfoId().intValue()) {
                    materialInfo.setOnlinePersonMaterial(onlinePersonMaterial);
                }
            }
            //若访问者的IP 地址符合
            if (flag && materialInfo.getOnlinePersonMaterial()!=null && materialInfo.getOnlinePersonMaterial().getMaterialUri()!=null){
                String strUri = materialInfo.getOnlinePersonMaterial().getMaterialUri();
                String newStrUri = strUri.replace("218.67.246.52:80","10.96.3.213:8091");
                materialInfo.getOnlinePersonMaterial().setMaterialUri(newStrUri);
            }
        }
        params.put("materialInfos", roleMaterialInfoList);

        List<MaterialInfo> roleMaterialInfoList_2 = new ArrayList<>();//非人社部门的材料列表
        for (MaterialInfo materialInfo : materialInfos){
            if (materialInfo.getArchivingStatus()!=null && materialInfo.getArchivingStatus()==1 ){
                if(flag && materialInfo.getOnlinePersonMaterial()!=null && materialInfo.getOnlinePersonMaterial().getMaterialUri()!=null){
                    String strUri = materialInfo.getOnlinePersonMaterial().getMaterialUri();
                    String newStrUri = strUri.replace("218.67.246.52:80","10.96.3.213:8091");
                    materialInfo.getOnlinePersonMaterial().setMaterialUri(newStrUri);
                }
                roleMaterialInfoList_2.add(materialInfo);
            }
        }
        params.put("materialInfos_2", roleMaterialInfoList_2);

        List<MaterialInfo> roleMaterialInfoList_3 = new ArrayList<>();//人社部门的材料列表
        for (MaterialInfo materialInfo : roleMaterialInfoList){
            if (materialInfo.getArchivingStatus()!=null && materialInfo.getArchivingStatus()==2){
                roleMaterialInfoList_3.add(materialInfo);
            }
        }
        params.put("materialInfos_3", roleMaterialInfoList_3);

        params.put("person", person);
        CompanyInfo companyInfo = iCompanyInfoService.findById(person.getCompanyId());
        if (companyInfo == null) {
            companyInfo = new CompanyInfo();
        }
        params.put("company", companyInfo);
        //添加营业执照,只有人社添加
        if (roles.contains(3)) {
            MaterialInfo businessLicenseMaterialInfo = new MaterialInfo();
            businessLicenseMaterialInfo.setId(-1);
            businessLicenseMaterialInfo.setName("经办人身份证");
            OnlinePersonMaterial businessLicenseMaterial = new OnlinePersonMaterial();
            businessLicenseMaterial.setMaterialUri(companyInfo.getBusinessLicenseSrc());
            businessLicenseMaterial.setId(-1);
            businessLicenseMaterial.setPersonId(-1);
            businessLicenseMaterial.setMaterialInfoName("经办人身份证");
            businessLicenseMaterialInfo.setOnlinePersonMaterial(businessLicenseMaterial);
            /*
            2020年3月12日
            若符合人社部门的IP地址，就替换
             */
            if (flag && businessLicenseMaterial.getMaterialUri()!=null){
                String strUri = businessLicenseMaterial.getMaterialUri();
                String newStrUri = strUri.replace("218.67.246.52:80","10.96.3.213:8091");
                businessLicenseMaterial.setMaterialUri(newStrUri);
            }
            roleMaterialInfoList.add(0, businessLicenseMaterialInfo);
        }
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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String downloadDate = sdf.format(new Date());
        params.put("downloadDate", downloadDate);

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
        if (roles.contains(3)) {
            params.put("renshe", true);
        }
        String templatePath = ResourceUtils.getFile("classpath:templates/").getPath();
        String html = FreeMarkerUtil.getHtmlStringFromTemplate(templatePath, template + ".ftl", params);
        Map result = new HashMap();

        condition = new Condition(MaterialAcceptRecord.class);
        criteria = condition.createCriteria();
        criteria.andEqualTo("personId", identityInfoId);
        criteria.andEqualTo("batchId", person.getBatchId());
        List<MaterialAcceptRecord> materials = iMaterialAcceptRecordService.findByCondition(condition);
        List<Integer> submittedMids = new ArrayList<>();
        Set<Integer> hSet = new HashSet<>();
        for (MaterialAcceptRecord item : materials) {
            if (!hSet.contains(item.getMaterialId())) {
                submittedMids.add(item.getMaterialId());
                hSet.add(item.getMaterialId());
            }
        }
        result.put("cIds", submittedMids);
        result.put("html", html);
        return ResponseUtil.success(result);
    }


    /**
     *2019年9月11日，添加后台窗口人员上传图片
     * @return
     */
    @RequestMapping(value = "/adminUploadImage", method = { RequestMethod.POST })
    @ResponseBody
    public Result adminUploadImage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        String userName =  SecurityUtil.getCurrentUserName();// 修改的用户名

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        String id = request.getParameter("id"); // 上传材料的id
        String identityInfoId = request.getParameter("identityInfoId"); // 申请人ID
        /*
        获取当前申请人的信息
         */
        IdentityInfo identityInfo = iIdentityInfoService.findById(Integer.parseInt(identityInfoId));

        MultipartFile file = multipartRequest.getFile(id);
        String fileName = file.getOriginalFilename();
        String fileName2 = file.getName();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = sdf.format(new Date());
        String timeStr = sdf2.format(new Date());
        String imageStr = timeStr+"_"+fileName;

        String savePath = "/data/upload" + "/" +SecurityUtil.getCurrentUserName()+"/" + dateStr ;
        //String savePath = "I:\\data";
        File targetFile = new File(savePath);
        if (!targetFile.exists()){
            targetFile.mkdirs();
        }

        String downloadPath = "http://218.67.246.52:80/wu-score/shopPic/"+SecurityUtil.getCurrentUserName()+"/"+dateStr+"/"+imageStr;

        // 开始保存
        InputStream stream = file.getInputStream();
        FileOutputStream fs = new FileOutputStream(savePath+"/"+imageStr);
        byte[] buffer = new byte[1024*1024];
        int byteread = 0;
        while ((byteread = stream.read(buffer))!=-1){
            fs.write(buffer,0,byteread);
            fs.flush();
        }
        fs.close();
        stream.close();

        Condition condition_pm = new Condition(OnlinePersonMaterial.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria_pm = condition_pm.createCriteria();
        criteria_pm.andEqualTo("personId", Integer.parseInt(identityInfoId));
        criteria_pm.andEqualTo("materialInfoId", Integer.parseInt(id));
        List<OnlinePersonMaterial> list_pm = iOnlinePersonMaterialService.findByCondition(condition_pm);

        if (list_pm.size() == 0){
            /*
            若没有保存过同类型的材料，就保存；
            否则覆盖原有的图片材料地址；
             */
            OnlinePersonMaterial onlinePersonMaterial = new OnlinePersonMaterial();
            onlinePersonMaterial.setPersonId(identityInfo.getId());
            onlinePersonMaterial.setBatchId(identityInfo.getBatchId());// 当前批次
            onlinePersonMaterial.setMaterialInfoId(Integer.parseInt(id));
            onlinePersonMaterial.setMaterialName(fileName); // 上传的图片名字
            onlinePersonMaterial.setMaterialUri(downloadPath); // 图片地址
            onlinePersonMaterial.setcTime(new Date()); // 创建时间
            iOnlinePersonMaterialService.save(onlinePersonMaterial);
        }else{
            list_pm.get(0).setMaterialUri(downloadPath);
            iOnlinePersonMaterialService.update(list_pm.get(0));
        }

        /*
        留痕记录
         */
        PersonBatchStatusRecord personBatchStatusRecord = new PersonBatchStatusRecord();
        personBatchStatusRecord.setPersonId(identityInfo.getId());
        personBatchStatusRecord.setBatchId(identityInfo.getBatchId());
        personBatchStatusRecord.setPersonIdNumber(identityInfo.getIdNumber());
        personBatchStatusRecord.setStatusTypeDesc("后台上传材料");
        personBatchStatusRecord.setStatusTime(new Date());
        personBatchStatusRecord.setStatusStr(userName+"上传成功");
        if (list_pm.size()==0){
            personBatchStatusRecord.setStatusReason("窗口误操作导致");
        }else {
            personBatchStatusRecord.setStatusReason("修改前的图片地址："+list_pm.get(0).getMaterialUri());
        }
        personBatchStatusRecord.setStatusInt(105);
        iPersonBatchStatusRecordService.save(personBatchStatusRecord);

        return ResponseUtil.success();
    }


    /*
    2019年9月5日
    后台管理员可以修改申请人的材料接收与打分情况
     */
    @GetMapping("/changeMaterialAndScore")
    public Result changeMaterialAndScore(@RequestParam Integer identityInfoId,
                                         @RequestParam(value = "template", required = false) String template) throws FileNotFoundException {
        Map params = new HashMap();

        Condition condition = new Condition(ScoreRecord.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("personId", identityInfoId);
        List<ScoreRecord> scoreRecords = iScoreRecordService.findByCondition(condition);

        params.put("scoreRecords",scoreRecords);
        String templatePath = ResourceUtils.getFile("classpath:templates/").getPath();
        String html = FreeMarkerUtil.getHtmlStringFromTemplate(templatePath, template + ".ftl", params);
        Map result = new HashMap();
        result.put("html", html);
        return ResponseUtil.success(result);
    }

    /**
     * 2019年9月9日
     * 后台窗口界面修改申请人的材料接收与打分
     * @param ids
     * @param statuss
     * @param scoreValues
     * @param indicatorNames
     * @return
     */
    @PostMapping("/saveChange")
    public Result saveChange(@RequestParam String ids, @RequestParam String statuss, @RequestParam String scoreValues, @RequestParam String indicatorNames) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        String userName =  SecurityUtil.getCurrentUserName();// 修改的用户名

        JSONArray jsonArrayids = JSONArray.parseArray(ids);
        JSONArray jsonArraystatuss = JSONArray.parseArray(statuss);
        JSONArray jsonArrayscoreValues = JSONArray.parseArray(scoreValues);
        JSONArray jsonArrayindicatorNames = JSONArray.parseArray(indicatorNames);
        ScoreRecord scoreRecord = null;
        String statusAndScore = "";
        for (int i=0; i<jsonArrayids.size(); i++){
            String id = ((JSONObject) jsonArrayids.get(i)).getString("id");
            String status = ((JSONObject) jsonArraystatuss.get(i)).getString("status");
            String scoreValue = ((JSONObject) jsonArrayscoreValues.get(i)).getString("scoreValue");
            //String indicatorName = ((JSONObject) jsonArrayindicatorNames.get(i)).getString("indicatorName");
            //System.out.println("参数,id："+id+",status:"+status+",scoreValue:"+scoreValue+",indicatorName:"+indicatorName);
            scoreRecord = iScoreRecordService.findById(Integer.parseInt(id.replace(",","")));
            statusAndScore = statusAndScore +scoreRecord.getStatus()+"-"+scoreRecord.getScoreValue()+";";
            scoreRecord.setStatus(Integer.parseInt(status));
            if (!StringUtils.isEmpty(scoreValue)){
                scoreRecord.setScoreValue(new BigDecimal(scoreValue));
            }
            iScoreRecordService.update(scoreRecord);
        }
        /*
        留痕记录
         */
        PersonBatchStatusRecord personBatchStatusRecord = new PersonBatchStatusRecord();
        personBatchStatusRecord.setPersonId(scoreRecord.getPersonId());
        personBatchStatusRecord.setBatchId(scoreRecord.getBatchId());
        personBatchStatusRecord.setPersonIdNumber(scoreRecord.getPersonIdNum());
        personBatchStatusRecord.setStatusTypeDesc("修改材料与打分状态");
        personBatchStatusRecord.setStatusTime(new Date());
        personBatchStatusRecord.setStatusStr(userName+"修改成功");
        personBatchStatusRecord.setStatusReason("修改前的状态与分数ID升序："+":"+statusAndScore);
        personBatchStatusRecord.setStatusInt(104);
        iPersonBatchStatusRecordService.save(personBatchStatusRecord);
        return ResponseUtil.success();
    }

    @GetMapping("/materialSupply")
    public Result materialSupply(HttpServletRequest request,@RequestParam Integer identityInfoId) throws FileNotFoundException {

        /*
        2020年3月12日
        获得访问者的 IP 地址，用其他内网IP来替换图片地址中的IP
         */
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        Date date = new Date();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf2.format(date)+";审核人员登录的IP地址："+ip);
        String[] strArr = ip.split("\\.");
        String str1 = strArr[0];
        String str2 = strArr[1];
        String str3 = strArr[2];
        String str4 = strArr[3];

//        String str1 = "10";
//        String str2 = "96";
//        String str3 = "49";
//        String str4 = "180";

        boolean flag = false;
        if (Integer.parseInt(str1)==172 && Integer.parseInt(str2)==20 && Integer.parseInt(str3)==211){
            int four = Integer.parseInt(str4);
            if (four==191){
                flag = true;
            }
        }

        Map params = new HashMap();
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        IdentityInfo person = iIdentityInfoService.findById(identityInfoId);
        if (person == null) {
            person = new IdentityInfo();
        }
        List<Integer> roles = userService.findUserDepartmentRoleByUserId(userId);
        List<Integer> indicatorIds = iIndicatorService.selectIndicatorIdByRoleIds(roles);
        Set<Integer> roleMidSet = new HashSet<>();
        for (Integer indicatorId : indicatorIds) {
            List<Integer> iIds = iIndicatorService.selectBindMaterialIds(indicatorId);
            for (Integer iId : iIds) {
                if (!roleMidSet.contains(iId)) {
                    roleMidSet.add(iId);
                }
            }
        }
        List<MaterialInfo> materialInfos = iMaterialInfoService.findAll();
        List<MaterialInfo> roleMaterialInfoList = new ArrayList<>();
        Map mMap = new HashMap();
        Set<Integer> rolesSet = new HashSet<>(roles);
        //该权限可以查看的所有材料
        //添加营业执照,只有人社添加
        CompanyInfo companyInfo = iCompanyInfoService.findById(person.getCompanyId());
        if (companyInfo == null) {
            companyInfo = new CompanyInfo();
        }
        params.put("company", companyInfo);
        if (roles.contains(3)) {
            MaterialInfo businessLicenseMaterialInfo = new MaterialInfo();
            businessLicenseMaterialInfo.setId(-1);
            businessLicenseMaterialInfo.setName("经办人身份证");
            OnlinePersonMaterial businessLicenseMaterial = new OnlinePersonMaterial();
            businessLicenseMaterial.setMaterialUri(companyInfo.getBusinessLicenseSrc());
            businessLicenseMaterial.setId(-1);
            businessLicenseMaterial.setPersonId(-1);
            businessLicenseMaterial.setMaterialInfoName("经办人身份证");
            businessLicenseMaterialInfo.setOnlinePersonMaterial(businessLicenseMaterial);
            roleMaterialInfoList.add(0, businessLicenseMaterialInfo);
        }

        for (MaterialInfo materialInfo : materialInfos) {
            mMap.put(materialInfo.getId() + "", materialInfo.getName());
            if (CollectionUtil.isHaveUnionBySet(rolesSet, materialInfo.getMaterialInfoRoleSet())) {
                if (materialInfo.getIsUpload() == 1) {
                    if (roleMidSet.contains(materialInfo.getId())) {
                        roleMaterialInfoList.add(materialInfo);
                    }
                }
            }
        }

        Condition condition = new Condition(OnlinePersonMaterial.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("personId", person.getId());
        criteria.andEqualTo("batchId", person.getBatchId());
        criteria.andNotEqualTo("status", 2);
        List<OnlinePersonMaterial> uploadMaterialList = iOnlinePersonMaterialService.findByCondition(condition);
        List<OnlinePersonMaterial> roleUploadMaterialList = new ArrayList<>();
        //用户上传的材料
        for (OnlinePersonMaterial onlinePersonMaterial : uploadMaterialList) {
            if (roleMidSet.contains(onlinePersonMaterial.getMaterialInfoId())) {
                onlinePersonMaterial.setMaterialInfoName((String) mMap.get(onlinePersonMaterial.getMaterialInfoId() + ""));
                roleUploadMaterialList.add(onlinePersonMaterial);
            }
        }
        params.put("onlinePersonMaterials", roleUploadMaterialList);
        params.put("uploadMaterialList", uploadMaterialList);

        for (MaterialInfo materialInfo : roleMaterialInfoList) {
            for (OnlinePersonMaterial onlinePersonMaterial : roleUploadMaterialList) {
                if (materialInfo.getId().intValue() == onlinePersonMaterial.getMaterialInfoId().intValue()) {
                    materialInfo.setOnlinePersonMaterial(onlinePersonMaterial);
                }
            }

            //若访问者的IP 地址符合
            if (flag && materialInfo.getOnlinePersonMaterial()!=null && materialInfo.getOnlinePersonMaterial().getMaterialUri()!=null){
                String strUri = materialInfo.getOnlinePersonMaterial().getMaterialUri();
                String newStrUri = strUri.replace("218.67.246.52:80","10.96.3.213:8091");
                materialInfo.getOnlinePersonMaterial().setMaterialUri(newStrUri);
            }
        }
        params.put("materialInfos", roleMaterialInfoList);

        for (MaterialInfo materialInfo : materialInfos) {
            for (OnlinePersonMaterial onlinePersonMaterial : uploadMaterialList) {
                if (materialInfo.getId().intValue() == onlinePersonMaterial.getMaterialInfoId().intValue()) {
                    materialInfo.setOnlinePersonMaterial(onlinePersonMaterial);
                }
            }
        }
        params.put("allMaterialInfos", materialInfos);

        params.put("person", person);

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
        String html = FreeMarkerUtil.getHtmlStringFromTemplate(templatePath, "material_supply.ftl", params);
        Map result = new HashMap();

        condition = new Condition(MaterialAcceptRecord.class);
        criteria = condition.createCriteria();
        criteria.andEqualTo("personId", identityInfoId);
        criteria.andEqualTo("batchId", person.getBatchId());
        List<MaterialAcceptRecord> materials = iMaterialAcceptRecordService.findByCondition(condition);
        List<Integer> submittedMids = new ArrayList<>();
        Set<Integer> hSet = new HashSet<>();
        for (MaterialAcceptRecord item : materials) {
            if (!hSet.contains(item.getMaterialId())) {
                submittedMids.add(item.getMaterialId());
                hSet.add(item.getMaterialId());
            }
        }
        result.put("cIds", submittedMids);
        result.put("html", html);
        return ResponseUtil.success(result);
    }

    @GetMapping("/materialSupply2")
    public Result materialSupply2(HttpServletRequest request,@RequestParam Integer identityInfoId) throws FileNotFoundException {

        /*
        2020年3月12日
        获得访问者的 IP 地址，用其他内网IP来替换图片地址中的IP
         */
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        Date date = new Date();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf2.format(date)+";审核人员登录的IP地址："+ip);
        String[] strArr = ip.split("\\.");
        String str1 = strArr[0];
        String str2 = strArr[1];
        String str3 = strArr[2];
        String str4 = strArr[3];

//        String str1 = "10";
//        String str2 = "96";
//        String str3 = "49";
//        String str4 = "180";

        boolean flag = false;
        if (Integer.parseInt(str1)==172 && Integer.parseInt(str2)==20 && Integer.parseInt(str3)==211){
            int four = Integer.parseInt(str4);
            if (four==191){
                flag = true;
            }
        }

        Map params = new HashMap();
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new AuthBusinessException("用户未登录");
        IdentityInfo person = iIdentityInfoService.findById(identityInfoId);
        if (person == null) {
            person = new IdentityInfo();
        }
        List<Integer> roles = userService.findUserDepartmentRoleByUserId(userId);
        List<Integer> indicatorIds = iIndicatorService.selectIndicatorIdByRoleIds(roles);
        Set<Integer> roleMidSet = new HashSet<>();
        for (Integer indicatorId : indicatorIds) {
            List<Integer> iIds = iIndicatorService.selectBindMaterialIds(indicatorId);
            for (Integer iId : iIds) {
                if (!roleMidSet.contains(iId)) {
                    roleMidSet.add(iId);
                }
            }
        }
        List<MaterialInfo> materialInfos = iMaterialInfoService.findAll();
        List<MaterialInfo> roleMaterialInfoList = new ArrayList<>();
        Map mMap = new HashMap();
        Set<Integer> rolesSet = new HashSet<>(roles);
        //该权限可以查看的所有材料
        //添加营业执照,只有人社添加
        CompanyInfo companyInfo = iCompanyInfoService.findById(person.getCompanyId());
        if (companyInfo == null) {
            companyInfo = new CompanyInfo();
        }
        params.put("company", companyInfo);
        if (roles.contains(3)) {
            MaterialInfo businessLicenseMaterialInfo = new MaterialInfo();
            businessLicenseMaterialInfo.setId(-1);
            businessLicenseMaterialInfo.setName("经办人身份证");
            OnlinePersonMaterial businessLicenseMaterial = new OnlinePersonMaterial();
            businessLicenseMaterial.setMaterialUri(companyInfo.getBusinessLicenseSrc());
            businessLicenseMaterial.setId(-1);
            businessLicenseMaterial.setPersonId(-1);
            businessLicenseMaterial.setMaterialInfoName("经办人身份证");
            businessLicenseMaterialInfo.setOnlinePersonMaterial(businessLicenseMaterial);

            /*
            2020年3月12日
            若符合人社部门的IP地址，就替换
             */
            if (flag && businessLicenseMaterial.getMaterialUri()!=null){
                String strUri = businessLicenseMaterial.getMaterialUri();
                String newStrUri = strUri.replace("218.67.246.52:80","10.96.3.213:8091");
                businessLicenseMaterial.setMaterialUri(newStrUri);
            }

            roleMaterialInfoList.add(0, businessLicenseMaterialInfo);
        }

        for (MaterialInfo materialInfo : materialInfos) {
            mMap.put(materialInfo.getId() + "", materialInfo.getName());
            if (CollectionUtil.isHaveUnionBySet(rolesSet, materialInfo.getMaterialInfoRoleSet())) {
                if (materialInfo.getIsUpload() == 1) {
                    if (roleMidSet.contains(materialInfo.getId())) {
                        roleMaterialInfoList.add(materialInfo);
                    }
                }
            }
        }

        Condition condition = new Condition(OnlinePersonMaterial.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("personId", person.getId());
        criteria.andEqualTo("batchId", person.getBatchId());
        criteria.andNotEqualTo("status", 2);
        List<OnlinePersonMaterial> uploadMaterialList = iOnlinePersonMaterialService.findByCondition(condition);
        List<OnlinePersonMaterial> roleUploadMaterialList = new ArrayList<>();
        //用户上传的材料
        for (OnlinePersonMaterial onlinePersonMaterial : uploadMaterialList) {
            if (roleMidSet.contains(onlinePersonMaterial.getMaterialInfoId())) {
                onlinePersonMaterial.setMaterialInfoName((String) mMap.get(onlinePersonMaterial.getMaterialInfoId() + ""));
                roleUploadMaterialList.add(onlinePersonMaterial);
            }
        }
        params.put("onlinePersonMaterials", roleUploadMaterialList);
        params.put("uploadMaterialList", uploadMaterialList);

        for (MaterialInfo materialInfo : roleMaterialInfoList) {
            for (OnlinePersonMaterial onlinePersonMaterial : roleUploadMaterialList) {
                if (materialInfo.getId().intValue() == onlinePersonMaterial.getMaterialInfoId().intValue()) {
                    materialInfo.setOnlinePersonMaterial(onlinePersonMaterial);
                }
            }
            //若访问者的IP 地址符合
            if (flag && materialInfo.getOnlinePersonMaterial()!=null && materialInfo.getOnlinePersonMaterial().getMaterialUri()!=null){
                String strUri = materialInfo.getOnlinePersonMaterial().getMaterialUri();
                String newStrUri = strUri.replace("218.67.246.52:80","10.96.3.213:8091");
                materialInfo.getOnlinePersonMaterial().setMaterialUri(newStrUri);
            }
        }
        params.put("materialInfos", roleMaterialInfoList);

        for (MaterialInfo materialInfo : materialInfos) {
            for (OnlinePersonMaterial onlinePersonMaterial : uploadMaterialList) {
                if (materialInfo.getId().intValue() == onlinePersonMaterial.getMaterialInfoId().intValue()) {
                    materialInfo.setOnlinePersonMaterial(onlinePersonMaterial);
                }
            }
        }
        params.put("allMaterialInfos", materialInfos);

        params.put("person", person);

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
        String html = FreeMarkerUtil.getHtmlStringFromTemplate(templatePath, "material_supply.ftl", params);
        Map result = new HashMap();

        condition = new Condition(MaterialAcceptRecord.class);
        criteria = condition.createCriteria();
        criteria.andEqualTo("personId", identityInfoId);
        criteria.andEqualTo("batchId", person.getBatchId());
        List<MaterialAcceptRecord> materials = iMaterialAcceptRecordService.findByCondition(condition);
        List<Integer> submittedMids = new ArrayList<>();
        Set<Integer> hSet = new HashSet<>();
        for (MaterialAcceptRecord item : materials) {
            if (!hSet.contains(item.getMaterialId())) {
                submittedMids.add(item.getMaterialId());
                hSet.add(item.getMaterialId());
            }
        }
        result.put("cIds", submittedMids);
        result.put("html", html);
        return ResponseUtil.success(result);
    }

    @RequestMapping(value = "/officeOption")
    @ResponseBody
    public List<Option> options(Office office) {
        List<Option> options = new ArrayList<>();
        List<Office> list = iOfficeService.selectByFilter(office);
        for (Office item : list) {
            options.add(new Option(item.getName(), item.getId()));
        }
        return options;
    }

    @PostMapping("/socialInfo")
    public Result socialInfo(@RequestParam(value = "personId", required = false) Integer personId)
            throws DocumentException, SOAPException, IOException {
        IdentityInfo identityInfo = iIdentityInfoService.findById(personId);
        Integer lessThan35 = 1;
        if (identityInfo.getAge() > 35) {
            lessThan35 = 0;
        }
        String req = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" \n"
                + "xmlns:ser=\"http://service.webinterface.yzym.si.sl.neusoft.com/\">\n" + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n" + "      <ser:RsResidentJFRDBusinessRev>\n" + "         <!--ticket:-->\n"
                + "         <ser:arg0>NEUSERVICE_GGFW_TICKET_12</ser:arg0>\n" + "         <!--buzzNumb:-->\n"
                + "         <ser:arg1>TJZSYL_JFRDXT_002</ser:arg1>\n" + "         <!--sender:-->\n"
                + "         <ser:arg2>JFRDXT</ser:arg2>\n" + "         <!--reciver:-->\n"
                + "         <ser:arg3>TJZSYL</ser:arg3>\n" + "         <!--operatorName:-->\n"
                + "         <ser:arg4>网上预审操作员</ser:arg4>\n" + "         <!--content:-->\n"
                + "         <ser:arg5><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<ROOT><QUERY_PRAMS><idNumber>" + identityInfo.getIdNumber() + "</idNumber>"
                + "<partnerIdNnumber></partnerIdNnumber>" + "<lessThan35>" + lessThan35 + "</lessThan35>"
                + "<canAdd>1</canAdd>" + "<busType>2</busType>" + "</QUERY_PRAMS></ROOT>]]></ser:arg5>\n"
                + "</ser:RsResidentJFRDBusinessRev></soapenv:Body></soapenv:Envelope>";
        String result = WebServiceClient.actionString(req);
        Map r = new HashMap();
        if (result.contains("没有查询到人员信息")) {
            r.put("list", "没有查询到人员信息");
        } else {
            result = result.substring(
                    (result.indexOf("<return xmlns=\"http://service.webinterface.yzym.si.sl.neusoft.com/\">")
                            + "<return xmlns=\"http://service.webinterface.yzym.si.sl.neusoft.com/\">".length()),
                    result.indexOf("</return>")).replaceAll("&lt;ROOT&gt;", "<div>").replaceAll(" &lt;appCode&gt;", "")
                    .replaceAll("&lt;/appCode&gt;", "").replaceAll(" &lt;errMsg&gt;", "")
                    .replaceAll("&lt;/errMsg&gt;", "").replaceAll("&lt;birthIns&gt;", "")
                    .replaceAll("&lt;/birthIns&gt;", "").replaceAll("&lt;flag&gt;", "").replaceAll("&lt;/flag&gt;", "")
                    .replaceAll("&lt;injuryIns&gt;", "").replaceAll("&lt;/injuryIns&gt;", "")
                    .replaceAll("&lt;medicalIns&gt;", "").replaceAll("&lt;/medicalIns&gt;", "")
                    .replaceAll("&lt;pesionIns&gt;", "").replaceAll("&lt;/pesionIns&gt;", "")
                    .replaceAll("&lt;unemploymentIns&gt;", "").replaceAll("&lt;/unemploymentIns&gt;", "")
                    .replaceAll("&lt;unitNumber&gt;", "").replaceAll("&lt;/unitNumber&gt;", "")
                    .replaceAll("&lt;unitName&gt;", "单位名称").replaceAll("&lt;/unitName&gt;", "")
                    .replaceAll("&lt;unitCode&gt;", "单位编号：").replaceAll("&lt;/unitCode&gt;", "")
                    .replaceAll("&lt;payBase&gt;", "缴纳基数：").replaceAll("&lt;/payBase&gt;", "")
                    .replaceAll("&lt;paymentYear&gt;", "缴纳月份：").replaceAll("&lt;/paymentYear&gt;", "")
                    .replaceAll("&lt;/ROOT&gt;", "</div>").replaceAll("&lt;personNumber&gt;", "身份证号：")
                    .replaceAll("&lt;/personNumber&gt;", "").replaceAll("&lt;/personNumber&gt;", "");
            r.put("list", result);
        }
        req = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" \n"
                + "xmlns:ser=\"http://service.webinterface.yzym.si.sl.neusoft.com/\">\n" + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n" + "      <ser:RsResidentJFRDBusinessRev>\n" + "         <!--ticket:-->\n"
                + "         <ser:arg0>NEUSERVICE_GGFW_TICKET_12</ser:arg0>\n" + "         <!--buzzNumb:-->\n"
                + "         <ser:arg1>TJZSYL_JFRDXT_001</ser:arg1>\n" + "         <!--sender:-->\n"
                + "         <ser:arg2>JFRDXT</ser:arg2>\n" + "         <!--reciver:-->\n"
                + "         <ser:arg3>TJZSYL</ser:arg3>\n" + "         <!--operatorName:-->\n"
                + "         <ser:arg4>经办人校验测试操作员</ser:arg4>\n" + "         <!--content:-->\n"
                + "         <ser:arg5><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<ROOT><QUERY_PRAMS><idNumber>" + identityInfo.getIdNumber() + "</idNumber>"
                + "<partnerIdNnumber></partnerIdNnumber>" + "<lessThan35>" + lessThan35 + "</lessThan35>"
                + "<canAdd>1</canAdd>" + "<busType>1</busType>" + "</QUERY_PRAMS></ROOT>]]></ser:arg5>\n"
                + "</ser:RsResidentJFRDBusinessRev></soapenv:Body></soapenv:Envelope>";
        SOAP3Response soapResponse = WebServiceClient.action(req);
        r.put("info", soapResponse);
        return ResponseUtil.success(r);
    }

    /*
    2018年10月12日，xgr
    修改需求：审核中心-人社预审-待审核，搜索面板中加入“锁定人”搜索项，对应待审核列表中的“锁定人”列。
     */
    @RequestMapping("/options")
    @ResponseBody
    public List<Option> options() {
        List<Option> options = new ArrayList<>();
        IdentityInfo identityInfo = new IdentityInfo();
        //        List<IdentityInfo> identityInfos = iIdentityInfoService.selectByFilter2(identityInfo);
        List<IdentityInfo> identityInfos = iIdentityInfoService.findAll();
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (IdentityInfo identityInfo2 : identityInfos) {
            map.put(identityInfo2.getLockUser2(), identityInfo2.getId());
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            options.add(new Option(entry.getKey(), entry.getKey()));
        }
        return options;
    }

    public static boolean isIDNumber(String IDNumber) {
        if (IDNumber == null || "".equals(IDNumber)) {
            return false;
        }
        // 定义判别用户身份证号的正则表达式（15位或者18位，最后一位可以为字母）
        String regularExpression = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
                "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
        //假设18位身份证号码:41000119910101123X  410001 19910101 123X
        //^开头
        //[1-9] 第一位1-9中的一个      4
        //\\d{5} 五位数字           10001（前六位省市县地区）
        //(18|19|20)                19（现阶段可能取值范围18xx-20xx年）
        //\\d{2}                    91（年份）
        //((0[1-9])|(10|11|12))     01（月份）
        //(([0-2][1-9])|10|20|30|31)01（日期）
        //\\d{3} 三位数字            123（第十七位奇数代表男，偶数代表女）
        //[0-9Xx] 0123456789Xx其中的一个 X（第十八位为校验值）
        //$结尾

        //假设15位身份证号码:410001910101123  410001 910101 123
        //^开头
        //[1-9] 第一位1-9中的一个      4
        //\\d{5} 五位数字           10001（前六位省市县地区）
        //\\d{2}                    91（年份）
        //((0[1-9])|(10|11|12))     01（月份）
        //(([0-2][1-9])|10|20|30|31)01（日期）
        //\\d{3} 三位数字            123（第十五位奇数代表男，偶数代表女），15位身份证不含X
        //$结尾


        boolean matches = IDNumber.matches(regularExpression);

        //判断第18位校验值
        if (matches) {

            if (IDNumber.length() == 18) {
                try {
                    char[] charArray = IDNumber.toCharArray();
                    //前十七位加权因子
                    int[] idCardWi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                    //这是除以11后，可能产生的11位余数对应的验证码
                    String[] idCardY = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
                    int sum = 0;
                    for (int i = 0; i < idCardWi.length; i++) {
                        int current = Integer.parseInt(String.valueOf(charArray[i]));
                        int count = current * idCardWi[i];
                        sum += count;
                    }
                    char idCardLast = charArray[17];
                    int idCardMod = sum % 11;
                    if (idCardY[idCardMod].toUpperCase().equals(String.valueOf(idCardLast).toUpperCase())) {
                        return true;
                    } else {
                        //System.out.println(IDNumber);
//                        System.out.println("身份证最后一位:" + String.valueOf(idCardLast).toUpperCase() +
//                                "错误,正确的应该是:" + idCardY[idCardMod].toUpperCase());
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("异常:" + IDNumber);
                    return false;
                }
            }

        }
        return matches;
    }

    /*
    全程监控——积分批次管理，列表中操作列加入“人数统计”按钮
     */
    @RequestMapping("/applicationCount")
    @ResponseBody
    public Result applicationCount(AcceptDateConf acceptDateConf) throws FileNotFoundException {

        /*
        2020年7月3日，解决随迁子女的身份证号校验的问题
         */
        /*Condition condition2 = new Condition(HouseRelationship.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria2 = condition2.createCriteria();
        criteria2.andGreaterThan("identityInfoId", 33982);
        criteria2.andNotEqualTo("relationship","配偶");
        criteria2.andEqualTo("isRemove",1);
        criteria2.andIsNotNull("idNumber");
        List<HouseRelationship> list = iHouseRelationshipService.findByCondition(condition2);
        for(HouseRelationship houseRelationship : list){
            if(!isIDNumber(houseRelationship.getIdNumber())){
                System.out.println("有问题的身份证号：'"+houseRelationship.getIdNumber()+"',");
            }
        }*/


        Condition condition = new Condition(IdentityInfo.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        if (acceptDateConf.getBatchId() != null) {
            criteria.andEqualTo("batchId", acceptDateConf.getBatchId());
        }
        List<IdentityInfo> identityInfoList = iIdentityInfoService.findByCondition(condition);
        String registerSum = identityInfoList.size() + "";//已在系统注册的人数
        int passSeltTest = 0;//已经通过自助测评的人数
        int applyingInterPre = 0;//已申请网上预审的人数
        int applyedInterPre = 0;//已通过网上预审的人数
        int reservationSum = 0;//已预约的人数
        int acceptedCheck = 0;//人社受理审核通过的人数
        int acceptedCheckRensheGongan = 0;//人社受理审核通过 且 公安前置审核通过 的人数
        int fieldPassGonganAndRenshe = 0;// 2020年10月10日 未通过公安、人社任何一个部门审核的全部人数
        int renshePassing = 0; // 2020年10月10日 人社受理审核待审核

        //市区
        int passSeltTest_1 = 0;//已经通过自助测评的人数
        int applyingInterPre_1 = 0;//已申请网上预审的人数
        int applyedInterPre_1 = 0;//已通过网上预审的人数
        int reservationSum_1 = 0;//已预约的人数
        int acceptedCheck_1 = 0;//人社受理审核通过的人数
        int acceptedCheckRensheGongan_1 = 0;//人社受理审核通过 且 公安前置审核通过 的人数
        int fieldPassGonganAndRenshe_1 = 0;// 2020年10月10日 未通过公安、人社任何一个部门审核的全部人数
        int renshePassing_1 = 0; // 2020年10月10日 人社受理审核待审核

        //滨海新区
        int passSeltTest_2 = 0;//已经通过自助测评的人数
        int applyingInterPre_2 = 0;//已申请网上预审的人数
        int applyedInterPre_2 = 0;//已通过网上预审的人数
        int reservationSum_2 = 0;//已预约的人数
        int acceptedCheck_2 = 0;//人社受理审核通过的人数
        int acceptedCheckRensheGongan_2 = 0;//人社受理审核通过 且 公安前置审核通过 的人数
        int fieldPassGonganAndRenshe_2 = 0;// 2020年10月10日 未通过公安、人社任何一个部门审核的全部人数
        int renshePassing_2 = 0; // 2020年10月10日 人社受理审核待审核

        for (IdentityInfo ideInfo : identityInfoList) {
            if (ideInfo.getReservationStatus() >= 6) {
                passSeltTest++;
            }
            if (ideInfo.getReservationStatus() >= 8) {
                applyingInterPre++;
            }
            if (ideInfo.getUnionApproveStatus2() == 2 && ideInfo.getUnionApproveStatus1() == 2) {
                applyedInterPre++;
            }
            if (ideInfo.getReservationStatus() == 11 && ideInfo.getReservationDate() != null) {
                reservationSum++;
            }
            if (ideInfo.getReservationStatus() == 11 && ideInfo.getRensheAcceptStatus() == 3) {
                acceptedCheck++;
            }
            if(ideInfo.getRensheAcceptStatus()==3 && ideInfo.getPoliceApproveStatus()==3){
                acceptedCheckRensheGongan++;
            }
            if(ideInfo.getRensheAcceptStatus()==4 || ideInfo.getPoliceApproveStatus()==4){
                fieldPassGonganAndRenshe++;
            }
            if(ideInfo.getRensheAcceptStatus()==1){
                renshePassing++;
            }

            //市区
            if (ideInfo.getReservationStatus() >= 8 && ideInfo.getAcceptAddressId() == 1) {
                applyingInterPre_1++;
            }
            if (ideInfo.getUnionApproveStatus2() == 2 && ideInfo.getUnionApproveStatus1() == 2 && ideInfo.getAcceptAddressId() == 1) {
                applyedInterPre_1++;
            }
            if (ideInfo.getReservationStatus() == 11 && ideInfo.getReservationDate() != null && ideInfo.getAcceptAddressId() == 1) {
                reservationSum_1++;
            }
            if (ideInfo.getReservationStatus() == 11 && ideInfo.getRensheAcceptStatus() == 3 && ideInfo.getAcceptAddressId() == 1) {
                acceptedCheck_1++;
            }
            if(ideInfo.getRensheAcceptStatus()==3 && ideInfo.getPoliceApproveStatus()==3 && ideInfo.getAcceptAddressId() == 1) {
                acceptedCheckRensheGongan_1++;
            }
            if((ideInfo.getRensheAcceptStatus()==4 || ideInfo.getPoliceApproveStatus()==4) && ideInfo.getAcceptAddressId() == 1) {
                fieldPassGonganAndRenshe_1++;
            }
            if(ideInfo.getRensheAcceptStatus()==1 && ideInfo.getAcceptAddressId() == 1) {
                renshePassing_1++;
            }

            //滨海新区
            if (ideInfo.getReservationStatus() >= 8 && ideInfo.getAcceptAddressId() == 2) {
                applyingInterPre_2++;
            }
            if (ideInfo.getUnionApproveStatus2() == 2 && ideInfo.getUnionApproveStatus1() == 2 && ideInfo.getAcceptAddressId() == 2) {
                applyedInterPre_2++;
            }
            if (ideInfo.getReservationStatus() == 11 && ideInfo.getReservationDate() != null && ideInfo.getAcceptAddressId() == 2) {
                reservationSum_2++;
            }
            if (ideInfo.getReservationStatus() == 11 && ideInfo.getRensheAcceptStatus() == 3 && ideInfo.getAcceptAddressId() == 2) {
                acceptedCheck_2++;
            }
            if(ideInfo.getRensheAcceptStatus()==3 && ideInfo.getPoliceApproveStatus()==3 && ideInfo.getAcceptAddressId() == 2) {
                acceptedCheckRensheGongan_2++;
            }
            if((ideInfo.getRensheAcceptStatus()==4 || ideInfo.getPoliceApproveStatus()==4) && ideInfo.getAcceptAddressId() == 2) {
                fieldPassGonganAndRenshe_2++;
            }
            if(ideInfo.getRensheAcceptStatus()==1 && ideInfo.getAcceptAddressId() == 2) {
                renshePassing_2++;
            }
        }

        Map params = new HashMap();
        params.put("registerSum", registerSum);
        params.put("passSeltTest", passSeltTest + "");
        params.put("applyingInterPre", applyingInterPre + "");
        params.put("applyedInterPre", applyedInterPre + "");
        params.put("reservationSum", reservationSum + "");
        params.put("acceptedCheck", acceptedCheck + "");
        params.put("acceptedCheckRensheGongan", acceptedCheckRensheGongan + "");
        params.put("fieldPassGonganAndRenshe", fieldPassGonganAndRenshe + "");
        params.put("renshePassing", renshePassing + "");

        params.put("applyingInterPre_1", applyingInterPre_1 + "");
        params.put("applyedInterPre_1", applyedInterPre_1 + "");
        params.put("reservationSum_1", reservationSum_1 + "");
        params.put("acceptedCheck_1", acceptedCheck_1 + "");
        params.put("acceptedCheckRensheGongan_1", acceptedCheckRensheGongan_1 + "");
        params.put("fieldPassGonganAndRenshe_1", fieldPassGonganAndRenshe_1 + "");
        params.put("renshePassing_1", renshePassing_1 + "");

        params.put("applyingInterPre_2", applyingInterPre_2 + "");
        params.put("applyedInterPre_2", applyedInterPre_2 + "");
        params.put("reservationSum_2", reservationSum_2 + "");
        params.put("acceptedCheck_2", acceptedCheck_2 + "");
        params.put("acceptedCheckRensheGongan_2", acceptedCheckRensheGongan_2 + "");
        params.put("fieldPassGonganAndRenshe_2", fieldPassGonganAndRenshe_2 + "");
        params.put("renshePassing_2", renshePassing_2 + "");

        String templatePath = ResourceUtils.getFile("classpath:templates/").getPath();
        String html = FreeMarkerUtil.getHtmlStringFromTemplate(templatePath, "application_count.ftl", params);

        Map result = new HashMap();
        result.put("html", html);
        return ResponseUtil.success(result);
    }


    /**
     * 2018年12月26日
     * 给公安局提供发放准迁证的数据，xml格式的数据
     */
    @RequestMapping("/provideDataToPolice")
    @ResponseBody
    public Result provideDataToPolice(AcceptDateConf acceptDateConf, HttpServletResponse response) {
        /*
        1、根据人社局提供的分数线与划定的人数限制，获得有资格的人的ID；
        2、生成xml格式的文档；
         */
        Condition condition = new Condition(BatchConf.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("id", acceptDateConf.getBatchId());
        List<BatchConf> list = iBatchConfService.findByCondition(condition);
        List<ScoreRecord> scoreRecords = iScoreRecordService.provideDataToPolice(list.get(0));
        Integer sum = scoreRecords.size();

        Element PACKAGE = createRootXml("PACKAGE");

        /*
        消息头 packageHead
         */
        Element PACKAGEHEAD = PACKAGE.addElement("PACKAGEHEAD");
        Element JLS = PACKAGEHEAD.addElement("JLS");//记录数，表示封装了多少人
        addText(JLS, sum.toString());

        Element FSSL = PACKAGEHEAD.addElement("FSSL");//发送时间，当前的时间
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date2 = sdf.format(date);
        FSSL.addText(date2);

        /*
        数据包编号，如：2205000000002018062601606
        构成方式为：
        1、220500000000：是固定的；
        2、20180626：年月日；
        3、01606：流水号，可以自定义；目前暂定为00001；
         */
        Element SJBBH = PACKAGEHEAD.addElement("SJBBH");
        String bh = "220500000000" + date2 + "00001";
        SJBBH.addText(bh);

        /*
        date 节点封装申请人的数据
         */
        Element DATA = PACKAGE.addElement("DATA");
        for (int i = 0; i < scoreRecords.size(); i++) {
            System.out.println("到第几个了："+i+" 姓名与身份证号："+scoreRecords.get(i).getPersonName()+"-"+scoreRecords.get(i).getPersonIdNum());
            /*
            1、获取申请人的信息
            a、申请人的信息；
            b、随迁人的信息；
            c、迁出、迁入地的信息
             */
            Condition condition_IdentityInfo = new Condition(IdentityInfo.class);
            tk.mybatis.mapper.entity.Example.Criteria criteria_IdentityInfo = condition_IdentityInfo.createCriteria();
            criteria_IdentityInfo.andEqualTo("id", scoreRecords.get(i).getId());
            List<IdentityInfo> list_IdeInfo = iIdentityInfoService.findByCondition(condition_IdentityInfo);

            condition = new Condition(HouseRelationship.class);
            criteria = condition.createCriteria();
            criteria.andEqualTo("identityInfoId", scoreRecords.get(i).getId());
            criteria.andEqualTo("isRemove", 1);//是否随迁 1：是，2：否
            List<String> list_rs = new ArrayList();
            list_rs.add("子");
            list_rs.add("女");
            criteria.andIn("relationship", list_rs);//与申请人关系
            List<HouseRelationship> list_relationship = iHouseRelationshipService.findByCondition(condition);


            Condition cond_move = new Condition(HouseMove.class);
            tk.mybatis.mapper.entity.Example.Criteria criteria_move = cond_move.createCriteria();
            criteria_move.andEqualTo("identityInfoId", scoreRecords.get(i).getId());
            List<HouseMove> list_move = iHouseMoveService.findByCondition(cond_move);

            /*
            通过 HouseMove的 moveRegion 属性值获取表 字典表的t_region的 police_code 值
             */
            Condition cond_region = new Condition(Region.class);
            tk.mybatis.mapper.entity.Example.Criteria criteria_region = cond_region.createCriteria();
            criteria_region.andEqualTo("id", list_move.get(0).getMoveRegion());
            List<Region> list_region = iRegionService.findByCondition(cond_region);
            if (list_region.size() == 0) {
                System.out.println("迁出地 省市县（区）：" + list_move.get(0).getMoveRegion() + ":" + list_move.get(0).getIdentityInfoId());
            }

            /*
            2019年1月2日
            核对好公安局派出所单位名称、代码后，进行赋值
             */
            Condition cond_office = new Condition(Office.class);
            tk.mybatis.mapper.entity.Example.Criteria criteria_office = cond_office.createCriteria();
            criteria_office.andIsNotNull("policeCode");
            //            criteria_office.andEqualTo("id",505);
            List<Office> list_office = iOfficeService.findByCondition(cond_office);

            Element RECORD = DATA.addElement("RECORD");
            //添加两个属性：SID、NO
            RECORD.addAttribute("SID", "010122");//SID属性值是固定的010122
            int j = i + 1;
            RECORD.addAttribute("NO", new Integer(j).toString());//表示xml文档中的第几个申请人

            //添加节点值
            Element YWLSH = RECORD.addElement("YWLSH");//业务流水号
            YWLSH.addText(list_IdeInfo.get(0).getAcceptNumber());
            Element SQR_GMSFHM = RECORD.addElement("SQR_GMSFHM");//申请人-公民身份号码
            SQR_GMSFHM.addText(list_IdeInfo.get(0).getIdNumber().replace(" ", ""));
            Element SQR_XM = RECORD.addElement("SQR_XM");//申请人姓名
            SQR_XM.addText(list_IdeInfo.get(0).getName());
            Element SQR_ZZ_SSXQDM = RECORD.addElement("SQR_ZZ_SSXQDM");//住址 省市县（区），与迁出地省市县（区）一致
            SQR_ZZ_SSXQDM.addText((list_region.get(0).getPolice_code()==null)? "":list_region.get(0).getPolice_code().toString());
            Element SQR_ZZ_QHNXXDZ = RECORD.addElement("SQR_ZZ_QHNXXDZ");//住址 区划内详细地址
            SQR_ZZ_QHNXXDZ.addText(list_move.get(0).getCurrentRegisteredAddress()); // 2020年12月29日 修改为现户口登记地址
            Element SQR_HKDJJG_GAJGJGDM = RECORD.addElement("SQR_HKDJJG_GAJGJGDM");//户口登记机关 公安机关机构代码
            SQR_HKDJJG_GAJGJGDM.addText("");
            Element SQR_HKDJJG_GAJGMC = RECORD.addElement("SQR_HKDJJG_GAJGMC");//户口登记机关 公安机关名称
            SQR_HKDJJG_GAJGMC.addText(list_move.get(0).getMoveRegisteredOffice());
            Element QCD_SSXQDM = RECORD.addElement("QCD_SSXQDM");//迁出地 省市县（区）
            QCD_SSXQDM.addText((list_region.get(0).getPolice_code()==null)?"":list_region.get(0).getPolice_code().toString());
            Element QCD_QHNXXDZ = RECORD.addElement("QCD_QHNXXDZ");//迁出地 区划内详细地址
            QCD_QHNXXDZ.addText(list_move.get(0).getCurrentRegisteredAddress());
            Element QCD_HKDJJG_GAJGJGDM = RECORD.addElement("QCD_HKDJJG_GAJGJGDM");//迁出地 户口登记机关 公安机关机构代码
            QCD_HKDJJG_GAJGJGDM.addText("");
            Element QCD_HKDJJG_GAJGMC = RECORD.addElement("QCD_HKDJJG_GAJGMC");//迁出地 户口登记机关 公安机关名称
            QCD_HKDJJG_GAJGMC.addText(list_move.get(0).getMoveRegisteredOffice());
            Element QRD_SSXQDM = RECORD.addElement("QRD_SSXQDM");//迁入地 省市县（区）
            QRD_SSXQDM.addText(getCode(list_move.get(0).getRegion().toString()));
            Element QRD_QHNXXDZ = RECORD.addElement("QRD_QHNXXDZ");// 迁入地 区划内详细地址
            QRD_QHNXXDZ.addText(list_move.get(0).getAddress());
            Element QRD_HKDJJG_GAJGJGDM = RECORD.addElement("QRD_HKDJJG_GAJGJGDM");//迁入地 户口登记机关 公安机关机构代码
            Element QRD_HKDJJG_GAJGMC = RECORD.addElement("QRD_HKDJJG_GAJGMC");//迁入地 户口登记机关 公安机构名称
            for (Office of : list_office) {
                if (of.getId().toString().equals(list_move.get(0).getRegisteredRegion())) {
                    QRD_HKDJJG_GAJGJGDM.addText(of.getPoliceCode());
                    QRD_HKDJJG_GAJGMC.addText(of.getName());
                    break;
                }
            }

            Element CBR_XM = RECORD.addElement("CBR_XM");//承办人姓名
            CBR_XM.addText(list_IdeInfo.get(0).getName());
            Element BZ = RECORD.addElement("BZ");//备注
            BZ.addText("");
            Element QYLDYYDM = RECORD.addElement("QYLDYYDM"); // 迁移（流动）原因，固定值 950
            QYLDYYDM.addText("950");
            Element YXQJZRQ = RECORD.addElement("YXQJZRQ");//有效期截止日期
            YXQJZRQ.addText("");
            Element SLSJ = RECORD.addElement("SLSJ");//受理时间，用创建时间代替的
            SLSJ.addText(sdf.format(list_IdeInfo.get(0).getcTime()));

            /*
            迁移人节点内有两个属性
             */
            int index = 1;
            Element QYR = RECORD.addElement("QYR");
            QYR.addAttribute("NO", new Integer(index).toString());// 顺序号
            QYR.addAttribute("SID", "010123");// 顺序号
            Element YSQRGX_JTGXDM = QYR.addElement("YSQRGX_JTGXDM");//与申请人关系_家庭关系，01：本人，20：子，30：女
            YSQRGX_JTGXDM.addText("01.");
            Element GMSFHM = QYR.addElement("GMSFHM");//公民身份号码
            GMSFHM.addText(list_IdeInfo.get(0).getIdNumber().replace(" ", ""));
            Element XM = QYR.addElement("XM");//姓名
            XM.addText(list_IdeInfo.get(0).getName());
            Element XBDM = QYR.addElement("XBDM");//性别
            String id_number = list_IdeInfo.get(0).getIdNumber();
            if (list_IdeInfo.get(0).getSex()==1) {
                XBDM.addText("1");
            } else {
                XBDM.addText("2");
            }
            Element CSRQ = QYR.addElement("CSRQ");//出生日期
            CSRQ.addText(list_IdeInfo.get(0).getIdNumber().replace(" ", "").substring(6, 14));
            Element YHLX = QYR.addElement("YHLX");//原户类型，10 家庭户，20集体户
            if (list_move.get(0).getHouseNature() !=null && (list_move.get(0).getHouseNature() == 4 || list_move.get(0).getHouseNature() == 5)) {
                YHLX.addText("20");
            } else {
                YHLX.addText("10");
            }
            Element HLX = QYR.addElement("HLX");//户类型
            if (list_move.get(0).getSettledNature() == 1 || list_move.get(0).getSettledNature() == 2) {
                HLX.addText("20");
            } else {
                HLX.addText("10");
            }
            Element LHYXRQ = QYR.addElement("LHYXRQ");//落户有效期限
            LHYXRQ.addText("");
            if (list_relationship.size() > 0) {
                for (HouseRelationship h : list_relationship) {
                    QYR = RECORD.addElement("QYR");
                    QYR.addAttribute("NO", new Integer(++index).toString());// 顺序号
                    QYR.addAttribute("SID", "010123");// 顺序号
                    YSQRGX_JTGXDM = QYR.addElement("YSQRGX_JTGXDM");//与申请人关系_家庭关系
                    if (h.getRelationship().equals("子")) {
                        YSQRGX_JTGXDM.addText("20.");
                    } else {
                        YSQRGX_JTGXDM.addText("30.");
                    }
                    GMSFHM = QYR.addElement("GMSFHM");//公民身份号码
                    GMSFHM.addText(h.getIdNumber().replace(" ", ""));
                    XM = QYR.addElement("XM");//姓名
                    XM.addText(h.getName()==null ? "":h.getName());
                    XBDM = QYR.addElement("XBDM");//性别
                    if (h.getRelationship().equals("子")) {
                        XBDM.addText("1");
                    } else {
                        XBDM.addText("2");
                    }
                    CSRQ = QYR.addElement("CSRQ");//出生日期
                    //System.out.println(h.getName() + ":" + h.getIdNumber());
                    CSRQ.addText(h.getIdNumber()==null ||  h.getIdNumber().length()<18? "" : h.getIdNumber().replace(" ", "").substring(6, 14));
                    YHLX = QYR.addElement("YHLX");//原户类型
                    if (list_move.get(0).getHouseNature() == 4 || list_move.get(0).getHouseNature() == 5) {
                        YHLX.addText("20");
                    } else {
                        YHLX.addText("10");
                    }
                    HLX = QYR.addElement("HLX");//户类型
                    if (list_move.get(0).getSettledNature() == 1 || list_move.get(0).getSettledNature() == 2) {
                        HLX.addText("20");
                    } else {
                        HLX.addText("10");
                    }
                    LHYXRQ = QYR.addElement("LHYXRQ");//落户有效期限
                    LHYXRQ.addText("");
                }
            }

        }

        /*
        xml数据生成String 并写入文档
         */
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + PACKAGE.asXML();
        /*FileWriter fw = null;
        File f = new File("E:\\"+date2+(new Date().getTime())+"police.xml");
        try {
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(xmlString);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/

        //1.设置文件下载的response响应格式
        String fileName = date2 + (new Date().getTime()) + "toPolice";  //文件名
        String fileType = "xml";    //文件类型
        //        HttpServletResponse response = ServletActionContext.getResponse();
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + "." + fileType);
        response.setContentType("multipart/form-data");
        Cookie cookie = new Cookie("fileDownload", "true");
        cookie.setPath("/");
        response.addCookie(cookie);
        try {
            //3.将内容转为byte[]格式
            byte[] data = xmlString.getBytes("UTF-8");

            //4.将内容写入响应流

            OutputStream out = response.getOutputStream();
            out.write(data);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Map result = new HashMap();
        result.put("ok", "ok");
        return ResponseUtil.success(result);

    }

    /**
     * 2018年1月14日
     * 提供申请人收件地址的数据，excel格式的数据
     */

    private static String[] headers = new String[]{"身份证号", "姓名", "总分", "性别", "落户地区", "公安编号", "迁入地户口登记机关", "拟落户地区名称", "迁入地详细地址", "收件人", "收件人电话", "收件地址", "经办人1姓名", "经办人1联系电话",  "本人电话", "单位电话"};
    private static String[] mapHeaders = new String[]{"PERSON_ID_NUM", "PERSON_NAME", "SCORE_VALUE", "SEX", "ACCEPT_ADDRESS", "LUOHU_NUMBER", "REGISTRATION", "AREANAME", "ADDRESS", "WITNESS", "WITNESS_PHONE", "WITNESS_ADDRESS", "OPERATOR", "OPERATOR_MOBILE",   "SELF_PHONE", "COMPANY_PHONE"};

    @RequestMapping("/identityInfoRecipient")
    @ResponseBody
    public Result identityInfoRecipient(AcceptDateConf acceptDateConf, HttpServletResponse response, HttpServletRequest request) {
        String message = "";
        try {
            Condition condition = new Condition(BatchConf.class);
            tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
            criteria.andEqualTo("id", acceptDateConf.getBatchId());
            List<BatchConf> list = iBatchConfService.findByCondition(condition);
            Map argMap = new HashMap();
            if (list.size() > 0) {
                argMap.put("batchId", list.get(0).getId());
                Integer scoreValue = list.get(0).getScoreValue();
                if (scoreValue != null) {
                    argMap.put("scoreValue", scoreValue);
                } else {
                    //不存在打分标准时抛出异常
                    message = "不存在打分标准";
                    throw new NullPointerException();
                }
            } else {
                //不存在batchid时抛出异常
                message = "该ID不存在";
                throw new NullPointerException();
            }

            List<Map> allList = iIdentityInfoService.selectIdentityInfoRecipientList(argMap);
            String savePath = request.getSession().getServletContext().getRealPath("/") + uploadPath + "/" + System.currentTimeMillis() + ".xlsx";
            ExcelFileUtil.exportXlsx(savePath, allList, headers, mapHeaders);
            ExcelFileUtil.download(response, savePath, "有落户资格的申请人名单与收件地址.xlsx");
            return ResponseUtil.success(message);
        } catch (Exception e) {
            e.printStackTrace();
            if ("".equals(message)) {
                message = "下载失败";
            }
            return ResponseUtil.error(message);
        }
    }

    private static String[] headers2 = new String[]{"身份证号", "ID", "姓名","得分", "性别", "年龄", "拟落户地区","预约大厅状态","资格取消状态"};
    private static String[] mapHeaders2 = new String[]{"PERSON_ID_NUM", "ID", "PERSON_NAME","SCORE_VALUE", "SEX", "年龄", "拟落户地区", "预约大厅状态","资格取消状态"};

    @RequestMapping("/provideTotalScore")
    @ResponseBody
    public Result provideTotalScore(AcceptDateConf acceptDateConf, HttpServletResponse response, HttpServletRequest request) {
        String message = "";
        try {
            Condition condition = new Condition(BatchConf.class);
            tk.mybatis.mapper.entity.Example.Criteria criteria = condition.createCriteria();
            criteria.andEqualTo("id", acceptDateConf.getBatchId());
            List<BatchConf> list = iBatchConfService.findByCondition(condition);
            Map argMap = new HashMap();
            if (list.size() > 0) {
                argMap.put("batchId", list.get(0).getId());
                Integer scoreValue = list.get(0).getScoreValue();
                if (scoreValue != null) {
                    argMap.put("scoreValue", scoreValue);
                } else {
                    //不存在打分标准时抛出异常
                    message = "不存在打分标准，请设置";
                    throw new NullPointerException();
                }
            } else {
                //不存在batchid时抛出异常
                message = "该ID不存在";
                throw new NullPointerException();
            }

            List<Map> allList = iIdentityInfoService.selectIdentityInfoRecipientList2(argMap);
            String savePath = request.getSession().getServletContext().getRealPath("/") + uploadPath + "/" + System.currentTimeMillis() + ".xlsx";
            ExcelFileUtil.exportXlsx(savePath, allList, headers2, mapHeaders2);
            ExcelFileUtil.download(response, savePath, "申请人总分数排名.xlsx");
            return ResponseUtil.success(message);
        } catch (Exception e) {
            e.printStackTrace();
            if ("".equals(message)) {
                message = "下载失败";
            }
            return ResponseUtil.error(message);
        }
    }


    /**
     * 创建xml根节点
     *
     * @param rootName：根节点名称
     * @return
     */
    private static Element createRootXml(String rootName) {
        org.dom4j.Document document = DocumentHelper.createDocument();
        //2、添加根节点
        Element root = document.addElement(rootName);
        return root;
    }

    /**
     * 主要是往元素里面加值，不是设置参数,就是root加完元素后网里面加值。
     * 这里，ele.setText(String value),所以里面的值都是字符串
     */
    public static void addText(Element ele, String value) {
        if (!StringUtils.isEmpty(value)) {
            ele.setText(value);
        } else {
            ele.setText("");
        }
    }


    public static String getCode(String code) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("21", "120101");//和平区
        map.put("22", "120102");//河东区
        map.put("23", "120103");//河西区
        map.put("24", "120104");//南开区
        map.put("25", "120105");//河北区
        map.put("26", "120106");//红桥区
        map.put("27", "120110");//东丽区
        map.put("28", "120111");//西青区
        map.put("29", "120112");//津南区
        map.put("30", "120113");//北辰区
        map.put("31", "120114");//武清区
        map.put("32", "120115");//宝坻区
        map.put("33", "120116");//滨海新区
        map.put("34", "120117");//宁河区
        map.put("35", "120118");//静海区
        map.put("36", "120225");//蓟县

        String policeCode = (String) map.get(code);
        return policeCode;
    }
}
