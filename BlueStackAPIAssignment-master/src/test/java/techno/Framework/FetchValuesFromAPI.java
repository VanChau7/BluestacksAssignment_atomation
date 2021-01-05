package techno.Framework;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * This class fetches the values from API using rest assured libraries and
 * compare the values fetched from API with the UI and calculate the variance
 * percentage. When the variance percentage is greater than the given range it
 * throws 'Illegalstate exception'
 * 
 * @author User
 *
 */
public class FetchValuesFromAPI extends FetchValuesfromUI {
	static Map<String, String> apiTemperatures;
	static ArrayList<String> cities;
	static int variance;

	/**
	 * This method passes the given query parameters and generate the response
	 * depending on the query parameters passed
	 */

	public static void hitAPI() {
		RestAssured.given().queryParam("q", "Delhi").queryParam("appid", "acbd12188d900620d68326ce8efd77fb").when()
				.get("http://api.openweathermap.org/data/2.5/weather").then().log().all();
	}

	/**
	 * This method takes the parameter as city and get the weather details of
	 * that specific city if the response status is success.
	 * 
	 * @param city
	 */
	public static void getWeatherData(String city) {

		RestAssured.baseURI = "http://api.openweathermap.org/data/2.5/";

		RequestSpecification request = RestAssured.given();

		Response response = request.queryParam("appid", "acbd12188d900620d68326ce8efd77fb").queryParam("q", city)
				.queryParam("units", "metric").get("/weather");

		response.asString();

		int status = response.getStatusCode();

		if (status == 200) {
			System.out.println("Weather data for " + city + " is below");
			System.out.println(response.path("weather"));

		} else
			System.out.println(city + " is wrong city name in the input json");
	}

	//////////////////////// PHASE 3 ////////////////////

	/**
	 * This function reads the temperature from API by reading the cities from
	 * JSON file and storing them in array list. After reading the cities it
	 * stores the respective temperature in Linkedhashmap with key as city and
	 * value as temp.
	 * 
	 * This function internally calls private method getTemperature() to get the
	 * temperature of each city.
	 * 
	 * @return: map of city and temperature
	 */
	public static Map<String, String> readApiTemperatures() {
		cities = new ArrayList<String>(readInputJson());
		apiTemperatures = new LinkedHashMap<String, String>();
		for (int counter = 0; counter < cities.size(); counter++) {
			String city = "";
			city = cities.get(counter);
			apiTemperatures.put(city, (getTemperature(city)).toString());

		}
		return apiTemperatures;
	}

	/**
	 * This function get the temperature of the city passed as parameter. This
	 * is a private function and is used by internall methods only.
	 * 
	 * @param city
	 * @return
	 */

	private static Object getTemperature(String city) {
		Object temp = null;
		try {

			RestAssured.baseURI = "http://api.openweathermap.org/data/2.5/";

			RequestSpecification request = RestAssured.given();

			Response response = request.queryParam("appid", "acbd12188d900620d68326ce8efd77fb").queryParam("q", city)
					.queryParam("units", "metric").get("/weather");

			String jsonString = response.asString();
			// System.out.println("Response " + jsonString);
			int status = response.getStatusCode();

			if (status == 200) {
				System.out.println("Temperature for API for city " + city+" "+ response.path("main.temp"));
				return response.path("main.temp");
			} else
				System.out.println(city + " is wrong city name in the input json");

		} catch (Exception e) {
			System.out.println("Exception " + e);
			return temp;
		}
		return "NA";
	}

	private static int readVarianceFromJSON() {
		JSONParser parser = new JSONParser();

		Object obj = null;
		try {
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
		int variance = Integer.parseInt(jsonObject.get("Variance").toString());

		System.out.println(variance);
		return variance;
	}

	/**
	 * This methods fetches the temperature from UI and API and compares them.
	 * It calculates the percentage variance and if variance is greater than
	 * given range of JSON file, it throws exception or else it mentions the
	 * variance percentage.
	 * 
	 * @throws InterruptedException
	 */

	public static void matchUiAndAPI() throws InterruptedException {

		variance = readVarianceFromJSON();
		System.out.println("Value of variance in JSON file is "+  variance);
		for (String citykey : uiTemperatures.keySet()) {

			float ui_temperature, api_temperature;
			String api_temp = apiTemperatures.get(citykey);
			if (api_temp != null) {
				// fetching the temperatures from UI and converting them into float.
				ui_temperature = Float.parseFloat(uiTemperatures.get(citykey));
				System.out.println("UI temp for " + citykey + " " + ui_temperature);
				// Fetching values from API and converting them into float
				api_temperature = Float.parseFloat(apiTemperatures.get(citykey));
				System.out.println("API temp for " + citykey + " " + api_temperature);

				// Calculate the variance percentage between API and UI
				// temperature and pring the same
				float actualVariance = ((ui_temperature - api_temperature) / ui_temperature) * 100;
				System.out.println("% Variance is " + actualVariance);
				// comparing if actualvariance lies in the range given in JSON
				// file it prints the data or else it throws a matcher
				// exception.
				if (actualVariance <= variance) {
					System.out.println("City " + citykey + "is in variance range with variance = " + actualVariance);
				} else
					throw new IllegalStateException("%Variance out of range for city : " + citykey);

			}

		}
	}
}
