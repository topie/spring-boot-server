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
                <li class="">
                    <a data-toggle="tab" href="#material-tab" aria-expanded="false">材料提交</a>
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
                            <table style="font-size: 12px;" class="table table-hover table-condensed">
                                <!-- 两组数据信息的 -->
                                <tr>
                                    <td colspan="6">单位名称：${company.companyName}</td>
                                    <td colspan="6">机构代码：<strong>${company.societyCode}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">联系人：<strong>${company.operator}</strong></td>
                                    <td colspan="6">联系电话：<strong>${company.operatorMobile}</strong></td>
                                </tr>
                                <!-- 一组数据信息的 -->
                                <tr>
                                    <td colspan="12">联系地址：<strong>${company.operatorAddress}</strong></td>
                                </tr>
                            </table>
                        </div>
                        <div class="panel panel-default">
                            <div class="panel-heading">申请人及随迁子女信息</div>
                            <table style="font-size: 12px;" class="table table-hover table-condensed">
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
                                    <td colspan="6">工种：<strong>${profession.jobTypeStr}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">现户口性质：<strong>${move.houseNatureStr}</strong></td>
                                    <td colspan="6">落户性质：<strong>${move.settledNatureStr}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">单位名称：<strong>${other.companyName}</strong></td>
                                    <td colspan="6">单位电话：<strong>${other.companyPhone}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">单位地址：<strong>${other.companyAddress}</strong></td>
                                    <td colspan="6">本人电话：<strong>${other.selfPhone}</strong></td>
                                </tr>
                                <tr>
                                    <td colspan="6">拟落户地区：<strong>${move.regionStr}</strong></td>
                                    <td colspan="6">是否社保缴纳：<strong>${other.socialSecurityPayStr}</strong></td>
                                </tr>
                                <!-- 标题特变长的分为特殊的两组 -->
                                <tr>
                                    <td colspan="6">积分期间有无行政拘留记录：<strong>${other.detentionStr}</strong></td>
                                    <td colspan="6">积分期间有无行获刑记录：<strong>${other.penaltyStr}</strong></td>
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
                                        <table id="move-table" class="table table-hover table-bordered table-condensed">
                                            <tr class="info">
                                                <th>与本人关系</th>
                                                <th>姓名</th>
                                                <th>身份证号</th>
                                                <th>文化程度</th>
                                                <th>是否随迁</th>
                                                <th>操作</th>
                                            </tr>
                                        <#list relation as ritem>
                                            <tr>
                                                <td>${ritem.relationship}</td>
                                                <td>${ritem.name}</td>
                                                <td>${ritem.idNumber}</td>
                                                <td>${ritem.cultureDegree}</td>
                                                <td>
                                                    <div class="display"><#if ritem.isRemove == 1>是<#else>否</#if></div>
                                                    <div class="select" style="display: none;">
                                                        <select role-id="${ritem.id?c}">
                                                            <option value="1" <#if ritem.isRemove == 1>selected</#if> >是</option>
                                                            <option value="0" <#if ritem.isRemove == 0>selected</#if> >否</option>
                                                        </select>
                                                    </div>
                                                </td>
                                                <td>
                                                    <button type="button" class="btn btn-mini btn-danger">修改</button>
                                                </td>
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
                            <table class="table table-hover table-bordered table-condensed">
                                <tr class="info">
                                    <th>预览</th>
                                    <th class="text-info">材料名称</th>
                                </tr>
                                <#list materialInfos as item>
                                    <#if item.onlinePersonMaterial??>
                                        <#if item.onlinePersonMaterial.materialUri=="">
                                        <tr>
                                            <td class="text-center">
                                                未上传
                                            </td>
                                            <td>${item.name}</td>
                                        </tr>
                                        <#else>
                                        <tr>
                                            <td class="text-center">
                                                <img class="p-img" id="img_${item.onlinePersonMaterial.id?c}"
                                                     style="cursor: pointer;border: 1px solid gray;"
                                                     width="100" height="100"
                                                     src="${item.onlinePersonMaterial.materialUri}">
                                            </td>
                                            <td>${item.onlinePersonMaterial.materialInfoName}
                                                <br>
                                                <a class="download btn btn-mini btn-info"
                                                   href="${item.onlinePersonMaterial.materialUri}"
                                                   download="${item.onlinePersonMaterial.materialInfoName}_${item.onlinePersonMaterial.personId?c}">
                                                    下载
                                                </a>
                                            </td>
                                        </tr>
                                        </#if>
                                    <#else>
                                        <tr>
                                            <td class="text-center">
                                                未上传
                                            </td>
                                            <td>${item.name}</td>
                                        </tr>
                                    </#if>
                                </#list>
                            </table>
                            <script type="text/javascript">
                                $(".p-img").off("click");
                                $(".p-img").on("click", function () {
                                    var img = $('<img src="' + $(this).attr("src") + '">');
                                    $.orangeModal({
                                        title: "图片预览",
                                        destroy: true
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
                            </script>
                        </div>
                    </div>
                </div>
                <div id="material-tab" class="main-cont clearfix tab-pane">
                    <div class="panel panel-default">
                        <!-- Default panel contents -->
                        <div class="panel-heading">
                            申请人提交材料确认（<span class="text-danger">请勾选确认申请人送达的材料</span>）
                        </div>
                        <div class="table-list-item">
                            <table class="table table-hover table-bordered table-condensed">
                                <tr class="info">
                                    <th>确认</th>
                                    <th class="text-info">材料名称</th>
                                </tr>
                                <#list materialInfos as item>
                                    <tr>
                                        <td class="text-center">
                                            <input name="material" value="${item.id?c}"
                                                   type="checkbox"/>
                                        </td>
                                        <td>${item.name}</td>
                                    </tr>
                                </#list>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

