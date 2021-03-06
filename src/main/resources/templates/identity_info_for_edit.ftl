<style>
    .table-list-item {
        padding: 5px 5px;
    }

    .table-list-item .table {
        margin-bottom: 0;
    }
</style>
<div class="widget-box transparent">
    <div class="widget-header">
        <h4 class="widget-title lighter smaller">
            申请人信息
        </h4>

        <div class="widget-toolbar no-border">
            <ul class="nav nav-tabs" id="recent-tab">
                <li class="active">
                    <a data-toggle="tab" href="#info-tab" aria-expanded="true">信息</a>
                </li>
                <li class="">
                    <a data-toggle="tab" href="#online-tab" aria-expanded="false">材料上传</a>
                </li>
            </ul>
        </div>
    </div>

    <div class="widget-body">
        <div class="widget-main">
            <div class="tab-content">
                <div id="info-tab" class="row tab-pane active">
                    <div class="col-md-12 col-sx-12">
                        <div class="panel panel-default">
                            <!-- Default panel contents -->
                            <div class="panel-heading">用人单位信息</div>
                            <!-- Table -->
                            <table style="font-size: 14px;" class="table table-hover table-condensed">
                                <!-- 两组数据信息的 -->
                                <tr>
                                    <td colspan="6">单位名称：<strong>${company.companyName}</strong></td>
                                    <td colspan="6">机构代码：<strong>${company.societyCode}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="4">联系人：<strong>${company.operator}</strong></td>
                                    <td colspan="4">身份证：<strong>${company.idCardNumber_1}</strong></td>
                                    <td colspan="4">联系电话：<strong>${company.operatorMobile}</strong></td>
                                </tr>
                                <!-- 一组数据信息的 -->
                                <tr>
                                    <td colspan="12">实际经营地址：<strong>${company.operatorAddress}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">核准日期：${company.apprdate}</td>
                                    <td colspan="6">住所：<strong>${company.dom}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">企业类型：${company.enttype}</td>
                                    <td colspan="6">成立日期：<strong>${company.estdate}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">法定代表人：${company.lerep}</td>
                                    <td colspan="6">经营期限自：<strong>${company.opfrom}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="12">经营范围：<strong>${company.opscope}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">经营期限至：${company.opto}</td>
                                    <td colspan="6">注册资本：<strong>${company.regcap}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">注册资本币种：${company.regcapcur}</td>
                                    <td colspan="6">注册号：<strong>${company.regno}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">登记机关：${company.regorg}</td>
                                    <td colspan="6">登记状态：<strong>${company.regstate}</strong></td>
                                </tr>
                            </table>
                        </div>
                        <div class="panel panel-default">
                            <div class="panel-heading">申请人及随迁子女信息</div>
                            <table style="font-size: 14px;" class="table table-hover table-condensed">
                                <tr>
                                    <td colspan="12">居住证号（身份证号）：<strong>${person.idNumber}</strong>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="6">申请人类型：<strong>${other.applicantTypeStr}</strong></td>
                                    <td colspan="6">居住证申领日期：<strong>${other.applicationDate}</strong></td>
                                </tr>
                                <!-- 三组数据信息的 -->
                                <tr>
                                    <td colspan="4">姓名：<strong>${person.name}</strong></td>
                                    <td colspan="4">性别：<strong><#if person.sex == 1>男<#else>女</#if></strong></td>
                                    <td colspan="4">民族：<strong>${person.nation}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">是否于2020年4月2日之前购买住房：<strong><#if person.ourBuyHouse == 1>是<#else>否</#if></strong></td>
                                    <td colspan="6">是否按照“津发改社会〔2018〕26号”文件计算：<strong><#if person.is201826Doc == 1>是<#else>否</#if></strong></td>
                                </tr>
                                <tr>
                                    <td colspan="12">租赁房屋地址：<strong>${move.rentHouseAddress}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="12">租赁登记备案证明编号：<strong>${move.rentIdNumber}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="12">租赁备案起始日：<strong>${move.rentHouseStartDate}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="12">租赁合同终止日：<strong>${move.rentHouseEndDate}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="12">是否缴存住房公积金：<strong><#if other.providentFund == 1>是<#else>否</#if></strong></td>
                                </tr>
                                <tr>
                                    <td colspan="12">是否在开发区缴纳外资企业住房公积金：<strong><#if other.isInsurance == 1>是<#else>否</#if></strong></td>
                                </tr>
                                <tr>
                                    <td colspan="12">2014年以前是否缴存开发区社保中心公积金：<strong><#if other.kaifaquFund == 1>是<#else>否</#if></strong></td>
                                </tr>
                                <tr>
                                    <td colspan="12">落户类别：<strong><#if move.settledNature == 1>持有不动产权证或者房屋所有权证</#if>
                                    <#if move.settledNature == 2>购买住房还未办理不动产权证，但持有购房合同和税收缴款书（此选项不允许子女随迁）</#if>
                                    <#if move.settledNature == 3>无住房，但单位已设立集体户口（此选项不允许子女随迁）</#if>
                                    <#if move.settledNature == 4>无住房，且单位也未设立集体户口，自愿落户在市、区政府下设人力资源中介机构集体户（此选项不允许子女随迁）</#if></strong>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">出生日期：<strong>${person.birthday}</strong></td>
                                    <td colspan="4">政治面貌：<strong>${other.politicalStatusStr}</strong></td>
                                    <td colspan="4">婚姻状况：<strong>${move.marriageStatusStr}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="4">文化程度：<strong>${other.cultureDegreeStr}</strong></td>
                                    <td colspan="4">学位：<strong>${other.degreeStr}</strong></td>
                                    <td colspan="4">年龄：<strong>${person.age}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="12"><strong>专业：${other.profession}</strong>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="6">职业资格：<strong>${profession.professionTypeStr}</strong></td>
                                    <td colspan="6">职业名称：<strong>${profession.jobNameStr}</td>
                                </tr>
                                <tr>
                                    <td colspan="12">迁出地省（市自治区）：
                                        <select disabled style="height: 25px;">
                                        <#list provinceList as item>
                                            <option <#if move.moveProvince==item.id>selected</#if>
                                                    value="${item.id}">${item.name}</option>
                                        </#list>
                                        </select>
                                        市：
                                        <select disabled style="height: 25px;">
                                        <#list cityList as item>
                                            <#if item.parentId==move.moveProvince>
                                                <option <#if move.moveCity==item.id>selected</#if>
                                                        value="${item.id}">${item.name}</option>
                                            </#if>
                                        </#list>
                                        </select>
                                        区县：
                                        <select disabled style="height: 25px;">
                                        <#list areaList as item>
                                            <#if item.parentId==move.moveCity>
                                                <option <#if move.moveRegion==item.id>selected</#if>
                                                        value="${item.id}">${item.name}</option>
                                            </#if>
                                        </#list>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="12">迁出详细地址：<strong>${move.currentRegisteredAddress}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">现户口性质：<select disabled style="height: 25px;">
                                        <option <#if move.houseNature==1>selected</#if> value="1">未落常住户口</option>
                                        <option <#if move.houseNature==2>selected</#if> value="2">非农业家庭户口</option>
                                        <option <#if move.houseNature==3>selected</#if> value="3">农业家庭户口</option>
                                        <option <#if move.houseNature==4>selected</#if> value="4">非农业集体户口</option>
                                        <option <#if move.houseNature==5>selected</#if> value="5">农业集体户口</option>
                                        <option <#if move.houseNature==6>selected</#if> value="6">自理口粮户口</option>
                                        <option <#if move.houseNature==7>selected</#if> value="7">寄住户口</option>
                                        <option <#if move.houseNature==8>selected</#if> value="8">暂住户口</option>
                                        <option <#if move.houseNature==9>selected</#if> value="9">地方城镇居民户口</option>
                                        <option <#if move.houseNature==10>selected</#if> value="10">其他户口</option>
                                    </select></td>
                                    <td colspan="6">落户性质：
                                        <select data-id="${move.id?c}" data-name="move.settledNature" class="edit"
                                                style="height: 25px;">
                                            <option <#if move.settledNature==1>selected</#if> value="1">持有不动产权证或者房屋所有权证</option>
                                            <option <#if move.settledNature==2>selected</#if> value="2">购买住房还未办理不动产权证，但持有购房合同和税收缴款书（此选项不允许子女随迁）</option>
                                            <option <#if move.settledNature==3>selected</#if> value="3">无住房，但单位已设立集体户口（此选项不允许子女随迁）</option>
                                            <option <#if move.settledNature==4>selected</#if> value="4">无住房，且单位也未设立集体户口，自愿落户在市、区政府下设人力资源中介机构集体户（此选项不允许子女随迁）</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="12">现户籍登记机关：<strong>${move.moveRegisteredOffice}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="12">迁入地户籍登记机关：
                                        <select disabled style="height: 25px;">
                                        <#list officeList1 as item>
                                            <option <#if move.registeredOffice==item.id?string>selected</#if>
                                                    value="${item.id}">${item.name}</option>
                                        </#list>
                                        </select>
                                        <select disabled style="height: 25px;">
                                        <#list officeList2 as item>
                                            <option <#if move.registeredRegion==item.id?string>selected</#if>
                                                    value="${item.id?c}">${item.name}</option>
                                        </#list>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="12">迁入地详细地址：<strong>${move.address}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">拟落户地区：
                                        <select disabled style="height: 25px;">
                                        <#list officeList1 as item>
                                            <option <#if move.region==21>selected</#if>
                                                    value="21">和平区
                                            </option>
                                            <option <#if move.region==22>selected</#if>
                                                    value="22">河东区
                                            </option>
                                            <option <#if move.region==23>selected</#if>
                                                    value="23">河西区
                                            </option>
                                            <option <#if move.region==24>selected</#if>
                                                    value="24">南开区
                                            </option>
                                            <option <#if move.region==25>selected</#if>
                                                    value="25">河北区
                                            </option>
                                            <option <#if move.region==26>selected</#if>
                                                    value="26">红桥区
                                            </option>
                                            <option <#if move.region==27>selected</#if>
                                                    value="27">东丽区
                                            </option>
                                            <option <#if move.region==28>selected</#if>
                                                    value="28">西青区
                                            </option>
                                            <option <#if move.region==29>selected</#if>
                                                    value="29">津南区
                                            </option>
                                            <option <#if move.region==30>selected</#if>
                                                    value="30">北辰区
                                            </option>
                                            <option <#if move.region==31>selected</#if>
                                                    value="31">武清区
                                            </option>
                                            <option <#if move.region==32>selected</#if>
                                                    value="32">宝坻区
                                            </option>
                                            <option <#if move.region==33>selected</#if>
                                                    value="33">滨海新区
                                            </option>
                                            <option <#if move.region==34>selected</#if>
                                                    value="34">宁河区
                                            </option>
                                            <option <#if move.region==35>selected</#if>
                                                    value="35">静海区
                                            </option>
                                            <option <#if move.region==36>selected</#if>
                                                    value="36">蓟州区
                                            </option>
                                        </#list>
                                        </select>
                                    </td>
                                    <td colspan="6">收件人：<strong>${move.witness}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">联系电话：<strong>${move.witnessPhone}</strong></td>
                                    <td colspan="6">邮寄地址：<strong>${move.witnessAddress}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">犯罪日期：<strong>${other.penaltyDate}</strong></td>
                                    <td colspan="6">处罚结果：<strong>${other.penaltyResults}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="12">犯罪内容：<strong>${other.penaltyContent}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="12">是否为派遣制用工：<strong>${other.dispatchStr}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">是否具有劳务派遣经营许可证：<strong>${other.isDispatchLicenseStr}</strong></td>
                                    <td colspan="6">劳务派遣经营许可证编号：<strong>${other.dispatchLicenseNum}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">劳务派遣经营许可证是否在有效期内：<strong>${other.isDispatchLicenseDateStr}</strong></td>
                                    <td colspan="6">是否签订派遣协议：<strong>${other.isSignDispatchStr}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">实际用工单位名称：<strong>${other.companyName}</strong></td>
                                    <td colspan="6">实际用工单位电话：<strong>${other.companyPhone}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">实际用工单位现经营地址：<strong>${other.companyAddress}</strong></td>
                                    <td colspan="6">本人电话：<strong>${other.selfPhone}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="12">申请人实际办公地址：<strong>${other.applyOfficeAddress}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">实际用工单位所属行业：<strong>${other.industry}</strong></td>
                                    <td colspan="6">申请人现工作岗位：<strong>${other.jobTitle}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">具体工作内容：<strong>${other.jobContent}</strong></td>
                                    <td colspan="6">在此岗位工作起始时间：<strong>${other.jobDate}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="4">是否社保缴纳：<strong>${other.socialSecurityPayStr}</strong></td>
                                    <td colspan="4">有无行政拘留记录：<strong>${other.detentionStr}</strong></td>
                                    <td colspan="4">有无获刑记录：<strong>${other.penaltyStr}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">资格证书级别：<strong>${profession.jobLevelStr}</strong></td>
                                    <td colspan="6">证书编号：<strong>${profession.certificateCode}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">发证机关：<strong>${profession.issuingAuthority}</strong></td>
                                    <td colspan="6">发证日期：<strong>${profession.issuingDate}</strong></td>
                                </tr>
                                <!-- 内表格 -->
                                <tr>
                                    <td colspan="12">
                                        <table style="font-size: 14px;" class="table table-hover table-bordered table-condensed">
                                            <tr class="info">
                                                <th>与本人关系</th>
                                                <th>姓名</th>
                                                <th>身份证号</th>
                                                <th>文化程度</th>
                                                <th>是否随迁</th>
                                                <th>配偶是否在天津就业且用人单位依法缴<br>纳社会保险累计满24个月</th>
                                                <th>配偶是否是现役军人</th>
                                            </tr>
                                        <#list relation as ritem>
                                            <tr>
                                                <td>${ritem.relationship}</td>
                                                <td>${ritem.name}</td>
                                                <td>${ritem.idNumber}</td>
                                                <td>${ritem.cultureDegree}</td>
                                                <td> <#if ritem.isRemove == 1>是</#if>
                                                    <#if ritem.isRemove == 2>否</#if></td>
                                                <td> <#if ritem.inTianjin == 1>是</#if>
                                                    <#if ritem.inTianjin == 2>否</#if></td>
                                                <td> <#if ritem.isSpousesoldier == 1>是</#if>
                                                    <#if ritem.isSpousesoldier == 2>否</#if></td>
                                            </tr>
                                        </#list>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>
                <div id="online-tab" class="main-cont clearfix tab-pane">
                    <div class="panel panel-default">
                        <!-- Default panel contents -->
                        <div class="panel-heading">
                            申请人材料上传情况
                        </div>
                        <!-- Table 多个表格列表组合 -->
                        <div class="table-list-item">
                            <table style="font-size: 14px;" class="table table-hover table-bordered table-condensed">
                                <tr class="info">
                                    <th>预览</th>
                                    <th class="text-info">材料名称</th>
                                    <th class="text-info">失败原因</th>
                                </tr>
                            <#list materialInfos as item>
                                <#if item.onlinePersonMaterial??>
                                    <#if item.onlinePersonMaterial.materialInfoId=="">
                                        <tr>
                                            <td class="text-center">
                                                未上传
                                            </td>
                                            <td>${item.name}</td>
                                            <td></td>
                                        </tr>
                                    <#else>
                                        <tr>
                                            <td style="width:100px;height:100px" class="text-center">
                                                <#if item.onlinePersonMaterial.materialUri!=null>
                                                    <img class="p-img" id="img_${item.onlinePersonMaterial.id?c}"
                                                         style="cursor: pointer;border: 1px solid gray;"
                                                         width="100" height="100"
                                                         src="${item.onlinePersonMaterial.materialUri}">
                                                <#else>
                                                    未上传
                                                </#if>
                                            </td>
                                            <td>${item.onlinePersonMaterial.materialInfoName}
                                                <br>
                                                <a class="download btn btn-mini btn-info" target="_blank"
                                                   href="${item.onlinePersonMaterial.materialUri}"
                                                   download="${item.onlinePersonMaterial.materialInfoName}_${item.onlinePersonMaterial.personId?c}">
                                                    下载
                                                </a>
                                                <br>
                                                <a class="download btn btn-mini btn-primary" > <#--target="_blank" href="${item.onlinePersonMaterial.materialUri}"-->
                                                    <div style="display: none"; position: absolute; margin:0 auto;"><img style="height: 90%;width: auto" src="${item.onlinePersonMaterial.materialUri}"></div>
                                                <span  src="${item.onlinePersonMaterial.materialUri}" onclick="wjwprint(this)">打印</span>
                                                </a>
                                            </td>
                                            <td>${item.onlinePersonMaterial.reason}</td>
                                        </tr>
                                    </#if>
                                <#else>
                                    <tr>
                                        <td class="text-center">
                                            未上传
                                        </td>
                                        <td>${item.name}</td>
                                        <td></td>
                                    </tr>
                                </#if>
                            </#list>
                            </table>
                            <script type="text/javascript">
                                var hostName = window.location.host;
                                if (hostName == "172.16.200.68") {
                                    $(".p-img").each(function () {
                                        var src = $(this).attr("src");
                                        var newSrc = src.replace("218.67.246.52:80", "172.16.200.68:8092");
                                        $(this).attr("src", newSrc);
                                    });
                                    $("a.download").each(function () {
                                        var href = $(this).attr("href");
                                        var newHref = href.replace("218.67.246.52:80", "172.16.200.68:8092");
                                        $(this).attr("href", newHref);
                                    });
                                }
                                $(".p-img").off("click");
                                $(".p-img").on("click", function () {
                                    var img = $('<img src="' + $(this).attr("src") + '">');
                                    $.orangeModal({
                                        title: "图片预览",
                                        destroy: true,
                                        buttons: [
                                            {
                                                text: '旋转',
                                                cls: 'btn btn-info',
                                                handle: function (m) {
                                                    m.$body.find("img").each(function (i, d) {
                                                        var transform = $(this).css('transform');

                                                        if (transform == "none") {
                                                            $(this).css("transform", 'rotate(90deg)');
                                                        } else {
                                                            function getmatrix(a, b, c, d, e, f) {
                                                                var aa = Math.round(180 * Math.asin(a) / Math.PI);
                                                                var bb = Math.round(180 * Math.acos(b) / Math.PI);
                                                                var cc = Math.round(180 * Math.asin(c) / Math.PI);
                                                                var dd = Math.round(180 * Math.acos(d) / Math.PI);
                                                                var deg = 0;
                                                                if (aa == bb || -aa == bb) {
                                                                    deg = dd;
                                                                } else if (-aa + bb == 180) {
                                                                    deg = 180 + cc;
                                                                } else if (aa + bb == 180) {
                                                                    deg = 360 - cc || 360 - dd;
                                                                }
                                                                return deg >= 360 ? 0 : deg;
                                                            }

                                                            var deg = eval('get' + transform);
                                                            var step = 90;//每次旋转多少度
                                                            $(this).css({'transform': 'rotate(' + (deg + step) % 360 + 'deg)'});
                                                        }
                                                    });
                                                }
                                            },
                                            {
                                                text: '放大',
                                                cls: 'btn btn-info',
                                                handle: function (m) {
                                                    m.$body.find("img").each(function (i, d) {
                                                        var that = this;
                                                        $(this).css("height", $(that).height()*1.1);
                                                        $(this).css("width",$(that).width()*1.1);
                                                    });
                                                }
                                            }, {
                                                text: '缩小',
                                                cls: 'btn btn-info',
                                                handle: function (m) {
                                                    m.$body.find("img").each(function (i, d) {
                                                        var that = this;
                                                        $(this).css("height", $(that).height()*0.9);
                                                        $(this).css("width",$(that).width()*0.9);
                                                    });
                                                }
                                            }
                                        ]
                                    }).show().$body.html(img);
                                });
                                $("#move-table").find("button").on("click", function () {
                                    console.info($(this).parent().parent().find("div.display").length);
                                    $(this).parent().parent().find("div.display").hide();
                                    $(this).parent().parent().find("div.select").show();
                                });
                                $("#move-table").find("select").on("change", function () {
                                    var that = $(this);
                                    var v = $(this).val();
                                    var id = $(this).attr("role-id");
                                    $.ajax({
                                        type: "POST",
                                        dataType: "json",
                                        url: window.App.href + "/api/score/houseRelationship/updateIsMove",
                                        data: {
                                            "id": id,
                                            "isMove": v
                                        },
                                        success: function (ddd) {
                                            that.parent().parent().find("div.display").html(v === 1 ? "是" : "否");
                                            that.parent().parent().find("div.display").show();
                                            that.parent().parent().find("div.select").hide();
                                        }
                                    });
                                });

                                function wjwprint(ele) {
                                    console.log(ele);
                                    $(ele).prev().show();
                                    $(ele).prev().print({
                                        globalStyles: true,
                                        mediaPrint: false,
                                        stylesheet: null,
                                        noPrintSelector: ".no-print",
                                        iframe: true,
                                        append: null,
                                        prepend: null,
                                        manuallyCopyFormValues: true,
                                        deferred: $.Deferred()
                                    });
                                    $(ele).prev().hide();
                                }
                            </script>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>

