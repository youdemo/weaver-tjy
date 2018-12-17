package pdf;

import java.io.File;

public class HtmlToPdf {
	private static final String TOPDFTOOL = "D:\\wkhtmltopdf\\bin\\wkhtmltopdf.exe";

    /**
     * html转pdf
     * @param srcPath html路径，可以是硬盘上的路径，也可以是网络路径
     * @param destPath pdf保存路径
     * @return 转换成功返回true
     */
    public static boolean convert(String srcPath, String destPath) {

        File file = new File(destPath);
        File parent = file.getParentFile();
        // 如果pdf保存路径不存在，则创建路径
        if (!parent.exists()) {
            parent.mkdirs();
        }

        //c:\wkhtmltopdf.exe http://www.csdn.net c:\csdn.pdf"
        StringBuilder cmd = new StringBuilder();
        cmd.append(TOPDFTOOL);
        cmd.append(" ");
        cmd.append(srcPath);
        cmd.append(" ");
        cmd.append(destPath);
        System.out.println(cmd.toString());
        boolean result = true;
        try {
            Process proc = Runtime.getRuntime().exec(cmd.toString());
            HtmlToPdfInterceptor error = new HtmlToPdfInterceptor(proc.getErrorStream());
            HtmlToPdfInterceptor output = new HtmlToPdfInterceptor(
                    proc.getInputStream());
            error.start();
            output.start();
            proc.waitFor();
            System.out.println("HTML2PDF成功，参数---html路径：{},pdf保存路径 ：{}"+new Object[] {srcPath, destPath });
        } catch (Exception e) {
            System.out.println(("HTML2PDF失败，srcPath地址：{},错误信息：{}"));
            result = false;
        }
        return result;
    }

    public static void main(String[] args) {
        String htmlPath = "http://222.92.108.195:8082/TableToPdf/1579488/1579488.html";
        String pdfPath = "D:\\test\\testpdf2.pdf";
        HtmlToPdf.convert(htmlPath, pdfPath );
    }

}
