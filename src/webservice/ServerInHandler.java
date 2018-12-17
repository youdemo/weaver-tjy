package webservice;

import java.util.Iterator;

import javax.activation.DataHandler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.attachments.Attachment;
import org.codehaus.xfire.handler.AbstractHandler;

import com.fr.plugin.PluginLicense;

import weaver.general.BaseBean;

/**
 * Web服务端前置的Handler类
 */
public class ServerInHandler extends AbstractHandler {
	public String name = "";
	public void invoke(MessageContext ctx) throws Exception {
		System.out.println("ServerInHandler is Invoked...");
		BaseBean log = new BaseBean();
		Iterator it = ctx.getInMessage().getAttachments().getParts();
		log.writeLog("attachmentsize:"+ctx.getInMessage().getAttachments().size());
		while(it.hasNext()){
			Attachment attachment = (Attachment) it.next();
			DataHandler dh=attachment.getDataHandler();
			String name=dh.getName();
			log.writeLog("attachmentname:"+name);
			log.writeLog("attachmentid:"+attachment.getId());
			this.name=attachment.getId();
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}