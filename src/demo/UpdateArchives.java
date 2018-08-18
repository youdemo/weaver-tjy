/**
 * 
 */
package seahonor.Lambert;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

/**
 * @author Lambert
   @date 2018年8月3日
	档案变更
 */
public class UpdateArchives implements Action{
	public String execute(RequestInfo requestInfo) {
		BaseBean log = new BaseBean();
		RecordSet rs = new RecordSet();
		RecordSet rs1 = new RecordSet();
		RecordSet rs2 = new RecordSet();
		String tablename = "";//流程表的名字 
		String oTableSql1 ="";//第一个明细表需要执行的sql 语句
		String oTableSql2 ="";//第二个明细表需要执行的sql语句 
		log.writeLog("UpdateArchives IS START");
		String workflowId = requestInfo.getWorkflowid();//获取流程的workflow ID
		String request = requestInfo.getRequestid();//获取流程的request ID

		
		
		String sql = " Select tablename From Workflow_bill Where id in ("
				+ " Select formid From workflow_base Where id= " + workflowId + ")";
		rs.execute(sql);
		if (rs.next()) {
			tablename = Util.null2String(rs.getString("tablename"));//获取表的名字
		}
		if(tablename.length()>0) {
			
			//变更多个人
			oTableSql1= " select b.damc,a.sqr,b.jz,b.bghjyr,b.bghjyesj from "+tablename+" a ,"+tablename+"_dt1 b where a.id = b.mainid and " 
				 +" a.requestid ='"+request+"'";
			
			//变更同一个人
			oTableSql2 = " select b.damc ,a.sqr,a.bghjyr,b.jyjzmc,b.bghjysj from "+tablename+" a,"+tablename+"_dt2 b where a.id = b.mainid and "
					+" a.requestid='"+request+"'";
			//执行sql
			rs1.execute(oTableSql1);
			rs2.execute(oTableSql2);
	
			//执行结果(明细表一（多人）)
			while(rs1.next()) {
				String damc = Util.null2String(rs1.getString("damc"));//档案名称名称
				String jz = Util.null2String(rs1.getString("jz"));//卷宗
				String sqr = Util.null2String(rs1.getString("sqr"));//申请人
				String bghjyr = Util.null2String(rs1.getString("bghjyr"));//变更后借阅人
				String bghjyesj = Util.null2String(rs1.getString("bghjyesj"));//变更后借阅时间
				//更新变更人
				sql  = "update uf_FileWa set applicant = '"+bghjyr+"' where archBelong ='"+damc+"' and "
						+" applicant ='"+sqr+"' and  id ='"+jz+"'" ;
				rs.execute(sql);
				}
			
			//执行结果(明细表二（单人）)
			while(rs2.next()) {
				String damc = Util.null2String(rs2.getString("damc"));//档案名称
				String jyjzmc = Util.null2String(rs2.getString("jyjzmc"));//卷宗名称
				String sqr = Util.null2String(rs2.getString("sqr"));//申请人
				String bghjyr = Util.null2String(rs2.getString("bghjyr"));//变更后借阅人
				String bghjysj = Util.null2String(rs1.getString("bghjysj"));//变更后借阅时间
				
				//更新变更人
				sql  = "update uf_FileWa set applicant = '"+bghjyr+"' where archBelong ='"+damc+"' and "
						+" applicant ='"+sqr+"' and  id = '"+jyjzmc +"' " ;
				
				log.writeLog(sql+"更新语句");
				
				rs.execute(sql);
				}
		}
		log.writeLog("UpdateArchives is success");
		return SUCCESS;
	}

}
