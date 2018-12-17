package webservice;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;

public class TestAttach {

	public static void main(String[] args) {
		MobileCodeWS();

	}
	
	public static void MobileCodeWS() {
        try {
            ServiceClient serviceClient = new ServiceClient();
           // HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator(); 
           // auth.setUsername("USEROA"); 
           // auth.setPassword("PassOA1234");
           // serviceClient.getOptions().setProperty(HTTPConstants.AUTHENTICATE, auth);
            //创建服务地址WebService的URL,注意不是WSDL的URL
            String url = "http://127.0.0.1:7000/services/TestAttachments?wsdl";
            EndpointReference targetEPR = new EndpointReference(url);
            Options options = serviceClient.getOptions();
            options.setTo(targetEPR);
            //确定调用方法（wsdl 命名空间地址 (wsdl文档中的targetNamespace) 和 方法名称 的组合）
            options.setAction("http://localhost/services/TestAttachments/testaa");

            OMFactory fac = OMAbstractFactory.getOMFactory();
            /*
             * 指定命名空间，参数：
             * uri--即为wsdl文档的targetNamespace，命名空间
             * perfix--可不填
             */
            OMNamespace omNs = fac.createOMNamespace("http://localhost/services/TestAttachments", "");
            // 指定方法
            OMElement method = fac.createOMElement("testaa", omNs);
            // 指定方法的参数
          //  OMElement mobileCode = fac.createOMElement("Data", omNs);
          //  mobileCode.setText("{\"ProcessType\":\"制度流程\",\"ProcessEndDate\":\"2018-09-29\",\"ProcessCode\":\"1460773\"}");
           // OMElement userID = fac.createOMElement("userID", omNs);
           // userID.setText("");
           // method.addChild(mobileCode);
            //method.addChild(userID);
            method.build();

            //远程调用web服务
            OMElement result = serviceClient.sendReceive(method);
            System.out.println(result);
            serviceClient.cleanupTransport();
            serviceClient.cleanup();

        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        }
    }

}
