package demo;



import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import weaver.general.BaseBean;

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
	 * @param textJson 长文本json
	 * @return
	 */
	@POST
	@Path("/request")
	//@Produces(MediaType.TEXT_PLAIN)
	public String createRequest(@FormParam("ZOATJson") String ZOATJson, @FormParam("headJson") String headJson,
			@FormParam("itemsJson") String itemsJson, @FormParam("textJson") String textJson) {
		BaseBean log = new BaseBean();
		log.writeLog("CreateRequestABIService:");
		log.writeLog("ZOATJson:"+ZOATJson);
		log.writeLog("headJson:"+headJson);
		log.writeLog("itemsJson:"+itemsJson);
		log.writeLog("textJson:"+textJson);
		return "success";
	}
}
