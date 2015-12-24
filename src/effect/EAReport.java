/*
 * $Id: $
 */
package effect;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

/**
 * $Id: $
 */
public class EAReport
{
    private final HSSFWorkbook WORKBOOK = new HSSFWorkbook();
    
    private final HSSFCellStyle STYLE_VL   = WORKBOOK.createCellStyle();
    private final HSSFCellStyle STYLE_VR  = WORKBOOK.createCellStyle();
    private final HSSFCellStyle STYLE_VC = WORKBOOK.createCellStyle();

    private final HSSFCellStyle STYLE_HC  = WORKBOOK.createCellStyle();
    
    private final HSSFSheet SHEET_SUMMERY = WORKBOOK.createSheet("Summery");
    private final HSSFSheet SHEET_XFM     = WORKBOOK.createSheet("map:XFM-Controller");
    private final HSSFSheet SHEET_JAVA    = WORKBOOK.createSheet("map:Java-Java");
    private final HSSFSheet SHEET_BD      = WORKBOOK.createSheet("map:BD-JoltService");
    private final HSSFSheet SHEET_JOLT    = WORKBOOK.createSheet("list:Jolt Service");
    private final HSSFSheet SHEET_ERROR   = WORKBOOK.createSheet("Error");

    public EAReport()
    {
        super();
        setColumnWidth(SHEET_SUMMERY, 0, 5);
        setColumnWidth(SHEET_SUMMERY, 1, 20);
        setColumnWidth(SHEET_SUMMERY, 2, 90);
        
        setColumnWidth(SHEET_XFM, 0, 50);
        setColumnWidth(SHEET_XFM, 1, 45);
        setColumnWidth(SHEET_XFM, 2, 50);

        setColumnWidth(SHEET_JAVA, 0, 60);
        setColumnWidth(SHEET_JAVA, 1, 60);

        setColumnWidth(SHEET_BD, 0, 50);
        setColumnWidth(SHEET_BD, 1, 15);
        setColumnWidth(SHEET_BD, 2, 50);

        setColumnWidth(SHEET_JOLT, 0, 20);

        setColumnWidth(SHEET_ERROR, 0, 120);
        
        STYLE_VL.setBorderTop   (HSSFCellStyle.BORDER_THIN);
        STYLE_VL.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        STYLE_VL.setBorderLeft  (HSSFCellStyle.BORDER_THIN);
        STYLE_VL.setBorderRight (HSSFCellStyle.BORDER_THIN);
        STYLE_VL.setAlignment   (HSSFCellStyle.ALIGN_LEFT);

        STYLE_VR.setBorderTop   (HSSFCellStyle.BORDER_THIN);
        STYLE_VR.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        STYLE_VR.setBorderLeft  (HSSFCellStyle.BORDER_THIN);
        STYLE_VR.setBorderRight (HSSFCellStyle.BORDER_THIN);
        STYLE_VR.setAlignment   (HSSFCellStyle.ALIGN_RIGHT);
        
        STYLE_VC.setBorderTop   (HSSFCellStyle.BORDER_THIN);
        STYLE_VC.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        STYLE_VC.setBorderLeft  (HSSFCellStyle.BORDER_THIN);
        STYLE_VC.setBorderRight (HSSFCellStyle.BORDER_THIN);
        STYLE_VC.setAlignment   (HSSFCellStyle.ALIGN_CENTER);
                
        STYLE_HC.setBorderTop          (HSSFCellStyle.BORDER_THIN);
        STYLE_HC.setBorderBottom       (HSSFCellStyle.BORDER_THIN);
        STYLE_HC.setBorderLeft         (HSSFCellStyle.BORDER_THIN);
        STYLE_HC.setBorderRight        (HSSFCellStyle.BORDER_THIN);
        STYLE_HC.setAlignment          (HSSFCellStyle.ALIGN_CENTER);

        STYLE_HC.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        STYLE_HC.setFillPattern        (HSSFCellStyle.SOLID_FOREGROUND);
    }
    
    private void setColumnWidth(HSSFSheet sheet, int index, int width)
    {
        sheet.setColumnWidth((short)index, (short)(width * 256));
    }
    
    public void write() throws Exception
    {
        writeReportJob();
        writeReportJava();
        writeReportJolt();
        writeSummery();
        writeToFile();
    }
    
    public void writeError(String message)
    {
        setValuesNext(SHEET_ERROR, 0, STYLE_VL, message);
    }

    private void writeToFile() throws Exception
    {
        FileOutputStream fout = null;
        try 
        {
            fout = new FileOutputStream("effect_"+EAUtils.now("yyyyMMdd_HHmm_ss")+".xls");
            WORKBOOK.write(fout);
            fout.close();
        }
        finally
        {
            try { if (fout != null) fout.close (); } catch (Exception ioe) {}
        }  
    }
     
    private void writeReportJob()
    {
        setValues(SHEET_XFM, 0, 0, STYLE_HC, new String[] {"XFM", "Controller", "Class"});
        
        Iterator i = EAController.getControllerIterator();
        while (i.hasNext())
        {
            Object key = i.next();
            EAController controller = EAController.getController(key);
            for(int j = 0; j < controller.xfms.size(); j++) {
                setValuesNext(SHEET_XFM, 0, STYLE_VL,
                    new String[] {(String)controller.xfms.get(j), controller.name, controller.type});
            }
        }
    }

    private void writeReportJolt()
    {
        setValues(SHEET_JOLT, 0, 0, STYLE_HC, new String[] {"Jolt 서비스"});
        
        Iterator i = EAJolt.getIterator();
        while (i.hasNext())
        {
            String service = (String)i.next();
            setValuesNext(SHEET_JOLT, 0, STYLE_VL, service);
        }
    }

    private void writeReportJava()
    {
        setValues(SHEET_JAVA, 0, 0, STYLE_HC, new String[] {"Java", "Java"});
        setValues(SHEET_BD  , 0, 0, STYLE_HC, new String[] {"BD", "Jolt 서비스", "비고"});
        
        Iterator iterator = EAJava.getIterator();
        while(iterator.hasNext())
        {
            EAJava java = (EAJava)iterator.next();
        
            //컨트롤러와 BD 사이의 연관관계를 기록
            if (java.isUserDefined())
            {
                List dependancies = java.listDependancies();
                int count = 0;
                for(int i = 0; i < dependancies.size(); i++) 
                {
                    EAJava dep = (EAJava)dependancies.get(i);
                    if (dep.isUserDefined()) {
                        setValuesNext(SHEET_JAVA, 0, STYLE_VL, java.getName(), dep.getName());
                        count++;
                    }
                }
                if (count == 0) 
                {
                    setValuesNext(SHEET_JAVA, 0, STYLE_VL, java.getName(), "(없음)");
                }
            }
            
            //BD와 Jolt 사이의 연관관계를 기록. 이것은 BD일 경우에만 기록한다.
            if (java.isBD()) 
            {
                if (java.listJoltCalls().size() == 0) {
                    setValuesNext(SHEET_BD, 0, STYLE_VL, java.getName(), "", "(없음)");
                }
                else 
                {
                    List joltCalls = java.listJoltCalls();
                    for(int i = 0; i < joltCalls.size(); i++) 
                    {
                        String[] sa = ((String)joltCalls.get(i)).split("\t"); 
                        String[] data = new String[3];
                        data[0] = java.getName();
                        for(int j = 0; j < sa.length; j++)
                        {
                            data[1 + j] = sa[j];
                        }
                        setValuesNext(SHEET_BD, 0, STYLE_VL, data);
                    }
                }        
            }
        }
    }
    
    private int countJava()
    {
        int counter = 0;
        Iterator i = EAJava.getIterator();
        while(i.hasNext())
        {
            EAJava java = (EAJava)i.next();
            if (java.isUserDefined()) counter++;
        }
        return counter;
    }
    
    private int countControllerFromJava()
    {
        int counter = 0;
        Iterator i = EAJava.getIterator();
        while(i.hasNext())
        {
            EAJava java = (EAJava)i.next();
            if (java.isController()) counter++;
        }
        return counter;
    }

    private int countBdFromJava()
    {
        int counter = 0;
        Iterator i = EAJava.getIterator();
        while(i.hasNext())
        {
            EAJava java = (EAJava)i.next();
            if (java.isBD()) counter++;
        }
        return counter;
    }
    
    private int countDaoFromJava()
    {
        int counter = 0;
        Iterator i = EAJava.getIterator();
        while(i.hasNext())
        {
            EAJava java = (EAJava)i.next();
            if (java.isDAO()) counter++;
        }
        return counter;
    }
    
    private int countJavaWhatCallsJolt()
    {
        int counter = 0;
        Iterator i = EAJava.getIterator();
        while(i.hasNext())
        {
            EAJava java = (EAJava)i.next();
            if (java.hasActualJoltCall()) counter++;
        }
        return counter;
    }
    
    private int countBdWhatCallsJolt()
    {
        int counter = 0;
        Iterator i = EAJava.getIterator();
        while(i.hasNext())
        {
            EAJava java = (EAJava)i.next();
            if (java.isBD() && java.hasActualJoltCall()) counter++;
        }
        return counter;
    }    
    
    private int countJavaParseError()
    {
        int counter = 0;
        Iterator i = EAJava.getIterator();
        while(i.hasNext())
        {
            EAJava java = (EAJava)i.next();
            if (java.isParseFailed()) counter++;
        }
        return counter;
    }  
    
    private void writeSummery()
    {
        HSSFCellStyle[] styles = new HSSFCellStyle[] {STYLE_HC, STYLE_VR, STYLE_VL};
        
        setValuesNext(SHEET_SUMMERY, 0, new HSSFCellStyle[] {STYLE_HC, STYLE_HC, STYLE_HC}, "ID", "데이터", "설명");
        setValuesNext(SHEET_SUMMERY, 0, styles, null, EAUtils.now(), "작성일시");
        setValuesNext(SHEET_SUMMERY, 0, styles, "01", EAXfm.count(), "XFM 총 갯수");
        setValuesNext(SHEET_SUMMERY, 0, styles, "02", countXfmWhatCallsController(), "Controller 를 호출하는 XFM 갯수");
        setValuesNext(SHEET_SUMMERY, 0, styles, "03", countControllersFromXfm(),"XFM 이 호출하는 Controller 갯수(중복제거)");
        setValuesNext(SHEET_SUMMERY, 0, styles, "04", countControllersFromJob(), "Job XML 에 정의된 컨트롤러 갯수");
        setValuesNext(SHEET_SUMMERY, 0, styles, "05", EAController.controllerFindError, "XFM 이 호출하는 Controller 를 Job XML 에서 찾을수 없는 건수");
        setValuesNext(SHEET_SUMMERY, 0, styles, "06", EAController.xfmParseError,"XFM 이 XML 규격을 준수하지 않아 분석이 불가능한 건수");
        setValuesNext(SHEET_SUMMERY, 0, styles, "07", countJava(), "Java 모듈 갯수");
        setValuesNext(SHEET_SUMMERY, 0, styles, "08", countControllerFromJava(),  "Java 모듈중 Controller 갯수");
        setValuesNext(SHEET_SUMMERY, 0, styles, "09", countBdFromJava(), "Java 모듈중 BD 갯수");
        setValuesNext(SHEET_SUMMERY, 0, styles, "10", countJavaWhatCallsJolt(), "Jolt 서비스를 호출하는 Java 갯수");
        setValuesNext(SHEET_SUMMERY, 0, styles, "11", countBdWhatCallsJolt(), "Jolt 서비스를 호출하는 BD 갯수");
        setValuesNext(SHEET_SUMMERY, 0, styles, "12", countJoltServicesFromBD(), "BD 에서 호출하는 Jolt 서비스 갯수(중복제거)");        
        setValuesNext(SHEET_SUMMERY, 0, styles, "13", EAJolt.count(), "jrepository 에 명시된 서비스 갯수");
        setValuesNext(SHEET_SUMMERY, 0, styles, "14", countDaoFromJava(), "Java 모듈중 DAO 갯수");
        setValuesNext(SHEET_SUMMERY, 0, styles, "15", countJavaParseError(), "Java 모듈 분석에 실패한 건수");
        //setValuesNext(SHEET_SUMMERY, 0, styles, "15", calcJoltServiceAnalSuccessRate(), "BD 에서 Jolt 서비스를 호출하는 것으로 분석되어 실제 Jolt 서비스명을 알아내는데 성공한 비율");        
    }

    private int countControllersFromXfm()
    {
        int counter = 0;
        Iterator iterator = EAController.getControllerIterator();
        while (iterator.hasNext())
        {
            Object key = iterator.next();
            EAController controller = EAController.getController(key);
            if (controller.xfms.size() > 0) counter++;
        }
        return counter;        
    }
    
    private int countControllersFromJob()
    {
        return EAController.countControllers();
    }
    
    private int countXfmWhatCallsController()
    {
        List list = new ArrayList();
        Iterator iterator = EAController.getControllerIterator();
        while (iterator.hasNext())
        {
            Object key = iterator.next();
            EAController controller = EAController.getController(key);
            for(int i = 0; i < controller.xfms.size(); i++) 
            {
                List xfms = controller.xfms;
                for(int j = 0; j < xfms.size(); j++)
                {
                    if (!list.contains(xfms.get(j))) 
                    {
                        list.add(xfms.get(j));
                    }
                }
            }
        }
        return list.size();
    }
    
    private int countJoltServicesFromBD()
    {
        List all = new ArrayList();
        List list = listJoltCallsAnalizeSuccessed();
        for(int i = 0; i < list.size(); i++)
        {
            String service = (String)list.get(i); 
            if (service.indexOf('\t') > -1) {
                service = service.substring(0, service.lastIndexOf("\t"));
            }
            if (!all.contains(service)) all.add(service);
        }
        return all.size();
    }

    private int calcJoltServiceAnalSuccessRate()
    {   
        double value1 = listJoltCallsAnalizeSuccessed().size();
        double value2 = listJoltCallsAll().size();
        return (int)((value1 / value2) * 100);
    }
    
    private List listJoltCallsAll()
    {
        List result = new ArrayList();
        Iterator iterator = EAJava.getIterator();
        while(iterator.hasNext())
        {
            EAJava java = (EAJava)iterator.next();
            List list = java.listJoltCalls();
            result.addAll(list); 
        }
        return result;
    }
    private List listJoltCallsAnalizeSuccessed()
    {
        List result = new ArrayList();
        List list = listJoltCallsAll();
        for(int i = 0; i < list.size(); i++)
        {
            String service = (String)list.get(i); 
            if (!service.startsWith("\t"))
            {
                result.add(service);
            }
        }
        return result;
    }
    
    private void setValuesNext(HSSFSheet sheet, int colnum, HSSFCellStyle style, String value)
    {
        setValues(sheet, getRowNextIndex(sheet), colnum, style, new String[] {value}); 
    }

    private void setValuesNext(HSSFSheet sheet, int colnum, HSSFCellStyle[] style, String value)
    {
        setValues(sheet, getRowNextIndex(sheet), colnum, style, new String[] {value}); 
    }
    
    private void setValuesNext(HSSFSheet sheet, int colnum, HSSFCellStyle style, String value1, int value2, String value3)
    {
        setValues(sheet, getRowNextIndex(sheet), colnum, style, new String[] {value1, Integer.toString(value2), value3});        
    }

    private void setValuesNext(HSSFSheet sheet, int colnum, HSSFCellStyle[] style, String value1, int value2, String value3)
    {
        setValues(sheet, getRowNextIndex(sheet), colnum, style, new String[] {value1, Integer.toString(value2), value3});        
    }
    
    private void setValuesNext(HSSFSheet sheet, int colnum, HSSFCellStyle style, String value1, String value2)
    {
        setValues(sheet, getRowNextIndex(sheet), colnum, style, new String[] {value1, value2}); 
    }

    private void setValuesNext(HSSFSheet sheet, int colnum, HSSFCellStyle[] style, String value1, String value2)
    {
        setValues(sheet, getRowNextIndex(sheet), colnum, style, new String[] {value1, value2}); 
    }

    private void setValuesNext(HSSFSheet sheet, int colnum, HSSFCellStyle style, String value1, String value2, String value3)
    {
        setValues(sheet, getRowNextIndex(sheet), colnum, style, new String[] {value1, value2, value3}); 
    }

    private void setValuesNext(HSSFSheet sheet, int colnum, HSSFCellStyle[] style, String value1, String value2, String value3)
    {
        setValues(sheet, getRowNextIndex(sheet), colnum, style, new String[] {value1, value2, value3}); 
    }
    
    private void setValuesNext(HSSFSheet sheet, int colnum, HSSFCellStyle style, String[] values)
    {
        setValues(sheet, getRowNextIndex(sheet), colnum, style, values);
    }

    private void setValuesNext(HSSFSheet sheet, int colnum, HSSFCellStyle[] style, String[] values)
    {
        setValues(sheet, getRowNextIndex(sheet), colnum, style, values);
    }
    
    private void setValues(HSSFSheet sheet, int rownum,  int colnum, HSSFCellStyle style, String[] values)
    {
       HSSFCellStyle[] styles = new HSSFCellStyle[values.length];
       for (int i = 0; i < values.length; i++)
       {
           styles[i] = style;
       }
       setValues(sheet, rownum, colnum, styles, values);
    }
    
    private void setValues(HSSFSheet sheet, int rownum, int colnum, HSSFCellStyle[] styles, String[] values)
    {
        HSSFRow r = getRow(sheet, rownum); 
        for(int i = 0; i < values.length; i++)
        {
            getCell(r, colnum + i).setCellValue(values[i]); 
            getCell(r, colnum + i).setCellStyle(styles[i]);
        }
    }
    
    private HSSFCell getCell(HSSFRow row, int index)
    {
        HSSFCell c = row.getCell((short)index); 
        if (c == null ) c = row.createCell((short)index);
        c.setEncoding(HSSFCell.ENCODING_UTF_16);
        c.setCellStyle(STYLE_VL);
               
        return c;
    }
    
    private HSSFRow getRow(HSSFSheet s, int index)
    {
        HSSFRow r = s.getRow(index); 
        if (r == null) r = s.createRow(index);
        
        return r;
    }

    /**
     * 시트에 Row가 하나있으나 하나도 없으나 getLastRowNum() 은 항상 0을 리턴한다.
     * 그래서 시트에 Row 가 없을경우 Row Next 가 0이 되도록 하기 위해 0 번째 Row 가
     * null 인지 여부를 검사해야 함. 
     * @param s
     * @return
     */
    private int getRowNextIndex(HSSFSheet s)
    {
        int index = -1;
        if (s.getRow(0) != null)
            index = s.getLastRowNum();
        return index + 1;
    }
}
