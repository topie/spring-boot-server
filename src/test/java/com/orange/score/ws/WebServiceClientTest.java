package com.orange.score.ws;

import com.alibaba.csb.ws.sdk.DumpSoapUtil;
import com.alibaba.csb.ws.sdk.WSClientSDK;
import com.orange.score.module.score.ws.SOAP3Response;
import org.dom4j.DocumentException;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class WebServiceClientTest {



    private static SOAPMessage formatSoapString(String soapString) {
        MessageFactory msgFactory;
        try {
            msgFactory = MessageFactory.newInstance();
            SOAPMessage reqMsg = msgFactory
                    .createMessage(new MimeHeaders(), new ByteArrayInputStream(soapString.getBytes("UTF-8")));
            reqMsg.saveChanges();
            return reqMsg;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void testWS2WSWithDispath() throws Exception {
        String ns = "http://service.webinterface.yzym.si.sl.neusoft.com/";
        String wsdlWS2WSAddr = "http://172.30.1.59:9081/juZhuZhengJiFen/1.0.0/ws2ws?wsdl";
        QName serviceName = new QName(ns, "NeuWebService");
        QName portName = new QName(ns, "NeuWebServicePortType");
        Service service = Service.create(serviceName);
        service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, wsdlWS2WSAddr);
        Dispatch<SOAPMessage> dispatch = service.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);
        String req = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" \n"
                + "xmlns:ser=\"http://service.webinterface.yzym.si.sl.neusoft.com/\">\n" + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n" + "      <ser:RsResidentJFRDBusinessRev>\n" + "         <!--ticket:-->\n"
                + "         <ser:arg0>NEUSERVICE_GGFW_TICKET_12</ser:arg0>\n" + "         <!--buzzNumb:-->\n"
                + "         <ser:arg1>TJZSYL_JFRDXT_003</ser:arg1>\n" + "         <!--sender:-->\n"
                + "         <ser:arg2>JFRDXT</ser:arg2>\n" + "         <!--reciver:-->\n"
                + "         <ser:arg3>TJZSYL</ser:arg3>\n" + "         <!--operatorName:-->\n"
                + "         <ser:arg4>自动打分操作员</ser:arg4>\n" + "         <!--content:-->\n"
                + "         <ser:arg5><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ROOT><QUERY_PRAMS><idNumber>449581890311122</idNumber><partnerIdNnumber></partnerIdNnumber><lessThan35>0</lessThan35><canAdd>0</canAdd><busType>3</busType></QUERY_PRAMS></ROOT>]]></ser:arg5>\n"
                + "      </ser:RsResidentJFRDBusinessRev>\n" + "   </soapenv:Body>\n" + "</soapenv:Envelope>";
        InputStream is = new ByteArrayInputStream(req.getBytes());
        SOAPMessage request = MessageFactory.newInstance().createMessage(null, is);
        dispatch = WSClientSDK
                .bind(dispatch, "3b12cb12fdf54a9296988ef3479fdf44", "j0R2XYM2mJjJ+dNNyc3rWdsVpAQ=", "juZhuZhengJiFen",
                        "1.0.0");
        System.out.println("Send out the request: " + req);
        SOAPMessage reply = dispatch.invoke(request);
        if (reply != null)
            System.out.println("Response from invoke:" + DumpSoapUtil.dumpSoapMessage("response", reply));
        else System.out.println("Response from invoke is null");

        SOAP3Response util = new SOAP3Response();
        try {
            util.analysis(DumpSoapUtil.dumpSoapMessage("response", reply));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        System.out.println(util.getAppCode());
        System.out.println(util.getUnitName());
    }
}
