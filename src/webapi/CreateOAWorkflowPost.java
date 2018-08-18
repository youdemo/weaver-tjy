package webapi;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
/**
 * ����arcgis����rest�ӿ�
 * @author tangjianyong
 *
 */
@Path("/CreateOAWorkflowPost")
public class CreateOAWorkflowPost extends BaseBean{
	/**
	 * ����rest�ӿ�
	 * @param dataInfo json������
	 * @return ���ش������
	 */
	@POST
	@Path("/request")
    //@Consumes(MediaType.APPLICATION_FORM_URLENCODED)  
   // @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})  
	public String CreateOARequest(String dataInfo){
		BaseBean log = new BaseBean();
		log.writeLog("CreateOAWorkflowService dataInfo:" + dataInfo);
		if ("".equals(dataInfo)) {
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("MSG_TYPE", "E");
			retMap.put("MSG_CONTENT", "����jsonֵΪ�գ�����");
			retMap.put("OA_ID", "0");
			log.writeLog("CreateOAWorkflowService result:"
					+ getJsonStr(retMap));
			return getJsonStr(retMap);
		}
		String result=createWorkflowRequest("52001722", dataInfo);
		log.writeLog("CreateOAWorkflowService result:"+result);
		return result;
	}
	/**
	 * 
	 * @param workcode ��������ID
	 * @param jsonStr json��
	 * @return
	 */
	private String createWorkflowRequest(String workcode,String jsonStr){
		RecordSet rs = new RecordSet();
		String sqr = "";
		String sqrbm = "";
		String workflowId = "1908";
		String sql = "select id,departmentid from hrmresource where workcode='"
				+ workcode + "' and status in(0,1,2,3) and belongto is  null";
		rs.executeSql(sql);
		if (rs.next()) {
			sqr = Util.null2o(rs.getString("id"));
			sqrbm = Util.null2o(rs.getString("departmentid"));
		} else {
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("MSG_TYPE", "E");
			retMap.put("MSG_CONTENT", "��Ա����޷�ƥ�䣡");
			retMap.put("OA_ID", "0");

			return getJsonStr(retMap);
		}
		String json = "";
		try {
			json = getworkflowJson(jsonStr,sqr);
		} catch (Exception e) {
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("MSG_TYPE", "E");
			retMap.put("MSG_CONTENT", "json��ʽ����");
			retMap.put("OA_ID", "0");

			return getJsonStr(retMap);
		}
		AutoRequestService ars = new AutoRequestService();
		String result = ars.createRequest(workflowId, json, sqr, "1");
		return result;
	}
	/**
	 * 
	 * @param jsonstr ����json��
	 * @param sqr ����������
	 * @return ���ؽ����õ�json
	 * @throws Exception
	 */
	private String getworkflowJson(String jsonstr,String sqr) throws Exception{
		String ObjectID = "";
		String title = "";
		String desctription = "";
		String geometry = "";
		String url = "";
		String datetime = "";
		String globalid = "";
		JSONObject jo = new JSONObject(jsonstr);
		ObjectID = jo.getString("objectid");
		title = jo.getString("title");
		desctription = jo.getString("description");
		geometry = jo.getString("geometry");
		datetime = jo.getString("datetime");
		globalid = jo.getString("globalid");
		url=UrlTrans2(ObjectID,title);
		JSONObject json = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject details = new JSONObject();

		json.put("HEADER", header);
		json.put("DETAILS", details);
		
		header.put("sqr", sqr);
		header.put("OBJECTID", ObjectID);
		header.put("Title", title);
		header.put("Desctription", desctription);
		header.put("geometry", geometry);
		header.put("datetime", datetime);
		header.put("globalid", globalid);
		header.put("url", url);
		
		return json.toString();
	}
	private String getJsonStr(Map<String, String> map) {
		JSONObject json = new JSONObject();
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = map.get(key);
			try {
				json.put(key, value);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return json.toString();
	}
	private String UrlTrans2(String ObjectID, String title) {
		// String url = "<a href='" + fileUrl + "' target='_blank'>" + filename
		// + "</a>";
		String url = "<a target='_blank' href='https://portal.app.com.cn/incident.html?objectid="+ObjectID
				+ "' >"+ title + "</a>";
		return url;
	}
	
	private String UrlTrans(String ObjectID, String title) {
		// String url = "<a href='" + fileUrl + "' target='_blank'>" + filename
		// + "</a>";
		String url = "<a href='javascript:void(window.open(&quot;"
				+ "https://portal.app.com.cn/incident.html?objectid="+ObjectID
				+ "&quot;,&quot;_blank&quot;,&quot;toolbar=no,scrollbar=no,location=no,top=50,left=50,width=1200,height=550&quot;));' >"
				+ title + "</a>";
		return url;
	}
}
