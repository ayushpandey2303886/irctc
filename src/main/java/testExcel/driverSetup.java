package testExcel;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class driverSetup {
	public static WebDriver getWebDriver(String browserName)
	{
		if(browserName.equalsIgnoreCase("chrome"))
        	return new ChromeDriver();
        else if(browserName.equalsIgnoreCase("foxfire"))
        	return new FirefoxDriver();
        else
        	System.out.println("The Driver with name "+browserName+" is not available so the default broeser will get open");
        	return new ChromeDriver();
	}
}
