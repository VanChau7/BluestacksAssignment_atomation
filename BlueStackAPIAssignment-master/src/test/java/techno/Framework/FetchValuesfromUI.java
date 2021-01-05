package techno.Framework;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * This class fetches the values from UI by reading the input.json file stored
 * in data folder of the framework and at end returning the hasmap with list of
 * cities and respective temperature.
 * 
 * storecitiesInList: Is storing the list of cities present in json file into a
 * array list readInputJson: Is reading the Json file using JSONParser class and
 * returning the list of cities launch_browser: Is launching the chrome browser
 * version 87 and initializing the dynamic waits. searchCity: Is search each and
 * every city present in json file. For all the correct cities it is storing the
 * values in linked hashmap and for incorrect city an exception is thrown and no
 * value is stored in map for that. main : main method is calling all the
 * methods and execution is begin from there.
 * 
 * @method : storecitiesInList, readInputJson, launch_browser, searchCity, main
 * @author User
 *
 */
public class FetchValuesfromUI {
	static WebDriver driver;
	static WebDriverWait wait;
	static List<String> cityList;
	static Map<String, String> uiTemperatures;

	/**
	 * This method reads the city from json file and stores it in arrayList
	 */

	public static void storecitiesInList() {

		cityList = new ArrayList<String>(readInputJson());
		System.out.println("List of cities is " + cityList);
	}

	/**
	 * This method uses JSONParser class to read the data from Json file stored
	 * in data folder. It throws the exception with message in case file is not
	 * present at given location
	 * 
	 * @return
	 */
	public static ArrayList<String> readInputJson() {
		
		JSONParser parser = new JSONParser();

		Object obj = null;
		try {
			//Setting connection with json file 
			obj = parser.parse(new FileReader("data/input.json"));
		} catch (FileNotFoundException e) {
			System.out.println("input file not found");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jsonObject = (JSONObject) obj;
		// Getting the list of city JSONObject and storing it in json array
		JSONArray citylist = (JSONArray) jsonObject.get("City");

		/*
		 * Iterator<JSONObject> iterator = citylist.iterator(); while
		 * (iterator.hasNext()) { System.out.println(iterator.next()); }
		 */
		return citylist;
	}

	/**
	 * This function launches the browser , maximize it and initialise dynamic
	 * wait which is used in further methods
	 */
	public static void launch_browser() {
		System.setProperty("webdriver.chrome.driver", "data/chromedriver.exe");
		// WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.get("https://weather.com/");
		driver.manage().window().maximize();
		wait = new WebDriverWait(driver, 20);

	}

	/**
	 * This method searches the city in UI and fetches the temperature. The city
	 * and temp are stored in map and is returned by the function.
	 * 
	 * @return
	 * @throws InterruptedException
	 */

	public static Map<String, String> searchCity() throws InterruptedException {
		uiTemperatures = new LinkedHashMap<String, String>();
		String temp = null;
		for (String city : cityList) {
			// Clearing the text field before entering values
			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#LocationSearch_input"))).clear();
			try {
				// Enter the city name in search box one by one
				wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#LocationSearch_input")))
						.sendKeys(city);

				wait.until(ExpectedConditions.visibilityOfElementLocated(
						By.xpath("//div[@id='LocationSearch_listbox']//button[contains(text(),'" + city + "')]")))
						.sendKeys(Keys.ENTER);
				// Fetching the temperature displayed in UI for specific city
				temp = wait
						.until(ExpectedConditions.visibilityOfElementLocated(By
								.xpath("//div[@class='CurrentConditions--primary--3xWnK']/span[@data-testid='TemperatureValue']")))
						.getText();
				// Since the temperature comes in degree so we are removing the
				// last character of string (degree circle) for easier
				// calculation
				temp = temp.substring(0, temp.length() - 1);
				System.out.println("Temperature from UI for city " + city + " " + temp);
				uiTemperatures.put(city, temp);
			} catch (Exception e) {
				e.printStackTrace();
				wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#LocationSearch_input"))).clear();

			}

		}
		System.out.println(
				"Values of correct cities are stored in Linkedhashmap, for incorrect city (NewYork) exception is thrown and nothing is stored in map for that. ");
		// Printing the map to see correct values
		for (String s : uiTemperatures.keySet()) {
			System.out.println(s + " " + uiTemperatures.get(s));
		}
		return uiTemperatures;
	}

	/**
	 * This methods closes all the instances of the browser once the execution
	 * is done
	 */
	public void quitBrowser() {
		driver.quit();
	}
}