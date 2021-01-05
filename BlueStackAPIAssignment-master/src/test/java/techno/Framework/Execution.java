package techno.Framework;

public class Execution {

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		FetchValuesfromUI ui = new FetchValuesfromUI();
		FetchValuesFromAPI api = new FetchValuesFromAPI();
		//Phase 1 : UI automation
		ui.storecitiesInList();
		ui.launch_browser();
		try {
			ui.searchCity();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ui.quitBrowser();
		try {
			// PHASE 2: automated current weather data of any given city
			api.getWeatherData("Mumbai");
			
			//PHASE 3: Created a comparator to calculate variance percentage.
			api.readApiTemperatures();
			api.matchUiAndAPI();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
