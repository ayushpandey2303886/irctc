package MiniProject;
import testExcel.*;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;


public class IRCTC {

	WebElement todayDate;
	static ArrayList<ArrayList<String>> list;
//	removing pop up 
    public void removePopUp(WebDriver driver) throws InterruptedException
    {

//    	waiting for the pop up to appear
    	Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(30))           // Maximum wait time
                .pollingEvery(Duration.ofSeconds(2))           // Polling interval
                .ignoring(NoSuchElementException.class);       // Ignore specific exceptions during polling
        
    	WebElement element =  wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[9]/div/div/button")));
//      close the pop up by clicking on the cross button
    	JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("arguments[0].click();", element);
        
    }
    public void fillJourneyDetails(WebDriver driver , int i) throws InterruptedException
    {
        /*Retrieving the input data from excel sheet*/
    	String source=list.get(i).get(1);
    	String destination=list.get(i).get(2);
    	String expectedSource=list.get(i).get(3);
    	String expectedDestination=list.get(i).get(4);
    	WebElement fromStation=driver.findElement(By.id("stationFrom"));

        fromStation.sendKeys(source);
        
        /*This sleep statement is used because we have to wait for the origin list to update 
        according to the input*/
        Thread.sleep(4000);
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(50))           // Maximum wait time
                .pollingEvery(Duration.ofSeconds(5))           // Polling interval
                .ignoring(NoSuchElementException.class);       // Ignore specific exceptions during polling
             
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'"+expectedSource+"')]")));
        Actions action = new Actions(driver);
        /*using action class for clicking the desired origin*/
        action.moveToElement(element).click().perform();
        /*sending the input to the destination input box*/
        driver.findElement(By.id("stationTo")).sendKeys(destination);
        /*adding sleep so that the list could update*/
        Thread.sleep(3000);
        WebElement elemet = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'"+expectedDestination+"')]")));
        action.moveToElement(elemet).click().perform();

        /*the following code is used for selecting todays date from the date selector*/
        todayDate=driver.findElement(By.xpath("//span[@class=\"act active-red\"]"));
        todayDate.click();
        WebElement travelClass=driver.findElement(By.id("Traveller-Economy"));
        travelClass.click();

        /*the following code is used for selecting the travel class from the class selector .
         there is a list of classes from which we have to choose the required one*/
        WebElement chooseClass=driver.findElement(By.id("travelClass"));
        chooseClass.click();
        List<WebElement>travelClassList=driver.findElements(By.xpath("//select[@id='travelClass']/option"));
        String userClass=list.get(i).get(5);
        for(WebElement it: travelClassList)
        {
        	if(it.getText().equals(userClass))
        	{
        		it.click();
        		break;
        	}
        }
        
    }
    /*clicking on the search button*/
    public void search(WebDriver driver)
    {
    	 WebElement searchButton=driver.findElement(By.xpath("//button[contains(text(),'Search')]"));
         searchButton.click();
    }
    /*getting the flight details for the input by adding all the flights in a list and using a map to
      count for all the specific airlines*/
    public int getFlightDetails(WebDriver driver)
    {
    	List<WebElement> flightList;
    	try {
    	flightList=driver.findElements(By.xpath("//div[2]/b"));
    	}
    	catch(NoSuchElementException e) {
    		System.out.println("NO flights available");
    		return 0;
    		
    	}
    	
    	if(flightList.size()==0)
    	{
    		WebElement ele=driver.findElement(By.xpath("//div[@class='right-searchbarbtm']/p"));
    		System.out.println(ele.getText());
    		return 0;
    	}
        Map<String ,Integer> flightCount=new HashMap<String, Integer>();
        for(WebElement it:flightList)
        {
        	flightCount.put(it.getText(), flightCount.getOrDefault(it.getText(), 0) + 1);
        }
        for (Map.Entry<String, Integer> entry : flightCount.entrySet()) {
            System.out.println("Name: " +entry.getKey() +"       " +"Number of flights: "+entry.getValue());
        }
        return flightList.size();
    }

	public static void main(String[] args) throws InterruptedException, IOException {
		try {
		list=readExcelData.readExcelData();
		IRCTC obj=new IRCTC();
	
		for(int i=0;i<list.size();i++) {
			
//		select the browser
		String browserName=list.get(i).get(0);
		
//		driver setup
        WebDriver driver=driverSetup.getWebDriver(browserName);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        String link="https://www.air.irctc.co.in/";
        driver.get(link);
        driver.manage().window().maximize();
        
//      remove the popup that appears when we open the website
        obj.removePopUp(driver);
        
//      Fill the journey details that include source, destination, date, class  
        obj.fillJourneyDetails(driver,i);
        
//      click on the search button
        obj.search(driver);
        
//      get the flight details for the input
        int noOfFlights=obj.getFlightDetails(driver);       
        
//      capture the result window and store it 
        obj.resultScreenShot(driver,i);
        
//      validate the date and source

        obj.validation(driver,i,noOfFlights);

//      close the window/tab
        obj.closeWindows(driver);
        System.out.println("closed");

		}
		}
		catch(Exception e) {
			System.out.println( e.toString());
		}
	}
	
	/*close the driver*/
	private void closeWindows(WebDriver driver) {
		driver.close();
	}
	/*validating the source and date*/
	private void validation(WebDriver driver,int i , int noOfFlights) throws InterruptedException, IOException{
		/* source validation
		 * destination validation
		 * checking all the flights source
		 * */

        for(int j=1;j<=noOfFlights;j++)
        {
    		excelUtils.createRow(System.getProperty("user.dir")+"/src/test/resources/Book1.xlsx","Sheet2",j,3,"");
    		excelUtils.setCellData(System.getProperty("user.dir")+"/src/test/resources/Book1.xlsx","Sheet2",j,0,Integer.toString(j));

        	String airLine=driver.findElement(By.xpath("(//div[@class=\"right-searchbarbtm-in\"]/div[1]//b)["+j+"]")).getText();
    		excelUtils.setCellData(System.getProperty("user.dir")+"/src/test/resources/Book1.xlsx","Sheet2",j,1,airLine);
    		String planeCode=driver.findElement(By.xpath("(//div[@class=\"right-searchbarbtm-in\"]/div[1]//span)["+j+"]")).getText();
    		excelUtils.setCellData(System.getProperty("user.dir")+"/src/test/resources/Book1.xlsx","Sheet2",j,2,planeCode);
    		

        	if(driver.findElement(By.xpath("(//div[@class=\"right-searchbarbtm-in\"]/div[2]//span)["+j+"]")).getText().contains(list.get(i).get(3)) )
        	{
        		excelUtils.setCellData(System.getProperty("user.dir")+"/src/test/resources/Book1.xlsx","Sheet2",j,3,"Valid Source");
        	}
        	else
        	{
        		excelUtils.setCellData(System.getProperty("user.dir")+"/src/test/resources/Book1.xlsx","Sheet2",j,3,"InValid Source");
        	}
        	if(driver.findElement(By.xpath("(//div[@class=\"right-searchbarbtm-in\"]/div[3]//span)["+j+"]")).getText().contains(list.get(i).get(4)) )
        	{
        		excelUtils.setCellData(System.getProperty("user.dir")+"/src/test/resources/Book1.xlsx","Sheet2",j,4,"Valid Destination");
        	}
        	else
        	{
        		excelUtils.setCellData(System.getProperty("user.dir")+"/src/test/resources/Book1.xlsx","Sheet2",j,4,"InValid Destination");
        	}
        }
        
        
        /*date validation using the date from the system*/
        WebElement d=driver.findElement(By.xpath("//input[@id='originDate']"));
		String date=(String)((JavascriptExecutor)driver).executeScript("return arguments[0].value;",d);
        LocalDate currentDate =LocalDate.now();
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String todaysDate=currentDate.format(formatter);
        if(todaysDate.equals(date))
        {
        	System.out.println("valid date");
        	excelUtils.setCellData(System.getProperty("user.dir")+"/src/test/resources/Book1.xlsx","Sheet1",i+1,7,"Valid Date");
        }
        else
        {
        	System.out.println("Invalid date");
        	excelUtils.setCellData(System.getProperty("user.dir")+"/src/test/resources/Book1.xlsx","Sheet1",i+1,7,"InValid Date");

        }
	}
	
	
	
	/*taking the screenshot of the output and storing it*/
	public void resultScreenShot(WebDriver driver,int i) {

		File screenshotFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		try {
            // Specify the path where you want to save the screenshot
            FileUtils.copyFile(screenshotFile, new File(System.getProperty("user.dir")+"/src/test/resources/ScreenShot/"+"result.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}

