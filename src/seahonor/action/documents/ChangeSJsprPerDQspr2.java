package seahonor.action.documents;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

/**
 * @author Walter Wen
 * @version 1.0 /2018-11-23
 */
public class ChangeSJsprPerDQspr2 implements Action{
	/**
	 * 根据当前操作者更新上级审批人
	*/	
	@Override
	public String execute(RequestInfo info) {
		BaseBean log = new BaseBean();
		log.writeLog("ChangeManagerPerAssigned——————");	
		String workflowID = info.getWorkflowid();
		String requestid = info.getRequestid();		
		RecordSet rs = new RecordSet();
		String tableName = "";	
		String sql  = " Select tablename From Workflow_bill Where id in ("
				+ " Select formid From workflow_base Where id= "
				+ workflowID + ")";
		rs.execute(sql);
		if(rs.next()){
			tableName = Util.null2String(rs.getString("tablename"));
		}
		log.writeLog("tableName="+tableName);
		sql = "select * from "+tableName + " where requestid="+requestid;
		rs.execute(sql);
		if(rs.next()){	
			String mainid = Util.null2String(rs.getString("id"));
			String DQspr = Util.null2String(rs.getString("DQspr"));  //当前审批/操作人
			log.writeLog("Before:DQspr="+DQspr);
			//更新人员上级
			rs.execute("update "+tableName+" set SJspr = (select managerid from hrmresource where id = "+DQspr+") where id ="+mainid);
			rs.execute("update "+tableName+" set DQspr = (select managerid from hrmresource where id = "+DQspr+") where id ="+mainid);
			log.writeLog("After:DQspr="+DQspr);
			rs.execute("select SJspr from "+tableName + " where id="+mainid);
			if(rs.next()){
				String SJspr = Util.null2String(rs.getString("SJspr"));			
				log.writeLog("SJspr="+SJspr);			
			}						
		} 

		return SUCCESS;
	}
}
