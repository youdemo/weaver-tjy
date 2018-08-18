package webapi;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;

/**
 * 获取签字意见abi接口
 * @author tangjianyong 2018-08-17
 *
 */
@Path("/GetRequestRemarkABIService")
public class GetRequestRemarkABIService {
	/**
	 * ABI获取流程签字意见方法 前端调用跨域
	 * @param codes 流程id
	 * @return
	 */
	@POST
	@Path("/request")
	//@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.APPLICATION_XML）
	public Response getRequestRemarks(@QueryParam("codes") String codes) {
		BaseBean log = new BaseBean();
		log.writeLog("GetRequestRemarkABIService:");
		log.writeLog("codes:"+codes);
		
		JSONArray ja = new JSONArray();
		if(!"".equals(codes)){
			try {
				ja = getJsonStr(codes);
			} catch (Exception e) {
				log.writeLog("获取签字意见失败");
				log.writeLog(e);
			}
		}
		return Response.ok().entity(ja.toString()).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}
	/**
	 * 获取签字意见
	 * @param requestids
	 * @return
	 * @throws Exception
	 */
	private JSONArray getJsonStr(String requestids) throws Exception{
		BaseBean log = new BaseBean();
		RecordSet rs = new RecordSet();
		String sql = "";
		String nodename = "";
		String lastname = "";
		String jobtitlename = "";
		String time = "";
		String logtype = "";
		String remark = "";
		String workcode = "";
		String requestid = "";
		if("".equals(requestids)){
			return null;
		}
		
		JSONArray array = new JSONArray();
		String[] requestidArr = requestids.split(",");
		for(String rqid:requestidArr){
			//获取requestlog表的操作记录
			String sql_remark = "select a.requestid,d.nodename,  a.operator,b.lastname,b.workcode,c.jobtitlename,a.operatedate||' '||a.operatetime as time, a.logtype, remark "+
						"from workflow_requestlog a,hrmresource b,hrmjobtitles c,workflow_nodebase d where a.operator=b.id and b.jobtitle=c.id "+
	                " and a.nodeid=d.id and a.requestid in(" + rqid + ") and a.logtype not in ('e') "+
						"  order by a.operatedate asc ,a.operatetime asc";
			//log.writeLog("testsql:"+sql_remark);
			rs.executeSql(sql_remark);
			int count = 1;
			while(rs.next()){
				JSONObject jo = new JSONObject();
				requestid = Util.null2String(rs.getString("requestid"));
				nodename = Util.null2String(rs.getString("nodename"));
				workcode = Util.null2String(rs.getString("workcode"));
				lastname = Util.null2String(rs.getString("lastname"));
				jobtitlename = Util.null2String(rs.getString("jobtitlename"));
				time = Util.null2String(rs.getString("time"));
				logtype = Util.null2String(rs.getString("logtype"));
				logtype = getState(logtype,7);
				remark = removeHtmlTag(Util.null2String(rs.getString("remark")));
				if(count == 1){
					logtype=getState("new",7);
				}
				jo.put("NODENAME", nodename);
				jo.put("WORKCODE", workcode);
				jo.put("LASTNAME", lastname);
				jo.put("JOBTITLE", jobtitlename);
				jo.put("APPROVETIME", time);
				jo.put("LOGTYPE", logtype);
				jo.put("REMARK", remark);
				jo.put("CODE", requestid);
				
				array.put(jo);
				count++;
			}
			int count_remark = 0;
			String currentnodeid = "";
			String jjgdid = "0";
			String workflowid = "";
			String nodename2 = "";
			sql_remark = " select count(1) as count  from workflow_requestbase where requestid= "+rqid+" and currentnodetype>=3";
			rs.executeSql(sql_remark);
			if(rs.next()){
				count_remark = rs.getInt("count");
			}
			if(count_remark > 0){
				sql_remark = " select  a.currentnodeid,nodename,a.workflowid from workflow_requestbase a, workflow_nodebase b " +
						"where a.requestid= " + rqid + " and a.currentnodeid=b.id";
				rs.executeSql(sql_remark);
				if(rs.next()){
					JSONObject jo = new JSONObject();
					nodename2 = Util.null2String(rs.getString("nodename"));
					
					currentnodeid = Util.null2String(rs.getString("currentnodeid"));
					workflowid = Util.null2String(rs.getString("workflowid"));
					sql_remark = "select jjgdid from workflow_flownode where workflowid="+workflowid+" and nvl(jjgdid,0)>0";
					rs.executeSql(sql_remark);
					if(rs.next()){
						jjgdid = Util.null2String(rs.getString("jjgdid"));
					}
					if(jjgdid.equals(currentnodeid)){
						sql_remark = "select a.requestid,d.nodename,  a.operator,b.lastname,b.workcode,c.jobtitlename,a.operatedate||' '||a.operatetime as time, a.logtype, remark "+
								"from workflow_requestlog a,hrmresource b,hrmjobtitles c,workflow_nodebase d where a.operator=b.id and b.jobtitle=c.id "+
			                " and a.nodeid=d.id and a.requestid in("+rqid+") and a.logtype in ('e') "+
								"  order by a.operatedate asc ,a.operatetime asc";
						rs.executeSql(sql_remark);
						if(rs.next()){
							nodename = Util.null2String(rs.getString("nodename"));
							workcode = Util.null2String(rs.getString("workcode"));
							lastname = Util.null2String(rs.getString("lastname"));
							jobtitlename = Util.null2String(rs.getString("jobtitlename"));
							time = Util.null2String(rs.getString("time"));
							logtype = Util.null2String(rs.getString("logtype"));
							logtype = getState(logtype,7);
							remark = removeHtmlTag(Util.null2String(rs.getString("remark")));
							jo = new JSONObject();
							jo.put("NODENAME", nodename);
							jo.put("WORKCODE", workcode);
							jo.put("LASTNAME", lastname);
							jo.put("JOBTITLE", jobtitlename);
							jo.put("APPROVETIME", time);
							jo.put("LOGTYPE", "拒绝");
							jo.put("REMARK", remark);
							jo.put("CODE", rqid);
							array.put(jo);
						}
					}
					jo = new JSONObject();
					jo.put("NODENAME", nodename2);
					jo.put("WORKCODE", "");
					jo.put("LASTNAME", "");
					jo.put("JOBTITLE", "");
					jo.put("APPROVETIME", "");
					jo.put("LOGTYPE", "");
					jo.put("REMARK", "");
					jo.put("CODE", rqid);
					
					array.put(jo);
				}
			}else{
				//获取当前节点的记录
				sql_remark = "select b.workcode,b.lastname,c.jobtitlename,d.nodename " +
						" from workflow_currentoperator a, hrmresource b, hrmjobtitles c ,workflow_nodebase d " +
						" where a.userid = b.id and b.jobtitle = c.id  and a.nodeid=d.id and a.isremark not in(2,4)  " +
						" and a.requestid = "+rqid;
				rs.executeSql(sql_remark);
				while(rs.next()){
					JSONObject jo = new JSONObject();
					nodename = Util.null2String(rs.getString("nodename"));
					workcode = Util.null2String(rs.getString("workcode"));
					lastname = Util.null2String(rs.getString("lastname"));
					jobtitlename = Util.null2String(rs.getString("jobtitlename"));
					jo.put("NODENAME", nodename);
					jo.put("WORKCODE", workcode);
					jo.put("LASTNAME", lastname);
					jo.put("JOBTITLE", jobtitlename);
					jo.put("APPROVETIME", "");
					jo.put("LOGTYPE", "");
					jo.put("REMARK", "");
					jo.put("CODE", rqid);
					
					array.put(jo);
				}
			}
		}
		//log.writeLog("test:"+array.toString());
		return array;
	}
	
	/**
	 * 获取流程操作类型
	 * 
	 * @param state
	 * @param saplan
	 * @return
	 */
	private String getState(String state, int saplan) {
		String statename = "";
		if (saplan == 8) {
			if ("0".equals(state)) {
				statename = "Approval";
			} else if ("2".equals(state)) {
				statename = "Submit";
			} else if ("3".equals(state)) {
				statename = "Return";
			} else if ("4".equals(state)) {
				statename = "Reopen";
			} else if ("5".equals(state)) {
				statename = "Delete";
			} else if ("6".equals(state)) {
				statename = "Activation";
			} else if ("7".equals(state)) {
				statename = "Retransmission";
			} else if ("9".equals(state)) {
				statename = "Comment";
			} else if ("a".equals(state)) {
				statename = "Opinion inquiry";
			} else if ("b".equals(state)) {
				statename = "Opinion inquiry reply";
			} else if ("e".equals(state)) {
				statename = "filing";
			} else if ("h".equals(state)) {
				statename = "Transfer";
			} else if ("i".equals(state)) {
				statename = "intervene";
			} else if ("j".equals(state)) {
				statename = "Transfer feedback";
			} else if ("t".equals(state)) {
				statename = "CC";
			} else if ("s".equals(state)) {
				statename = "Supervise";
			} else if ("i".equals(state)) {
				statename = "Process intervention";
			} else if ("1".equals(state)) {
				statename = "Save";
			} else if ("new".equals(state)) {
				statename = "Create";
			} else {
				statename = "";
			}
		} else {
			if ("0".equals(state)) {
				statename = "批准";
			} else if ("2".equals(state)) {
				statename = "提交";
			} else if ("3".equals(state)) {
				statename = "退回";
			} else if ("4".equals(state)) {
				statename = "重新打开";
			} else if ("5".equals(state)) {
				statename = "删除";
			} else if ("6".equals(state)) {
				statename = "激活";
			} else if ("7".equals(state)) {
				statename = "转发";
			} else if ("9".equals(state)) {
				statename = "批注";
			} else if ("a".equals(state)) {
				statename = "意见征询";
			} else if ("b".equals(state)) {
				statename = "意见征询回复";
			} else if ("e".equals(state)) {
				statename = "归档";
			} else if ("h".equals(state)) {
				statename = "转办";
			} else if ("i".equals(state)) {
				statename = "干预";
			} else if ("j".equals(state)) {
				statename = "转办反馈";
			} else if ("t".equals(state)) {
				statename = "抄送";
			} else if ("s".equals(state)) {
				statename = "督办";
			} else if ("i".equals(state)) {
				statename = "流程干预";
			} else if ("1".equals(state)) {
				statename = "保存";
			} else if ("new".equals(state)) {
				statename = "创建";
			} else {
				statename = "";
			}
		}
		return statename;
	}

	/**
	 * 签字意见html格式化
	 * 
	 * @param content
	 *            签字意见remark字段文本
	 * @return
	 */
	private String removeHtmlTag(String content) {
		Pattern p = Pattern.compile("<([a-zA-Z]+)[^<>]*>(.*?)</\\1>");
		Matcher m = p.matcher(content);
		if (m.find()) {
			content = content
					.replaceAll("<([a-zA-Z]+)[^<>]*>(.*?)</\\1>", "$2");
			content = removeHtmlTag(content);
		}
		return content;
	}

}
