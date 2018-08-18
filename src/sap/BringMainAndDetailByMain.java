package sap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.JCO;
import com.weaver.integration.datesource.SAPInterationOutUtil;
import com.weaver.integration.log.LogInfo;

/**
 * 
 */
public class BringMainAndDetailByMain {
	public static BaseBean basebean = new BaseBean();
	private String dateid;

	
	public BringMainAndDetailByMain(){
		this.dateid = "1";
	}
	
	public BringMainAndDetailByMain(String dateid){
		this.dateid = dateid;
	}
	
	/**
	 * ��ȡSAP �ĺ�������
	 * @param id
	 * @return
	 */
	public static String getFunctionNameByWorkflowID(String id) {
		String result = "";
		String sql = "select distinct SAPFunctionName from TMC_SAP_MAPPING where workflowId ="
				+ id;
		RecordSet rs = new RecordSet();
		rs.execute(sql);
		if (rs.next()) {
			result = rs.getString("SAPFunctionName");
		}
		return result;
	}

	/**
	 * �������������ȡ�����
	 * 
	 * @param oaDatas
	 *            Map<OA�ֶ�,ҳ���϶�Ӧ��ֵ>
	 * @param workflowID
	 * @return
	 */
	public String getReturn(Map<String, String> oaDatas, String workflowID,String inTableName,List<Map<String, String>> list) {
		String formid = "-1";
		//basebean.writeLog("---------------------start SAP OA MAPPING ----------------------------");
		JSONObject jsonObj = new JSONObject();
		JSONObject jsonTable = new JSONObject();
		JSONObject jsonOther = new JSONObject();// �����������
		String errorSring = "";
		JCO.Client myConnection = null;
		try {
			myConnection = getSAPcon();
			myConnection.connect();// ����sap
		//	basebean.writeLog("----------test1");
			JCO.Repository myRepository = new JCO.Repository("Repository",myConnection);
			String functionName = getFunctionNameByWorkflowID(workflowID);
			IFunctionTemplate ft = myRepository.getFunctionTemplate(functionName);
			JCO.Function bapi = ft.getFunction();
			JCO.ParameterList paraList = bapi.getImportParameterList();
			JCO.ParameterList inputtable = bapi.getTableParameterList();
			String inputtype=getInputType(workflowID);
			if("inTab".equals(inputtype)){
				JCO.Table jt = bapi.getTableParameterList().getTable(inTableName);
				int length=list.size();
				
				basebean.writeLog("length"+length);
				for(int i=0;i<length;i++){
					Map<String, String> oadata=list.get(i);
					jt.appendRow();
					jt.setRow(i);
					Set keys1 = oadata.keySet();
					Iterator it1 = keys1.iterator();
					while (it1.hasNext()) {
						String oaFieldName = (String) it1.next();
						String value = (String) oadata.get(oaFieldName);
						basebean.writeLog("aaa value"+value+" name "+oaFieldName);
						jt.setValue(value, oaFieldName); 
					}
				}
			}else if("inStr".equals(inputtype)){
				JCO.Structure strlist = paraList.getStructure(inTableName);
				Set<String> keys = oaDatas.keySet();
				Iterator<String> it = keys.iterator();
				while(it.hasNext()){
					String oaFieldName = it.next();
					String value = oaDatas.get(oaFieldName);
					strlist.setValue(value, oaFieldName);
				}	
			}else{
				Set<String> keys = oaDatas.keySet();
				Iterator<String> it = keys.iterator();
				while (it.hasNext()) {
					String oaFieldName = it.next();
					String value = oaDatas.get(oaFieldName);
					try {
						SapOaMappingBean bean = getInputParameter(workflowID,oaFieldName);
			//			basebean.writeLog("Bean Sh��" + bean.toString());
						String sapFieldName = bean.getSAPFieldName();
						if ("inPara".equals(bean.getSAPParameterType())) {
							paraList.setValue(value, sapFieldName);
						} else if ("inStr".equals(bean.getSAPParameterType())) {
							JCO.Structure strlist = paraList.getStructure(bean.getSAPParameterName());
			//				basebean.writeLog("����ṹ������Ϊ��"+ bean.getSAPParameterName());
							strlist.setValue(value, sapFieldName);
						} else if ("inTab".equals(bean.getSAPParameterType())) {
							JCO.Table jt = inputtable.getTable(bean.getSAPParameterName());
		//					basebean.writeLog("����������Ϊ��"+ bean.getSAPParameterName());
							jt.setValue(value, sapFieldName);
						}
			//			basebean.writeLog("OA�ֶ���" + oaFieldName + "=" + value + "������SAP���ֶ�" + sapFieldName);
					} catch (Exception e) {
						basebean.writeLog("����SAP�����г��ִ��󣬴�����ϢΪ��" + e.getMessage());
					}
				}
			}
			myConnection.execute(bapi);

			// �����ؽ��

			// ��������������MESSAGE��STYPE
			JCO.ParameterList outPara = bapi.getExportParameterList();
			List<SapOaMappingBean> otherlist = getOutParameters(workflowID);
			for (SapOaMappingBean outBean : otherlist) {
				String outValue = outPara.getString(outBean.getSAPFieldName());
				
				
				jsonOther.put(outBean.getSAPFieldName(), outValue);
			}

			List<SapOaMappingBean> mainlist = getOutParameters(workflowID,"main");
			List<SapOaMappingBean> dtList = getOutParameters(workflowID, "dt");
			jsonObj.put("table", jsonTable);
			jsonTable.put("main", getReturnValues(bapi, mainlist, formid));
			jsonTable.put("Detail", getReturnValues(bapi, dtList, formid));
			jsonObj.put("msg", errorSring);
			jsonObj.put("type", jsonOther);
			return jsonObj.toString();
		} catch (Exception e) {
			return e.getMessage();
		} finally {
			releaseClient(myConnection);// �ͷ�����
			//basebean.writeLog("---------------------end SAP OA MAPPING ----------------------------");
		}
	}

	private static String getInputType(String workflowid) {
		RecordSet rs = new RecordSet();
		String SAPParameterType = "";
		String sql = "select SAPParameterType from TMC_SAP_MAPPING where SAPParameterType like 'in%' and workflowid ='"
				+ workflowid+"'" ;
		rs.executeSql(sql);
		if (rs.next()) {
			SAPParameterType = Util.null2String(rs
					.getString("SAPParameterType"));
		}
		return SAPParameterType;
	}

	private static JSONArray getReturnValues(JCO.Function bapi,
			List<SapOaMappingBean> list, String formid) throws JSONException {
		JSONArray arr = new JSONArray();
		// ��ȡ���صĽ��
		JCO.ParameterList outTable = bapi.getTableParameterList();
		for (SapOaMappingBean outBean : list) {
		//	basebean.writeLog("��ȡ���صĽ��(outBean)��" + outBean.toString());
			String outValue = "";
			if ("outPara".equals(outBean.getSAPParameterType())) {
				JCO.ParameterList para = bapi.getExportParameterList();
				outValue = para.getString(outBean.getSAPFieldName());
				String oaId = outBean.getOAFieldName();
				oaId = getPageIDByOAField(formid, oaId);
				JSONObject json = new JSONObject();
				json.put(oaId, outValue);
				arr.put(arr.length() == 0 ? 0 : arr.length(), json);// �����ص�����������뵽json�����С�������ݳ���Ϊ0�ʹ�0��ʼ�ǣ���������鳤�ȵ��Ǹ����ݿ�ʼȡ
		//		basebean.writeLog("SAP���������ƣ�" + outBean.getSAPParameterName());
			} else {
				// ���������
				JCO.Table jt = outTable.getTable(outBean.getSAPParameterName());
		//		basebean.writeLog("SAP���������ƣ�" + outBean.getSAPParameterName());
		//		basebean.writeLog("��ȡ���ص�������" + jt.getNumRows());
				if (jt.getNumRows() > 0) {
					for (int i = 0; i < jt.getNumRows(); i++) {
			//			basebean.writeLog("SAP�������ֶ���(for)��" + outBean.getSAPFieldName());
						JSONObject json = new JSONObject();
						if (arr.length() == jt.getNumRows()) {
							json = (JSONObject) arr.get(i);
						}
						jt.setRow(i);
						outValue = jt.getString(outBean.getSAPFieldName());
						
						if("dt".equals(outBean.getOATableType().substring(0, 2))) {// ��ϸ��
							String key = outBean.getOATableType();// ��ϸ������dt1��dt2����
							if (!json.has(key)) {
								JSONObject jStr = new JSONObject();
								String oaId = outBean.getOAFieldName();
								oaId = getPageIDByOAFieldDt(formid, oaId);
								jStr.put(oaId, outValue);
								json.put(key, jStr);
							} else {
								JSONObject jStr = (JSONObject) json.get(key);
								String oaId = outBean.getOAFieldName();
								// ��ȡ���е��ֶ���
								oaId = getPageIDByOAFieldDt(formid, oaId);
								jStr.put(oaId, outValue);
							}
						} else {// ��������
							String oaId = outBean.getOAFieldName();
							oaId = getPageIDByOAField(formid, oaId);
							json.put(oaId, outValue);
						}
						arr.put(i, json);
				//		basebean.writeLog("SAP���������ƣ�" + outBean.getSAPParameterName());
					}
				}
			}
		}
		return arr;
	}

	/**
	 * ��ȡ����ҳ���ֶ���
	 * 
	 * @param workflowid
	 * @param fieldName
	 * @return
	 */
	
	public static String getPageIDByOAField(String formid, String fieldName) {
		return fieldName;
	}
	/*
	public static String getPageIDByOAField(String formid, String fieldName) {
		String sql = "select id from workflow_billfield where billid = "
				+ formid + " and fieldname ='" + fieldName
				+ "' and viewtype = 0 ";
		basebean.writeLog("��ȡ����ҳ���ֶ���sql��" + sql);
		RecordSet rs = new RecordSet();
		rs.execute(sql);
		String id = "";
		if (rs.next()) {
			id = Util.null2String(rs.getString("id"));
		}
		return "field" + id;
	}*/


	/**
	 * ��ȡ��ϸ��ҳ��ID
	 * 
	 * @param workflowid
	 * @param fieldName
	 * @return
	 */
	public static String getPageIDByOAFieldDt(String formid, String fieldName) {
		return fieldName;
	}

	/**
	 * ��ȡ����������Ϣ
	 * 
	 * @param workflowId
	 * @return
	 */
	public static List<SapOaMappingBean> getOutParameters(String workflowId,
			String OAFieldType) {
		List<SapOaMappingBean> list = new ArrayList<SapOaMappingBean>();
		String sql = "select *  from TMC_SAP_MAPPING where workflowid = "
				+ workflowId + " and OATableType like '" + OAFieldType
				+ "%'and  SAPParameterType like 'out%'";
		RecordSet rs = new RecordSet();
		rs.execute(sql);
		while (rs.next()) {
			SapOaMappingBean bean = rowMapping(rs);
			list.add(bean);
		}
		return list;
	}

	public static List<SapOaMappingBean> getOutParameters(String workflowId) {
		List<SapOaMappingBean> list = new ArrayList<SapOaMappingBean>();
		String sql = "select *  from TMC_SAP_MAPPING where workflowid = "
				+ workflowId + " and SAPParameterType = 'outPara' and (OATableType<>'main' or OATableType is null)";
	
		RecordSet rs = new RecordSet();
		rs.execute(sql);
		while (rs.next()) {
			SapOaMappingBean bean = rowMapping(rs);
			list.add(bean);
		}
		return list;
	}

	/**
	 * ��ȡ����Ķ�Ӧ��ϵ
	 * 
	 * @param workflowId
	 * @param OaFieldName
	 * @return
	 */
	public static SapOaMappingBean getInputParameter(String workflowId,
			String OaFieldName) {
		SapOaMappingBean bean = new SapOaMappingBean();
		String sql = "select *  from TMC_SAP_MAPPING where OAFieldName ='"
				+ OaFieldName + "' and workflowid = " + workflowId
				+ " and  SAPParameterType like 'in%'";
	//	basebean.writeLog("��ȡ����Ķ�Ӧ��ϵsql��" + sql);
		RecordSet rs = new RecordSet();
		rs.execute(sql);
		if (rs.next()) {
		//	basebean.writeLog("��ȡ����Ķ�Ӧ��ϵ 12345");
			bean = rowMapping(rs);
		}
		return bean;
	}

	/**
	 * �������̻�ȡ��Ҫ���뵽SAP��OA��������
	 * 
	 * @param workflowId
	 * @return
	 */
	public static List<String> getOAParaName(String workflowId) {
		List<String> list = new ArrayList<String>();
		String sql = "select OAFieldName from TMC_SAP_MAPPING where workflowId = "
				+ workflowId + " and SAPParameterType in ('inPara','inStr')";
//		basebean.writeLog("�������̻�ȡ��Ҫ���뵽SAP��OA��������sql��" + sql);
		RecordSet rs = new RecordSet();
		rs.execute(sql);
		while (rs.next()) {
			list.add(rs.getString("OAFieldName"));
		}
		return list;
	}

	/**
	 * �����ݿ��л�ȡ��OA SAP��Ӧ��ϵ
	 * 
	 * @param workflowId
	 * @return
	 */
	public static List<SapOaMappingBean> getRelation(String workflowId) {
		String sql = "select OAFieldName, SAPParameterType,SAPParameterName,SAPFieldName from TMC_SAP_MAPPING where workflowId = "
				+ workflowId;
		sql = "select * from TMC_SAP_MAPPING where workflowId = " + workflowId;
//		basebean.writeLog("�����ݿ��л�ȡ��OA SAP��Ӧ��ϵsql��" + sql);
		RecordSet rs = new RecordSet();
		rs.execute(sql);
		List<SapOaMappingBean> list = new ArrayList<SapOaMappingBean>();
		while (rs.next()) {
			list.add(rowMapping(rs));
		}
		return list;
	}

	/**
	 * �������ݿ��ȡ�Ķ�Ӧ��ϵת��Ϊbean����
	 * 
	 * @param rs
	 * @return
	 */
	private static SapOaMappingBean rowMapping(RecordSet rs) {
		SapOaMappingBean bean = new SapOaMappingBean();
		bean.setId(Util.getIntValue(rs.getInt("id") + "", 0));
		bean.setOATableType(Util.null2String(rs.getString("oATableType")));
		bean.setSAPTableType(Util.null2String(rs.getString("sAPTableType")));
		bean.setOAFieldName(Util.null2String(rs.getString("OAFieldName")));
		bean.setSAPFieldName(Util.null2String(rs.getString("SAPFieldName")));
		bean.setSAPParameterType(Util.null2String(rs.getString("SAPParameterType")));
		bean.setSAPParameterName(Util.null2String(rs.getString("SAPParameterName")));
		bean.setSAPFunctionName(Util.null2String(rs.getString("sAPFunctionName")));
		bean.setWorkflowId(Util.null2String(rs.getString("WorkflowId")));
		
	//	basebean.writeLog("�������ݿ��ȡ�Ķ�Ӧ��ϵת��Ϊbean���� = " + bean.toString());
		return bean;
	}

	/**
	 * ���sap������
	 * 
	 * @return
	 */
	private  JCO.Client getSAPcon() {
		// �õ�����Դ������
		BaseBean bean = new BaseBean();
		String datasourceid = bean.getPropValue("datasource", "datasourceid");
	//	datasourceid = Util.null2String(datasourceid);
		datasourceid = dateid;
		//basebean.writeLog("SAP����ԴID��" + datasourceid);
		SAPInterationOutUtil sapUtil = new SAPInterationOutUtil();
		JCO.Client myConnection = (JCO.Client) sapUtil.getConnection(datasourceid, new LogInfo());
	//	basebean.writeLog("connection---" + myConnection);
		return myConnection;
	}

	/**
	 * �ͷ�sap����
	 * 
	 * @return
	 */
	public static void releaseClient(JCO.Client myConnection) {
		if (null != myConnection) {
			JCO.releaseClient(myConnection);
		}
	}
}
