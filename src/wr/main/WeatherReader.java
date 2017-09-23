package wr.main;

import org.json.JSONException;
import org.json.JSONObject;

import wr.assets.DrawableObject;

public class WeatherReader {

	private static WeatherReader	reader;

	private static WRUserInterface	ui;

	private static JSONDataHandler	handler;

	public static void main(final String[] args) {
		ui = new WRUserInterface();
		ui.initialize();
		reader = new WeatherReader();
		reader.initialize();

	}

	private void initialize() {
		instantiate();
		ui.getProgressBar().setValue(100);
		ui.onLoadingFinished(true);
	}

	private void instantiate() {
		// Heavy loading stuff, done in a separate thread
		ui.getProgressBar().setValue(20);
		new Thread(() -> IOHandler.init()).start();
		handler = new JSONDataHandler(IOHandler.data);
	}

	public static WRUserInterface getUi() {
		return ui;
	}

	public static WeatherReader getInstance() {
		return reader;
	}

	/**
	 * Delays the main thread
	 */
	static void delay(final int ms) {
		try {
			Thread.sleep(ms);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void onSearch(final String actionCommand) {
		if (actionCommand.equals("-"))
			return;
		if (handler.containsCity(actionCommand)) {
			final JSONObject cityData = handler.findByName(actionCommand);
			boolean setCond = false;
			try {
				if (handler.isCapital(cityData.getString("country"), actionCommand))
					setCond = true;
			} catch (final JSONException e) {
				e.printStackTrace();
			}
			final int city_id = handler.findInt(cityData, "id");
			final JSONObject received = handler.getFromOWMByID(city_id);
			ui.onDataReceived(received, actionCommand, setCond);
		}
	}

	public void onSearch(final int id) {
		final JSONObject cityData = handler.findByID(id);
		final JSONObject received = handler.getFromOWMByID(id);
		try {
			ui.onDataReceived(received, handler.getCityByID(id), handler.isCapital(cityData.getString("country")));
		} catch (final JSONException e) {}
	}

	public void onCityNotFound(final String city) {
		getUi().setCurrent(new DrawableObject(city + " Not Found !"));
	}

	public void onCountryNotFounds(final String country) {
		getUi().setCurrent(new DrawableObject(country + " Not Found !"));
		ui.getAdditionalInfoButton().setVisible(false);
	}

	public static JSONDataHandler getHandler() {
		return handler;
	}
}
