package pdf;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class CallJspToHtml {
	  
    //这个方法适当重载，就可以省去一些参数传递。
    public static void CallOnePage() { 
           try { 
            String str = "http://127.0.0.1:8080/test/toHtmlPath";
            int httpResult; //请求返回码
  
            URL url = new URL(str); //URL发送指定连接请求
            URLConnection connection = url.openConnection(); 
            connection.connect(); 
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection; 
            httpResult = httpURLConnection.getResponseCode(); 
              
            if (httpResult != HttpURLConnection.HTTP_OK) { //返回码为200则连接成功
             System.out.println("没有连接成功!"); 
            } else { 
             System.out.println("连接成功了!!!!!"); 
            } 
           } catch (Exception e) { 
               e.printStackTrace();
           } 
          } 
}