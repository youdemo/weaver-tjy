package webservice;

import java.util.Iterator;

import javax.activation.DataHandler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.attachments.Attachment;
import org.codehaus.xfire.handler.AbstractHandler;

import a.a.a.ac.df;
import weaver.general.BaseBean;

public class TestAttachments extends AbstractHandler{
//	public void addattach(MessageContext arg0) {
//		SOAPMessage soapMessage = arg0.getMessage();
//		
//		Iterator attachments = soapMessage.getAttachments();
//		while(attachments.hasNext()){
//			AttachmentPart attachment = (AttachmentPart)attachments.next();
//			try {
//				DataHandler dh = attachment.getDataHandler();
//			} catch (SOAPException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			System.out.println("ATTACHMENT CONTENT ID=" + attachment.getContentId());
//			System.out.println("ATTACHMENT CONTENT LOCATION="+ attachment.getContentLocation());
//			System.out.println("ATTACHMENT CONTENT TYPE="+ attachment.getContentType());
//			System.out.println("ATTACHMENT TO_STRING ="+ attachment.toString());
//			//System.out.println("ATTACHMENT CONTTENT BODY=" + CfDataSourceReader.convertToString(dh.getDataSource()));
//			}
//
////			} catch (SOAPException e) {
////			// TODO Auto-generated catch block
////			out.println("ReceiveMMSSoapContextHandler invoke SOAPEXCEPTION OCCURRED!!");
////			e.printStackTrace();
////			} catch (IOException e) {
////			// TODO Auto-generated catch block
////			out.println("ReceiveMMSSoapContextHandler invoke IOEXCEPTION OCCURRED!!");
////			e.printStackTrace();
////			}
//
//	}
	public String testaa(String asc) {
		return "ccc";
	}

	@Override
	public void invoke(MessageContext mct) throws Exception {
		BaseBean log = new BaseBean();
		Iterator it = mct.getInMessage().getAttachments().getParts();
		log.writeLog("attachmentsize:"+mct.getInMessage().getAttachments().size());
		while(it.hasNext()){
			Attachment attachment = (Attachment) it.next();
			DataHandler dh=attachment.getDataHandler();
			dh.getDataSource()
			log.writeLog("attachmentid:"+attachment.getId());
		}
		
		
	}
}
