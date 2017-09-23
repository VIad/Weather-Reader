package wr.service;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import wr.main.IOHandler;

public class OWMAPIConnectionTask {

	public static final String	API_KEY				= "????????????????????????????";//real API key hidden for obvious reasons

	public static final String	SERVICE_WEATHER		= "weather";
	public static final String	SERVICE_FORECAST	= "forecast";

	public static final String	SERVICE				= "weather";

	private final String		URL_WEATHER;

	public OWMAPIConnectionTask(final int city_id, final String service) {
		URL_WEATHER = "http://api.openweathermap.org/data/2.5/" + service + "?id=" + city_id + "&APPID=" + API_KEY;
	}

	public JSONObject readFromService() {
		try {
			return IOHandler.readJsonFromUrl(URL_WEATHER);
		} catch (IOException | JSONException e) {
			return null;
		}
	}

}
