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
 * abirestful�ӿ�
 * @author tangjianyong 2018-08-02
 *
 */
@Path("/CreateRequestABIService")
public class CreateRequestABIService {
	/**
	 * ABI��������webservice���÷���
	 * @param ZOATJson zoat00020��json
	 * @param headJson ����json
	 * @param itemsJson ��ϸ��json
	 * @param textJson ���ı�json
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
