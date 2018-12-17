package demo;

import java.text.SimpleDateFormat;
import java.util.Date;

import weaver.general.MD5;

public class Testjsd {

	public static void main(String[] args) {
		String aaa="123|1234";
		String aa[] = aaa.split("|");
		System.out.println(aa[0]);
		SimpleDateFormat dateFormate = new SimpleDateFormat("yyyyMMdd");
		String nowDate = dateFormate.format(new Date());
		String ssokey="/JST4293/b3043e8baf6cfe1d2d590da3419e3109";
		String workcode = "";
		String key = "sYWf5fhsB5drv80jUi5Q3HJZKyK8in8YnIjyF3gognH9vV2KiMMe97KpVx6VXDNjOQK6D8opqLOVs2CmFi3KWdPZRMIAuvtMrtJ";
		String token = "";
		String result = "";
		MD5 md5 = new MD5();
		if("".equals(ssokey)) {
			result = "0";
		}else {
			try {
				workcode = ssokey.substring(1,ssokey.lastIndexOf("/"));
				token = ssokey.substring(ssokey.lastIndexOf("/")+1,ssokey.length());
			}catch(Exception e) {
				result = "0";
			}
		}
		if(!"0".equals(result)) {
			String str=key+workcode+nowDate;
			System.out.println(md5.getMD5ofStr(str));
			System.out.println(token.toUpperCase());
			if(token.toUpperCase().equals(md5.getMD5ofStr(str).toUpperCase())) {
				result ="1";
			}
		}
		System.out.println(result);
		

	}

}
