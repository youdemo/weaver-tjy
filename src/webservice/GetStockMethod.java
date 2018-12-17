package htkj.mm.getstock;

import htkj.mm.webservice.SI_WLKC_SENDServiceStub;
import htkj.mm.webservice.SI_WLKC_SENDServiceStub.DATALINE_type0;
import htkj.mm.webservice.SI_WLKC_SENDServiceStub.DATELINE_type0;
import htkj.mm.webservice.SI_WLKC_SENDServiceStub.DT_WLKC_OREQ;
import htkj.mm.webservice.SI_WLKC_SENDServiceStub.DT_WLKC_ORES;
import htkj.mm.webservice.SI_WLKC_SENDServiceStub.MT_WLKC_OREQ;
import htkj.mm.webservice.SI_WLKC_SENDServiceStub.MT_WLKC_ORES;
import java.io.PrintStream;
import java.text.DecimalFormat;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HttpTransportProperties.Authenticator;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetStockMethod
{
  public static String getStock(String matnr, String werks, boolean flag)
  {
    String stock = "";
    try {
      SI_WLKC_SENDServiceStub si_wlkc_sendServiceStub = new SI_WLKC_SENDServiceStub();

      HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
      auth.setUsername("WS_USER");
      auth.setPassword("ht1234");
      si_wlkc_sendServiceStub._getServiceClient().getOptions().setProperty("_NTLM_DIGEST_BASIC_AUTHENTICATION_", auth);

      SI_WLKC_SENDServiceStub.MT_WLKC_OREQ mT_WLKC_OREQ0 = new SI_WLKC_SENDServiceStub.MT_WLKC_OREQ();
      SI_WLKC_SENDServiceStub.DT_WLKC_OREQ param = new SI_WLKC_SENDServiceStub.DT_WLKC_OREQ();

      SI_WLKC_SENDServiceStub.DATALINE_type0[] dataline_type0s = new SI_WLKC_SENDServiceStub.DATALINE_type0[1];
      SI_WLKC_SENDServiceStub.DATALINE_type0 datalineType0 = new SI_WLKC_SENDServiceStub.DATALINE_type0();
      datalineType0.setMATNR(matnr);
      datalineType0.setWERKS(werks);

      dataline_type0s[0] = datalineType0;
      param.setDATALINE(dataline_type0s);

      mT_WLKC_OREQ0.setMT_WLKC_OREQ(param);
      SI_WLKC_SENDServiceStub.DT_WLKC_ORES out = si_wlkc_sendServiceStub.SI_WLKC_SEND(mT_WLKC_OREQ0).getMT_WLKC_ORES();
      SI_WLKC_SENDServiceStub.DATELINE_type0[] outs = out.getDATELINE();
      double tmp_sum = 0.0D;
      JSONArray jsonArray = new JSONArray();
      for (int i = 0; i < outs.length; ++i) {
        JSONObject jsonObj = new JSONObject();
        SI_WLKC_SENDServiceStub.DATELINE_type0 tmp_type = outs[i];
        jsonObj.put("CINSM", tmp_type.getCINSM());
        jsonObj.put("CLABS", tmp_type.getCLABS());
        jsonObj.put("CSPEM", tmp_type.getCSPEM());
        jsonObj.put("INSME", tmp_type.getINSME());
        jsonObj.put("LABST", tmp_type.getLABST());
        jsonObj.put("LGORT", tmp_type.getLGORT());
        jsonObj.put("MAKTX", tmp_type.getMAKTX());
        jsonObj.put("MATNR", tmp_type.getMATNR());
        jsonObj.put("SPEME", tmp_type.getSPEME());
        jsonArray.put(jsonObj);
        if ((tmp_type.getLGORT().equals("1047")) || (tmp_type.getLGORT().equals("1048")))
          continue;
        if (flag)
          tmp_sum = tmp_sum + Double.valueOf(tmp_type.getCLABS()).doubleValue() + Double.valueOf(tmp_type.getCINSM()).doubleValue() + Double.valueOf(tmp_type.getCSPEM()).doubleValue();
        else {
          tmp_sum = tmp_sum + Double.valueOf(tmp_type.getLABST()).doubleValue() + Double.valueOf(tmp_type.getINSME()).doubleValue() + Double.valueOf(tmp_type.getSPEME()).doubleValue();
        }

      }

      DecimalFormat df = new DecimalFormat("#.000");
      String stockDetail = df.format(tmp_sum);
      jsonArray.put(stockDetail);

      stock = stockDetail;
    } catch (Exception e) {
      stock = "";
      System.out.println(e.getMessage());
    }
    return stock;
  }

  public static void main(String[] args) {
    System.out.println("result=" + getStock("10000425", "1010", true));
  }
}