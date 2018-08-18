package demo;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

public class TestFCAction implements Action{

	@Override
	public String execute(RequestInfo info) {
		String workflowID = info.getWorkflowid();
		String requestid = info.getRequestid();
		RecordSet rs = new RecordSet();
		BaseBean log = new BaseBean();
		log.writeLog("TestFCAction aaaa");
		return SUCCESS;
	}

}
