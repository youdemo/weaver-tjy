package sy;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.jacob.com.ComThread;

public class TestWaterExcel {
    //测试功能
        public static void main(String[] argv) {
            TestWaterExcel d = TestWaterExcel.getInstance();
            try {
                if (d.initExcelApp()) {
                    Date date=Calendar.getInstance().getTime();
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                    String dateStr=sdf.format(date);
                    String imgPath="D:\\test\\222.png";
                    String filePath="D:\\test\\aaa.xlsx";

                    d.openWorkbook(filePath,true,false);
                    d.getCurrentSheet();
                    d.setFooter(dateStr);//页脚
//                  d.setHeader(dateStr);//页眉
                    d.setWater(imgPath);//设置图片水印

//                  Dispatch curSheet=d.getSheetByIndex(2);//获取第二张表
//                  d.setValue(curSheet, "A1", "value", "test2");//往第2张表的A1中写入

                    d.closeDocument();
                } else{
                    System.out.println("打开对象失败！");
                }
            } catch (Exception e) {
                System.out.println(e);
            }finally{
                d.closeWordObj();
            }
        }


    public TestWaterExcel() {}
    private static TestWaterExcel instance;
    private ActiveXComponent excelApp;//Word对象
    private Dispatch workbooks  = null;//存储所有的文档
    private Dispatch workbook  = null;//用于存储一个文档：比如新增一个文档时返回，新增的文档
    private Dispatch sheets = null;// 获得sheets集合对象
    private Dispatch currentSheet = null;// 当前sheet


    public final static synchronized TestWaterExcel getInstance() {
        if (instance == null){
            instance = new TestWaterExcel();
        }
        return instance;
    }

    private void initComponents(){
        workbook = null;
        currentSheet = null;
        sheets = null;
    }

    /**
     * 初始化Excel对象*/
    public boolean initExcelApp() {
        boolean retFlag = false;
        initComponents();
        ComThread.InitSTA();//初始化线程、使用结束后要关闭线程
        try {
            excelApp = new ActiveXComponent("Excel.Application");//初始化表
            excelApp.setProperty("Visible", new Variant(true));//配置启动word时是显示执行还是隐式执行
            workbooks  = excelApp.getProperty("Workbooks").toDispatch();// 获取word所有文档对象
            retFlag = true;
        } catch (Exception e) {
            retFlag = false;
            e.printStackTrace();
        }
        return retFlag;
    }

    /**打开一个workbook*/
    public void openWorkbook(String filename,boolean visible, boolean readonly) {
        if (this.workbook != null) {
            this.closeDocument();
        }
        workbook = Dispatch.invoke(workbooks,"Open",Dispatch.Method,
                new Object[] { filename, new Variant(visible),new Variant(readonly) },// 是否以只读方式打开
                new int[1]).toDispatch();
    }

    /**
    * 得到workbook的名字
    * @return
    */
    public String getWorkbookName() {
        if(workbook==null)
            return null;
        return Dispatch.get(workbook, "name").toString();
    }

    /**
    * 得到sheets的集合对象
    * @return
    */
    public Dispatch getSheets() {
        if(sheets==null)
            sheets = Dispatch.get(workbook, "sheets").toDispatch();
        return sheets;
    }

    /**
    * 添加新的工作表(sheet)，（添加后为默认为当前激活的工作表）
    */
    public Dispatch addSheet() {
        return Dispatch.get(sheets,"add").toDispatch();
    }

    /**
    * 得到当前sheet
    * @return
    */
    public Dispatch getCurrentSheet() {
        currentSheet = Dispatch.get(workbook, "ActiveSheet").toDispatch();
        return currentSheet;
    }

    /**
    * 得到当前sheet的名字
    * @return
    */
    public String getCurrentSheetName() {
        return Dispatch.get(getCurrentSheet(), "name").toString();
    }

    /**
    * 通过sheetName得到sheet
    * @param name sheetName
    * @return
    */
    public Dispatch getSheetByName(String name) {
        return Dispatch.invoke(getSheets(),"Item",Dispatch.Get,
                new Object[]{name}, new int[1]).toDispatch();
    }

    /**
    * 通过工作表索引得到工作表(第一个工作簿index为1)
    * @param index
    * @return sheet对象
    */
    public Dispatch getSheetByIndex(Integer index) {
        return Dispatch.invoke(getSheets(), "Item", Dispatch.Get, 
                new Object[]{index}, new int[1]).toDispatch();
    }

    /**
    * 得到sheet的总数
    * @return
    */
    public int getSheetCount() {
        int count = Integer.parseInt(Dispatch.get(getSheets(), "count").toString());
    return count;

    }

    /**
    * 单元格写入值
    * @param sheet 被操作的sheet
    * @param position 单元格位置，如：C1
    * @param type 值的属性 如：value
    * @param value

    */
    public void setValue(Dispatch sheet, String position, String type, Object value) {
        Dispatch cell = Dispatch.invoke(sheet, "Range",Dispatch.Get, 
                new Object[] { position }, new int[1]).toDispatch();
        Dispatch.put(cell, type, value);
    }

    /**
    * 单元格读取值
    * @param position 单元格位置，如： C1
    * @param sheet
    * @return
    */
    public Variant getValue(String position, Dispatch sheet) {
        Dispatch cell = Dispatch.invoke(sheet, "Range", Dispatch.Get,
                new Object[] { position }, new int[1]).toDispatch();
        Variant value = Dispatch.get(cell, "Value");
        return value;
    }

    /**
     * 设置页脚信息
     */
    public void setFooter(String foot) {
        currentSheet=this.getCurrentSheet();
        Dispatch PageSetup=Dispatch.get(currentSheet,"PageSetup").toDispatch();
        //字体:&10 [注意空格],粗体:&B,颜色:&KFF0000,日期：&D，时间：&T
        Dispatch.put(PageSetup,"CenterFooter",
            "&13 &B&KF08080&D&");
    } 

    /**
     * 获取页脚信息
     */ 
    public String getFooter() {
        currentSheet=this.getCurrentSheet();
        Dispatch PageSetup=Dispatch.get(currentSheet,"PageSetup").toDispatch();
        return Dispatch.get(PageSetup,"CenterFooter").toString();
    } 

    /**
     * 设置页眉信息
     */
    public void setHeader(String header) {
        currentSheet=this.getCurrentSheet();
        Dispatch PageSetup=Dispatch.get(currentSheet,"PageSetup").toDispatch();
        Dispatch.put(PageSetup,"CenterHeader",header);
    } 

    /**
     * 获取页眉信息
     */
    public String getHeader() {
        currentSheet=this.getCurrentSheet();
        Dispatch PageSetup=Dispatch.get(currentSheet,"PageSetup").toDispatch();
        return Dispatch.get(PageSetup,"CenterHeader").toString();
    } 

    /**
     * 通过页眉设置水印
     */
    public void setWater(String filePath) {
        currentSheet=this.getCurrentSheet();
        Dispatch PageSetup=Dispatch.get(currentSheet,"PageSetup").toDispatch();
        Dispatch.put(PageSetup,"CenterHeader",
            "\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n&G");//显示图片对象，\n\r是换行，&G表示显示图片

        Dispatch header=Dispatch.get(PageSetup,"CenterHeaderPicture").toDispatch();//获取图片对象
        Dispatch.put(header,"Filename",filePath);
        Dispatch.put(header,"Height",105);
        Dispatch.put(header,"Width",170);
        Dispatch.put(header,"Brightness",0.75);//设置亮度
        Dispatch.put(header,"Contrast",0.25);//设置对比
    } 


    /**
    * 工作簿另存为
    * @param fileNewPath 另存为的路径
    */
    public void SaveAs(String fileNewPath){
    Dispatch.invoke(workbook, "SaveAs", Dispatch.Method,
        new Object[] { fileNewPath,new Variant(44) }, new int[1]);
    }

    /**
     * 保存并关闭当前文档
     */
    public void closeDocument() {
        if (workbook  != null) {
            Dispatch.call(workbook , "Save");//保存
            Dispatch.call(workbook , "Close", new Variant(0));//关闭
            workbook  = null;
        }
    }

    /**
     * 释放资源*/
    public void closeWordObj() {
        if(excelApp!=null){
            excelApp.invoke("Quit", new Variant[] {});// 关闭文件
            excelApp=null;
        }
        workbooks = null;
        ComThread.Release();// 释放线程
    }


}
