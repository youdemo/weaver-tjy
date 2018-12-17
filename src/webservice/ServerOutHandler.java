package webservice;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;

/**
 * Web服务端后置的Handler类
 */
public class ServerOutHandler extends AbstractHandler {
	public void invoke(MessageContext ctx) throws Exception {
		System.out.println("ServerOutHandler is Invoked...");
		System.out.println(ctx.getProperty("myServerParam"));
		System.out.println("ServerOutHandler is Exit...");
	}
}