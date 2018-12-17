package pdf;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

public class ToHtmlPath extends HttpServlet { 
  
    public void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException { 
  
        HttpSession session = request.getSession();     
        String url = "/workflow/request/ViewRequest2.jsp?requestid=9&_workflowid=54&_workflowtype=&isovertime=0"; 
        String name = "D:\\test\\222.html"; 
        System.out.println("name==="+name);
        ServletContext sc = getServletContext(); 
  
       // String file_name = request.getParameter("file_name");// 你要访问的jsp文件,如news.jsf。 
        // file_name如：fileDetail.jsf?fileId=56.要是有参数， 只有一个参数。并且以参数名作为文件名。 
  
       // String realName = request.getParameter("realName");// 要保存的文件名。如aaa;注意可以没有这个参数。 
  
        //String path = request.getParameter("path");// 你要访问的jsp文件路径。如news。注意可以没有这个参数。 
  
       // String realPath = request.getParameter("realPath");// 你要保存的文件路径,如htmlNews.注意可以没有这个参数。 
        // 下面确定要保存的文件名字。 
//        if (realName == null || realName == "") { 
//            int a = 0; 
//            a = file_name.indexOf("=") + 1; 
//            realName = file_name.substring(a); 
//            if (realName.indexOf(".")>0) { 
//                realName = file_name.substring(0, file_name.indexOf(".")); 
//            } 
//        } 
        // 下面构造要访问的页面。 
//        if (path == null || path.equals("")) { 
//            url = "/" + file_name;// 这是你要生成HTML的jsp文件,如 
//        } else { 
//            url = "/" + path + "/" + file_name;// 这是你要生成HTML的jsp文件,如 
//        } 
        // System.out.println("url==="+url);
        // 下面构造要保存的文件名，及路径。 
        // 1、如果有realPath，则保存在realPath下。 
        // 2、如果有path则保存在path下。 
        // 3、否则，保存在根目录下。 
//        if (realPath == null || realPath.equals("") ) { 
//            if (path == null || path.equals("") ) {
//                // 这是生成的html文件名,如index.htm.说明： ConfConstants.CONTEXT_PATH为你的上下文路径。 
//                name = session.getServletContext().getRealPath("") + "\\" + realName + ".html";
//            } else { 
//                name = session.getServletContext().getRealPath("") + "\\" + path + "\\"
//                        + realName + ".html";// 这是生成的html文件名,如index.html 
//            } 
//        } else { 
//            name = session.getServletContext().getRealPath("") + "\\" + realPath + "\\"
//                    + realName + ".html";// 这是生成的html文件名,如index.html
//        } 
  
        // 访问请求的页面，并生成指定的文件。 
        RequestDispatcher rd = sc.getRequestDispatcher(url); 
  
        final ByteArrayOutputStream os = new ByteArrayOutputStream(); 
  
        final ServletOutputStream stream = new ServletOutputStream() { 
            public void write(byte[] data, int offset, int length) { 
                os.write(data, offset, length); 
            } 
  
            public void write(int b) throws IOException { 
                os.write(b); 
            } 
        }; 
  
        final PrintWriter pw = new PrintWriter(new OutputStreamWriter(os)); 
  
        HttpServletResponse rep = new HttpServletResponseWrapper(response) { 
            public ServletOutputStream getOutputStream() { 
                return stream; 
            } 
  
            public PrintWriter getWriter() { 
                return pw; 
            } 
        }; 
        rep.setCharacterEncoding("gbk");//response的编码为gbk防乱码
        rd.include(request, rep); 
        pw.flush(); 
        FileOutputStream fos = new FileOutputStream(name); // 把jsp输出的内容写到xxx.html 
  
        os.writeTo(fos); 
        fos.close(); 
          
    } 
}