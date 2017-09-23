package wr.main;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wr.service.OWMAPIConnectionTask;

public class JSONDataHandler {

	private final JSONArray array;

	public JSONDataHandler(final JSONArray array) {
		this.array = array;
		WeatherReader.getUi().getProgressBar().setValue(80);
	}

	public JSONArray getArray() {
		return array;
	}

	public JSONObject getByIndex(final int index) {
		return get(index);
	}

	public JSONObject findByID(final int id) {
		for (int i = 0; i < array.length(); i++) {
			final JSONObject object = get(i);
			try {
				if (object.getInt("id") == id)
					return object;
			} catch (final JSONException e) {
				return null;
			}
		}
		return null;
	}

	public int getCityID(final String city) {
		for (int i = 0; i < array.length(); i++) {
			final JSONObject object = get(i);
			try {
				final int id = findInt(object, "id");
				final String cityName = object.getString("name");
				if (cityName.equals(city))
					return id;
			} catch (final JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return -0xff;
	}

	/**
	 * Kind of heavyweight but the best searching strategy.
	 */
	public JSONObject findByName(final String name) {
		final long start = System.currentTimeMillis();
		//Filter out and prioritize capitals, since we have 200 000+ cities(Some have the same names)
		if (isCapital(name))
			for (int i = 0; i < array.length(); i++) {
				final JSONObject object = get(i);
				try {
					if (object.getString("name").equals(name)
					        && object.getString("country").equals(getCountryIDByCapital(name))) {
						System.out.println("Found entry for : " + (System.currentTimeMillis() - start) + " ms");
						return object;
					}
				} catch (final JSONException e) {
					return null;
				}
			}

		for (int i = 0; i < array.length(); i++) {
			final JSONObject object = get(i);
			try {
				if (object.getString("name").equals(name)) {
					System.out.println("Found entry for : " + (System.currentTimeMillis() - start) + " ms");
					return object;
				}
			} catch (final JSONException e) {
				System.err.print("Something went wrong in ");
				System.err.println("JSONDataHandler.getByName()");
				return null;
			}
		}
		return null;
	}

	public String getCityByID(final int id) {
		for (int i = 0; i < array.length(); i++) {
			final JSONObject object = get(i);
			try {
				if (object.getInt("id") == id)
					return object.getString("name");
			} catch (final JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public JSONObject get(final int index) {
		try {
			return array.getJSONObject(index);
		} catch (final JSONException e) {
			System.err.print("Something went wrong at ");
			System.err.println("JSONDataHandler.get()");
			return null;
		}
	}

	public Object findObject(final JSONObject obj, final String key) {
		try {
			return obj.get(key);
		} catch (final JSONException e) {
			System.err.print("Something went wrong at ");
			System.err.println("JSONDataHandler.findObject()");
			return null;
		}
	}

	public int findInt(final JSONObject object, final String key) {
		try {
			return object.getInt(key);
		} catch (final JSONException e) {
			System.err.print("Something went wrong at ");
			System.err.println("JSONDataHandler.findInt()");
			return -0xff;
		}
	}

	public int findCapitalID(final String country_code, final String capital) {
		for (int i = 0; i < array.length(); i++)
			try {
				final JSONObject object = array.getJSONObject(i);
				if (object.getString("name").equals(capital) && object.getString("country").equals(country_code))
					return object.getInt("id");
			} catch (final JSONException e) {
				System.err.println("Error in findcapitalID");
			}
		return -0xff;
	}

	public String findCapitalByID(final String id) {
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

	public boolean containsCity(final String city) {
		for (int i = 0; i < array.length(); i++) {
			final JSONObject object = get(i);
			if (findObject(object, "name").toString().equals(city))
				return true;
		}
		WeatherReader.getUi().getAdditionalInfoButton().setVisible(false);
		WeatherReader.getInstance().onCityNotFound(city);
		return false;
	}

	public boolean containsCountry(final String country) {
		for (int i = 0; i < array.length(); i++)
			if (IOHandler.getCountryID(country) != null)
				return true;
		return false;
	}

	public boolean isCapital(final String city) {
		final Iterator<?> it = IOHandler.capitals.keys();
		while (it.hasNext()) {
			final String key = (String) it.next();
			try {
				if (IOHandler.capitals.getString(key).equals(city))
					return true;
			} catch (final JSONException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean isCapital(final String countryID, final String city) {
		final Iterator<?> it = IOHandler.capitals.keys();
		while (it.hasNext())
			try {
				final String key = (String) it.next();
				final String value = IOHandler.capitals.getString(key);
				if (key.equals(countryID) && value.equals(city))
					return true;
			} catch (final JSONException e) {
				return false;
			}
		return false;
	}

	public String getCountryIDByCapital(final String capital) {
		final Iterator<?> it = IOHandler.capitals.keys();//Yet again Josh Bloch saves the day !!
		while (it.hasNext())
			try {
				final String key = (String) it.next();
				final String value = IOHandler.capitals.getString(key);
				if (value.equals(capital))
					return key;
			} catch (final JSONException e) {
				return null;
			}
		return null;
	}

	public JSONObject getFromOWMByID(final int id) {
		return new OWMAPIConnectionTask(id, OWMAPIConnectionTask.SERVICE_WEATHER).readFromService();
	}

	public JSONArray getArrayFromObject(final JSONObject object, final String key) {
		try {
			return object.getJSONArray(key);
		} catch (final JSONException e) {
			System.err.println("Array not found");
			return null;
		}
	}

}
