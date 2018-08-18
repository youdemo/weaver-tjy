package com.app.doc.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;

import org.json.JSONObject;
import org.apache.axis.encoding.Base64;
import sun.misc.BASE64Decoder;

import weaver.conn.RecordSet;
import weaver.docs.webservices.DocAttachment;
import weaver.docs.webservices.DocInfo;
import weaver.docs.webservices.DocServiceImpl;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.hrm.User;
import weaver.hrm.resource.ResourceComInfo;
import weaver.soa.workflow.request.Cell;
import weaver.soa.workflow.request.DetailTable;
import weaver.soa.workflow.request.DetailTableInfo;
import weaver.soa.workflow.request.MainTableInfo;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;
import weaver.soa.workflow.request.RequestService;
import weaver.soa.workflow.request.Row;

public class DocWfCreateService extends BaseBean {

	/**
	 * 
	 * @param json   数据格式
	 * @param jsonDetail  明细记录    {"dt1":[{"a": "Brett","b":"McLaughlin"},{"a":"Jason","b":"Hunter"}]}
	 * @param whoCreate 创建人工号，未查询到以管理员身份创建
	 * @param wfID 系统创建流程ID  请查询数据库或在流程上以右键查看wfid
	 * @param docField 定义的文档字段名称
	 * @param docName 文档字段名称
	 * @param seccategory 文档目录
	 * @param isnext 是否流转下一节点
	 * @return  返回流程IDrequestid <0 为异常数据，  请查看系统异常列表   ；其余异常以错误信息直接返回
	 */
	public String createWf(String json,String jsonDetail,String whoCreate,String wfID,String docField,String docName,String seccategory,String isnext){
		BaseBean log = new BaseBean();
		RecordSet rs = new RecordSet();
		// 判断触发 流程是否存在 及有效
		String sql = "select count(1) as cNO from workflow_base where isvalid = 1 and id= " + wfID;
		rs.executeSql(sql);
		rs.next();
		if (rs.getInt("cNO") <= 0)  return "系统流程ID不存在、无效或历史版本";
		
		// 处理判断人员有效性  有问题转管理员触发
		String creator = "";
		sql = "select id from hrmresource where status in(0,1,2,3) and workcode  = '" + whoCreate + "'";
		rs.executeSql(sql);
		if(rs.next()){
			creator = rs.getString("id");
		}
		if("".equals(creator)) creator = "1"; 
		
		// 无明细  处理
		RequestInfo requestinfo = new RequestInfo();  // 流程创建对象
		// 主表信息
		MainTableInfo mti = new MainTableInfo();
		
		try {
			List<Property> list = new ArrayList<Property>();
			JSONObject wfJson = new JSONObject(json);			
			if(wfJson == null) return "传输JSON格式异常";			
			Iterator<String> it = wfJson.keys();
			while (it.hasNext()) {
				String key = it.next().toString();
				if(key.equalsIgnoreCase(docField)) continue;  // 文档字段特殊处理
				String value = wfJson.getString(key);
				Property pro = new Property();
				pro.setName(key);
				if (Util.null2String(value).length() > 0) {
					pro.setValue(value);
					list.add(pro);
				}
			}
			
			// 处理文档字段
			String ffVal = wfJson.getString(docField);
			if(ffVal!= null &&!"".equals(ffVal)){
				Property pro = new Property();
				pro.setName(docField);
				pro.setValue(getDocId(docName,ffVal,creator,seccategory));
				list.add(pro);
			}
			
			if(list.size() == 0) return "传输JSON格式异常";			
			Property properties[] = new Property[list.size()];
			for (int index = 0; index < list.size(); index++) {
				properties[index] = list.get(index);
			}
			mti.setProperty(properties);   //  设置主表			
			//  添加明细表
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return "传输JSON格式异常";
		}
		
		//  明细表 处理
		DetailTableInfo details = new DetailTableInfo();	
		try {
			
			JSONObject dts = new JSONObject(jsonDetail);
			List<DetailTable> list_detail = new ArrayList<DetailTable>();
			Iterator it_dts = dts.keys();
			int length = 0;
			while (it_dts.hasNext()) {
				length = length + 1;
				it_dts.next();
			}
			for (int j = 1; j <= length; j++) {

				JSONArray arr = null;
				try {
					arr = dts.getJSONArray("dt" + j);
				} catch (Exception e) {
					continue;
				}
				List<Row> list_row = new ArrayList<Row>();
				DetailTable dt = new DetailTable();
				for (int i = 0; i < arr.length(); i++) {
					JSONObject jo = arr.getJSONObject(i);

					Row row = new Row();
					List<Cell> list_cell = new ArrayList<Cell>();
					Iterator it = jo.keys();
					while (it.hasNext()) {
						String key = it.next().toString();
						String value = jo.getString(key);
						//
						Cell cel = new Cell();
						cel.setName(key);
						if (Util.null2String(value).length() > 0) {
							cel.setValue(value);
							list_cell.add(cel);

						}
					}
					int size = list_cell.size();
					Cell cells[] = new Cell[size];
					for (int index = 0; index < list_cell.size(); index++) {
						cells[index] = list_cell.get(index);
					}
					row.setCell(cells);
					row.setId("" + i);
					list_row.add(row);
				}
				int size = list_row.size();
				// if(size == 0) break;
				Row rows[] = new Row[size];
				for (int index = 0; index < list_row.size(); index++) {

					rows[index] = list_row.get(index);
				}
				dt.setRow(rows);
				dt.setId("" + j);
				list_detail.add(dt);
			}
			int size = list_detail.size();
			DetailTable detailtables[] = new DetailTable[size];
			for (int index = 0; index < list_detail.size(); index++) {
				detailtables[index] = list_detail.get(index);
			}
			details.setDetailTable(detailtables);
		} catch (Exception e) {
			e.printStackTrace();
			return "传输JSON格式异常(明细表)";
		}
		
		requestinfo.setDetailTableInfo(details);
		String requestid = "";
		requestinfo.setMainTableInfo(mti);
		requestinfo.setIsNextFlow(isnext);
		requestinfo.setDescription(getRequestName(wfID, creator));
		requestinfo.setCreatorid(creator);
		requestinfo.setWorkflowid(wfID);
		requestinfo.setRequestlevel("0");
		requestinfo.setRemindtype("0");
		RequestService res = new RequestService();
		try {
			requestid = res.createRequest(requestinfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return requestid;
	}
	
	private String getRequestName(String workflowID, String creater) {
		String requestName = "";
		String workflowname = "";
		String name = "系统管理员";
		SimpleDateFormat aa = new SimpleDateFormat("yyyy-MM-dd");
		String date = aa.format(new Date());
		String sql = "select lastname from hrmresource where id=" + creater;
		RecordSet rs = new RecordSet();
		rs.executeSql(sql);
		if (rs.next()) {
			name = Util.null2String(rs.getString("lastname"));
		}

		sql = "select workflowname from workflow_base where id=" + workflowID;
		rs.executeSql(sql);
		if (rs.next()) {
			workflowname = Util.null2String(rs.getString("workflowname"));
		}
		requestName = workflowname + "-" + name + "-" + date;
		return requestName;
	}
	
	private String getDocId(String name, String value,String createrid,String seccategory) throws Exception {
		String docId = "";
		DocInfo di= new DocInfo();
		di.setMaincategory(0);
		di.setSubcategory(0);
		di.setSeccategory(Integer.valueOf(seccategory));	
		di.setDocSubject(name.substring(0, name.lastIndexOf(".")));	
		DocAttachment doca = new DocAttachment();
		doca.setFilename(name);
		byte[] buffer = new BASE64Decoder().decodeBuffer(value);
		String encode = Base64.encode(buffer);
		doca.setFilecontent(encode);
		DocAttachment[] docs = new DocAttachment[1];
		docs[0]=doca;
		di.setAttachments(docs);
		String departmentId="-1";			
		User user = new User();			
		ResourceComInfo res = new ResourceComInfo();
		departmentId = res.getDepartmentID(createrid);
		user.setUid(Integer.parseInt(createrid));
		user.setUserDepartment(Integer.parseInt(departmentId));
		user.setLanguage(7);
		user.setLogintype("1");
		user.setLoginip("127.0.0.1");
		DocServiceImpl ds = new DocServiceImpl();
		try {
			docId = String.valueOf(ds.createDocByUser(di, user));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return docId;
	}
	
}
