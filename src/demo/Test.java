package demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import weaver.general.FWHttpConnectionManager;

public class Test {
	public void test1() throws Exception {
		String testUrl="http://erp.justech.com/SSORedirect.aspx?back_url=http://portal.justech.com";//原始链接
	    URL Url = new URL(testUrl);
	    HttpURLConnection conn=(HttpURLConnection)Url.openConnection();
	    conn.getResponseCode();
	    String realUrl=conn.getURL().toString();//跳转后所返回的链接
	    System.out.println(realUrl);
	}
	
	public void test2() {
		HttpURLConnection connection = null;
		try {
			URL getUrl = new URL("http://www.csdn.net/");
			connection = (HttpURLConnection) getUrl.openConnection();
			connection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String lines;
			StringBuffer sb = new StringBuffer();
			while ((lines = reader.readLine()) != null) {
				// System.out.println(lines);
				sb.append(lines + "\n");
			}
			reader.close();
			System.out.println(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				connection.disconnect();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}
	
	public void test3() throws Exception, IOException {
		HttpClient client = new HttpClient();//定义client对象  
	    client.getHttpConnectionManager().getParams().setConnectionTimeout(2000);//设置连接超时时间为2秒（连接初始化时间）  
	    GetMethod method = new GetMethod("http://www.google.com.hk/");//访问下谷歌的首页  
	    int statusCode = client.executeMethod(method);
	}
	public String getFinallyUrl(HttpClient client, String url) {
		
		PostMethod g = new PostMethod(url);
		try {
			client.executeMethod(g);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return url;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return url;
		}
		Header locationHeader = g.getResponseHeader("location");
		if (locationHeader != null) {
			url = locationHeader.getValue();
			url = getFinallyUrl(client, url);
		}
		if (g != null) {
			g.releaseConnection();
		}
		return url;
	}
	
	public void test4() throws Exception, IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL("aa")
				.openConnection();
		conn.setInstanceFollowRedirects(false);
		conn.getURL()
		conn.setConnectTimeout(5000);
        String urlss= conn.getHeaderField("Location");
        
        SimpleDateFormat dateFormate = new SimpleDateFormat("yyyyMMdd");
        String nowdate
	}

}
