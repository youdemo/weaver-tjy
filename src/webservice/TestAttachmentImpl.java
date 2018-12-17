package webservice;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import javax.activation.DataHandler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.attachments.Attachment;
import org.codehaus.xfire.service.invoker.AbstractInvoker;

import weaver.general.BaseBean;

public class TestAttachmentImpl implements TestAttachmentsInterface {

	@Override
	public String testaa(String asc) {
		BaseBean log = new BaseBean();
		log.writeLog("TestAttachmentImpl 111...");
		MessageContext ctx=AbstractInvoker.getContext();
		String name = "";
		if(ctx == null) {
			return "dddsss12";
		}
		if(ctx.getInMessage() == null) {
			return "dddsss";
		}
		if(ctx.getInMessage().getAttachments() == null) {
			return "dddsss33";
		}
		Iterator it = ctx.getInMessage().getAttachments().getParts();
		log.writeLog("attachmentsize111:"+ctx.getInMessage().getAttachments().size());
		while(it.hasNext()){
			Attachment attachment = (Attachment) it.next();
			
			DataHandler dh=attachment.getDataHandler();
			
			name = dh.getName();
			log.writeLog("attachmentname111:"+name);
			try {
				log.writeLog("getContent:"+String.valueOf(dh.getContent()));
				log.writeLog("attachmentid11:"+new String(attachment.getId().getBytes("gbk"),"utf-8"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "ccc"+name+"dd";
	}
	
	


}
