package demo;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import jp.sourceforge.reedsolomon.RsDecode;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class TestRest {
	 public static void main(String[] args) throws UnsupportedEncodingException, Exception {  
			String info="{ OBJECTID: '1',Title: '≤‚ ‘',Desctription: '≤‚ ‘',geometry:{ x:121,y: 31,spatialReference: { wkid: 4326 } } }";
			JSONObject jo = new JSONObject(info);
			ClientConfig config = new DefaultClientConfig();  
	        Client client = Client.create(config);  
	        WebResource service = client.resource(getBaseURI());  
	  
	          
	        MultivaluedMap<String, String> param = new MultivaluedMapImpl();  
	        param.add("ZOATJson", "123");
	        param.add("headJson", "123");  
	        param.add("itemsJson", "123");  
	        param.add("textJson", "123");  
	        System.out.println(service.post(String.class,"ZOATJson=123&headJson=222&itemsJson=12323&textJson=22"));  
	          
//	        System.out.println(service
//	                .type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class)); 
	    }  
	    private static URI getBaseURI() {  
	        return UriBuilder.fromUri(  
	                "http://127.0.0.1:7000/demo/CreateRequestABIService/request").build();  
	    }  
}
