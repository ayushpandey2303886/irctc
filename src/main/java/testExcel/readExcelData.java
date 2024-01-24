package testExcel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class readExcelData {
	/*storing the input in a list of list -each list for 1 row*/
	public static ArrayList<ArrayList<String>> readExcelData() throws IOException {
	FileInputStream file=new FileInputStream(System.getProperty("user.dir")+"/src/test/resources/Book1.xlsx");
	XSSFWorkbook workbook=new XSSFWorkbook(file);
	XSSFSheet sheet=workbook.getSheet("Sheet1");  //workbook.getSheetAt(0);
	/*getting the row count and cell count*/
	int totalrows=sheet.getLastRowNum();
	int totalcells=sheet.getRow(0).getLastCellNum();

	/*create a list of list and adding the values from the excel to the list*/
	ArrayList<ArrayList<String>> list= new ArrayList<ArrayList<String>>();
	for(int r=1;r<=totalrows;r++)
	{
		XSSFRow currentRow=sheet.getRow(r);
		ArrayList<String> li=new ArrayList<String>();
		for(int c=1;c<totalcells;c++)
		{
			String value=currentRow.getCell(c).toString();
			li.add(value);
		}
		list.add(li);
	}
	return list;
	}
}

