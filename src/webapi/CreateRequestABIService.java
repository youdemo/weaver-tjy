package webapi;



import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import weaver.general.BaseBean;
import weaver.general.Util;
import APPDEV.HQ.SAPOAInterface.CreateRequestAction;

/**
 * abirestful接口
 * @author tangjianyong 2018-08-02
 *
 */
@Path("/CreateRequestABIService")
public class CreateRequestABIService {
	/**
	 * ABI创建流程webservice调用方法
	 * @param ZOATJson zoat00020表json
	 * @param headJson 主表json
	 * @param itemsJson 明细表json
	 * @return
	 */
	@POST
	@Path("/request")
	//@Produces(MediaType.TEXT_PLAIN)
	public String createRequest(@FormParam("ZOATJson") String ZOATJson, @FormParam("headJson") String headJson,
			@FormParam("itemsJson") String itemsJson) {
		BaseBean log = new BaseBean();
		log.writeLog("CreateRequestABIService:");
		log.writeLog("ZOATJson:"+ZOATJson);
		log.writeLog("headJson:"+headJson);
		log.writeLog("itemsJson:"+itemsJson);
		CreateRequestAction cra = new CreateRequestAction();
		String result=cra.createRequest(Util.null2String(ZOATJson), Util.null2String(headJson), Util.null2String(itemsJson), "");
		return "success:"+result;
	}
}
