package pdf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class GetWorkFlowHTML {

		public static String doGet(String url) throws Exception {  
	        URL localURL = new URL(url);  
	        URLConnection connection = localURL.openConnection();  
	        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;  
	        httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");  
	        httpURLConnection.setRequestProperty("Content-Type",  
	                "application/text");  
	  
	        InputStream inputStream = null;  
	        InputStreamReader inputStreamReader = null;  
	        BufferedReader reader = null;  
	        StringBuffer resultBuffer = new StringBuffer();  
	        String tempLine = null;  
	  
	        if (httpURLConnection.getResponseCode() >= 3000) {  
	            throw new Exception(  
	                    "HTTP Request is not success, Response code is "  
	                            + httpURLConnection.getResponseCode());  
	        }  
	  
	        try {  
	            inputStream = httpURLConnection.getInputStream();  
	            inputStreamReader = new InputStreamReader(inputStream);  
	            reader = new BufferedReader(inputStreamReader);  
	  
	            while ((tempLine = reader.readLine()) != null) {  
	                resultBuffer.append(tempLine).append("\n");  
	            }  
	        }catch (Exception e) {
	        	 if (reader != null) {  
		                reader.close();  
		            }  
		  
		            if (inputStreamReader != null) {  
		                inputStreamReader.close();  
		            }  
		  
		            if (inputStream != null) {  
		                inputStream.close();  
		            }  
			} finally {  
	  
	            if (reader != null) {  
	                reader.close();  
	            }  
	  
	            if (inputStreamReader != null) {  
	                inputStreamReader.close();  
	            }  
	  
	            if (inputStream != null) {  
	                inputStream.close();  
	            }  
	  
	        }  
	        writeFile(resultBuffer.toString());
	        return resultBuffer.toString();  
	    }  
		public static void main(String[] args) throws Exception {
			System.out.println(doGet("http://127.0.0.1:8080/workflow/request/ViewRequestIframe.jsp?requestid=9&_workflowid=54&_workflowtype=&isovertime=0"));
			}

		public static void writeFile(String str) throws Exception{;
			String fileUrl = "D:\\test\\2017062101.html";
			File newfile=new File(fileUrl); 
			if(newfile.exists()){//若在该目录下已经有一个文件和新文件名相同，则不允许重命名 
	            //System.out.println("已经存在！"); 
			}else{ 
	        	//创建文件
	        	newfile.createNewFile();
	        	
	        }
			//文件输出流
	        FileOutputStream fos = new FileOutputStream(newfile);
	        //字符输出流
	        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(fos,"unicode"));  
	        //将字符串写入到文件中
	        output.write(str);  
	        //关闭输出流
	        output.close();  
		}

}
