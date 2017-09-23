package wr.service;

import java.io.IOException;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import wr.main.IOHandler;

public class LocationModule {

	public static final String url = "http://ip-api.com/json";

	private JSONObject readJsonFromUrl() throws IOException, JSONException {
		return IOHandler.readJsonFromUrl(url);
	}

	public static JSONObject getLocationObject() {
		try {
			return new LocationModule().readJsonFromUrl();
		} catch (IOException | JSONException e) {
			return null;
		}
	}

	public static String getLocationCity() {
		final JSONObject obj = getLocationObject();
		try {
			return obj.getString("city");
		} catch (final JSONException e) {
			return null;
		}
	}

	public static String getLocationCountry() {
		final JSONObject obj = getLocationObject();
		try {
			return obj.getString("country");
		} catch (final JSONException e) {
			return null;
		}
	}

	public static String findCountryByID(final String id) {
		final String country = "";
		for (int i = 0; i < IOHandler.countryCodes.length(); i++)
			try {
				final JSONObject object = IOHandler.countryCodes.getJSONObject(i);
				final String codeValue = object.getString("Code");
				final String value = object.getString("Name");
				if (codeValue.equals(id))
					return value;
			} catch (final JSONException e) {
				e.printStackTrace();
				return null;
			}

		return country;
	}

	public static String findCapitalByCountryID(final String id) {
		final Iterator<?> it = IOHandler.capitals.keys();
		while (it.hasNext()) {
			final String key = (String) it.next();
			if (key.equals(id))
				try {
					return IOHandler.capitals.getString(key);
				} catch (final JSONException e) {
					return null;
				}
		}
		return null;
	}
}
