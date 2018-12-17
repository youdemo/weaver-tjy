package demo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;


public class WebApi {
	public static String postConnection(String url, String param) throws Exception {
		PrintWriter printWriter = null;
		BufferedReader bufferedReader = null;			
		HttpURLConnection httpURLConnection = null;

		StringBuffer responseResult = new StringBuffer();

		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			httpURLConnection = (HttpURLConnection) realUrl.openConnection();
			httpURLConnection.setConnectTimeout(1000);
			httpURLConnection.setReadTimeout(1000);
			// 设置通用的请求属性
			httpURLConnection.setRequestProperty("accept", "*/*");
			httpURLConnection.setRequestProperty("connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Content-Length", String.valueOf(param.length()));
			httpURLConnection.setRequestProperty("Charset", "utf-8");
			// 发送POST请求必须设置如下两行
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			printWriter = new PrintWriter(httpURLConnection.getOutputStream());
			// 发送请求参数
			printWriter.write(param);
			// flush输出流的缓冲
			printWriter.flush();
			// 根据ResponseCode判断连接是否成功
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode == httpURLConnection.HTTP_OK) {
				// 定义BufferedReader输入流来读取URL的ResponseData
				bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					responseResult.append(line);
				}
				return responseResult.toString();
			}
			return null;
		} catch (ConnectException e) {
			throw new Exception(e);
		} catch (MalformedURLException e) {
			throw new Exception(e);
		} catch (IOException e) {
			throw new Exception(e);
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			httpURLConnection.disconnect();
			try {
				if (printWriter != null) {
					printWriter.close();
				}
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static String post(String url, String param) throws Exception {
		PrintWriter printWriter = null;
		BufferedReader bufferedReader = null;			
		HttpURLConnection httpURLConnection = null;

		StringBuffer responseResult = new StringBuffer();

		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			httpURLConnection = (HttpURLConnection) realUrl.openConnection();
			httpURLConnection.setConnectTimeout(1000);
			httpURLConnection.setReadTimeout(1000);
			// 设置通用的请求属性
			httpURLConnection.setRequestProperty("accept", "*/*");
			httpURLConnection.setRequestProperty("connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Content-Length", String.valueOf(param.length()));
			httpURLConnection.setRequestProperty("Charset", "utf-8");
			httpURLConnection.setr
			// 发送POST请求必须设置如下两行
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			printWriter = new PrintWriter(httpURLConnection.getOutputStream());
			// 发送请求参数
			printWriter.write(param);
			// flush输出流的缓冲
			printWriter.flush();
			// 根据ResponseCode判断连接是否成功
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode == httpURLConnection.HTTP_OK) {
				// 定义BufferedReader输入流来读取URL的ResponseData
				bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					responseResult.append(line);
				}
				return responseResult.toString();
			}
			return null;
		} catch (ConnectException e) {
			throw new Exception(e);
		} catch (MalformedURLException e) {
			throw new Exception(e);
		} catch (IOException e) {
			throw new Exception(e);
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			httpURLConnection.disconnect();
			try {
				if (printWriter != null) {
					printWriter.close();
				}
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	 public static String getMethod(String url)  {
		 	BufferedReader br = null;			
			HttpURLConnection conn = null;
			  StringBuffer responseResult = new StringBuffer();
			try {
		    URL restURL = new URL(url);		 
		     conn = (HttpURLConnection) restURL.openConnection();
		 
		    conn.setRequestMethod("GET"); // POST GET PUT DELETE
		    conn.setRequestProperty("Accept", "application/json");
		 
		    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    while((line = br.readLine()) != null ){
		    	responseResult.append(line);
		    }
		 
		    br.close();
		    conn.disconnect();
			}catch (Exception e) {
				e.printStackTrace();
			}finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				 conn.disconnect();
			}
		    return responseResult.toString();
	 }
		    
	
	public static String testurl(String url,String msg) throws Exception   {
		HttpPost post = new HttpPost(url);

		ResponseHandler responseHandler = new BasicResponseHandler();
		StringEntity entity;
		try {
			entity = new StringEntity(msg, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Exception(e);
		}
		post.setEntity(entity);
		HttpClient httpClient = new DefaultHttpClient();
		String response;
		try {
			response = (String) httpClient.execute(post,
					responseHandler);
			response=new String(response.getBytes("ISO-8859-1"),"UTF-8");
		} catch (ClientProtocolException e) {
			httpClient.getConnectionManager().shutdown();
			throw new Exception(e);
		} catch (IOException e) {
			httpClient.getConnectionManager().shutdown();
			throw new Exception(e);
		}	
		httpClient.getConnectionManager().shutdown();
		return response;
	}
	public static void main(String[] args) throws Exception {
		String sr = getMethod("http://192.168.0.102:8089/transfer/encodeUser?userId=123");
		JSONObject aa = new JSONObject(sr);
		String data = aa.getString("data");
		String sr = testurl("http://192.168.0.102:8089/transfer/jde",data);
		System.out.println(sr);
	}
	
	
}
