package test;

import weaver.general.BaseBean;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

public class TestAction implements Action{

	@Override
	public String execute(RequestInfo info) {
		BaseBean log = new BaseBean();
		String workflowID = info.getWorkflowid();
		String requestid = info.getRequestid();
		log.writeLog("TestAction aaaa");
		System.out.println("TestAction aaaa");
		return SUCCESS;
	}

}
