package pdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
public class JspTothml {
	
		private static long star = 0;
		private static long end = 0;
		private static long ttime = 0;
//		返回html代码
		private static String getHtmlCode(String httpUrl)//,int i, int j)
		{
			Date before = new Date();
			star = before.getTime();
			StringBuffer htmlCode = new StringBuffer();
			try {
				InputStream in;
				URL url = new java.net.URL(httpUrl);
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestProperty("User-Agent","Mozilla/4.0");
				connection.connect();
				in = connection.getInputStream();
				java.io.BufferedReader breader = new BufferedReader(new InputStreamReader(in ,
				"GBK"));
				String currentLine;
				while((currentLine=breader.readLine())!=null){
					htmlCode.append(currentLine);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				Date after = new Date();
				end = after.getTime();
				ttime = end-star ;
				System.out.println("执行时间:"+ttime +"毫秒");
			}
			return htmlCode.toString();
		}
//		存储文件
		private static synchronized void writeHtml(String filePath,String info,String flag) {
			PrintWriter pw = null;
			try {
				File writeFile = new File(filePath);
				boolean isExit = writeFile.exists();
				if (isExit != true) {
					writeFile.createNewFile();
				} else {
					if (!flag.equals("NO")) {
						writeFile.delete();
						writeFile.createNewFile();
					}
				}
				pw = new PrintWriter(new FileOutputStream(filePath, true));
				pw.println(info);
				pw.close();
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}finally{
				pw.close();
			}
		}
		public static void main(String[] args) {
			
//			for (int i = 0; i < 20; i++) {
	//
//				for (int j = 0; j < 20; j++) {
	//
//					// System.out.println("j =" +j+": " +39.0183+(0.03409 * j));
//					// }
//					// System.out.println("i =" +i+": " +121.417323+(0.04380 * i));
//					// }
////					System.out
////							.println("http://maps.google.com/maps/api/staticmap?center="
////									+ (39.0183
////									- (0.03409 * j))
////									+ ","
////									+ (121.417323
////									+ (0.04380 * i))
////									+ "&zoom=14&size=512x512&maptype=roadmap&format=png&sensor=false");
//					
//					String url = "http://maps.google.com/maps/api/staticmap?center="
//								+ (39.0183
//								- (0.03409 * j))
//								+ ","
//								+ (121.417323
//								+ (0.04380 * i))
//								+ "&zoom=14&size=512x512&maptype=roadmap&format=png&sensor=false";
//					
//					writeHtml("E:/bat/sina.htm",getHtmlCode(url,i,j),"NO");
//				}
//				System.out.println("==============================");
	//
//			}
			String url = "https://www.baidu.com/";
			writeHtml("D:/test/sina.htm",getHtmlCode(url),"NO");
		} 
//		public static void createHtml(String filePath,String url,String flag) {
//			writeHtml(filePath,getHtmlCode(url,),flag);
//		}
	


	
}
