<div style="width:595.3pt;margin-top:30.0pt;"><p><span
        style="font-size:24.0pt;color:#000000;white-space:pre-wrap;">申报编号：</span><span
        style="font-size:24.0pt;color:#000000;white-space:pre-wrap;">${person.acceptNumber}</span></p>
    <p style="text-align:center;white-space:pre-wrap;"><span
            style="font-size:30.0pt;font-weight:bold;color:#000000;white-space:pre-wrap;">天津市居住证积分材料接收凭证</span></p>
    <p style="text-align:left;white-space:pre-wrap;"><br/></p>
    <p style="text-align:left;white-space:pre-wrap;"><span style="font-size:18.0pt;color:#000000;white-space:pre-wrap;">申请人姓名：${person.name}</span>
    </p>
    <p style="text-indent:36.0pt;text-align:left;white-space:pre-wrap;"><span
            style="font-size:18.0pt;color:#000000;white-space:pre-wrap;">您于</span><span
            style="font-size:18.0pt;color:#000000;text-decoration:underline;white-space:pre-wrap;">${year}</span><span
            style="font-size:18.0pt;color:#000000;white-space:pre-wrap;">年</span><span
            style="font-size:18.0pt;color:#000000;text-decoration:underline;white-space:pre-wrap;">${month}</span><span
            style="font-size:18.0pt;color:#000000;white-space:pre-wrap;">月</span><span
            style="font-size:18.0pt;color:#000000;text-decoration:underline;white-space:pre-wrap;">${day}</span><span
            style="font-size:18.0pt;color:#000000;white-space:pre-wrap;">日，向本机关提出的</span><span
            style="font-size:18.0pt;color:#000000;text-decoration:underline;white-space:pre-wrap;">居住证积分入户</span><span
            style="font-size:18.0pt;color:#000000;white-space:pre-wrap;">申请所提交的下列申请材料，本机关已收到：</span></p>
    <#list mList as item>
        <p style="line-height:14.0pt;font-size:14.0pt;margin-left:36.0pt;text-align:left;white-space:pre-wrap;"><span
                style="font-size:14.0pt;color:#000000;white-space:pre-wrap;">${item_index+1}、${item.materialName}</span>
        </p>
    </#list>
    <#if mList?size lt 9>
        <#list 1..(9-mList?size) as t>
             <p style="line-height:14.0pt;font-size:14.0pt;margin-left:36.0pt;text-align:left;white-space:pre-wrap;"><span
                     style="font-size:14.0pt;color:#000000;white-space:pre-wrap;"> </span>
             </p>
        </#list>
    </#if>
    <#if renshe==true>
    <p style="text-indent:36.0pt;text-align:left;white-space:pre-wrap;"><br/></p>
    <p style="font-size:14.0pt;text-align:left;white-space:pre-wrap;"><span style="color:#000000;white-space:pre-wrap;"></span><span
            style="color:#000000;white-space:pre-wrap;"></span><span style="color:#000000;white-space:pre-wrap;"></span>
    </p>
    <#else>
    <p style="text-indent:36.0pt;text-align:left;white-space:pre-wrap;"><br/></p>
    <p style="text-align:left;white-space:pre-wrap;"><span style="color:#000000;white-space:pre-wrap;"></span><span
            style="color:#000000;white-space:pre-wrap;"></span><span style="color:#000000;white-space:pre-wrap;"></span>
    </p>
    </#if>
    <p style="text-align:right;white-space:pre-wrap;margin-bottom: 5pt;margin-right: 10pt;"><span
            style="font-size:18.0pt;color:#000000;text-decoration:underline;white-space:pre-wrap;">${nowYear}</span><span
            style="font-size:18.0pt;color:#000000;white-space:pre-wrap;">年</span><span
            style="font-size:18.0pt;color:#000000;text-decoration:underline;white-space:pre-wrap;">${nowMonth}</span><span
            style="font-size:18.0pt;color:#000000;white-space:pre-wrap;">月</span><span
            style="font-size:18.0pt;color:#000000;text-decoration:underline;white-space:pre-wrap;">${nowDay}</span><span
            style="font-size:18.0pt;color:#000000;white-space:pre-wrap;">日</span>
    </p>
    <p style="text-align:left;white-space:pre-wrap;"><span style="float:left;width:300pt;font-size:18.0pt;color:#000000;white-space:pre-wrap;">受理部门：${department}</span><span style="font-size:18.0pt;color:#000000;white-space:pre-wrap;">受理时间：${now}</span></p>
    <p style="text-align:left;white-space:pre-wrap;"><span style="float:left;width:300pt;font-size:18.0pt;color:#000000;white-space:pre-wrap;">受理人：${user.displayName}</span><span style="font-size:18.0pt;color:#000000;white-space:pre-wrap;">送达时间：${now}</span></p>
    <p style="text-align:left;white-space:pre-wrap;"><span style="float:left;width:300pt;font-size:18.0pt;color:#000000;white-space:pre-wrap;">经办人：<#list 1..24 as t> </#list></span><span style="font-size:18.0pt;color:#000000;white-space:pre-wrap;">联系电话：<#list 1..20 as t> </#list></span></p>
    <script>

    </script>
</div>

