package wr.assets;

import java.awt.image.BufferedImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wr.main.IOHandler;

public class DateObject {

	private String			time;
	private String			description;
	private String			date;

	private double			temp;

	private BufferedImage	icon;

	public DateObject(final JSONObject object) {
		try {
			final JSONArray weather = object.getJSONArray("weather");
			time = object.getString("dt_txt").substring(10);
			date = object.getString("dt_txt").substring(5, 10);
			description = weather.getJSONObject(0).getString("description");
			icon = IOHandler.getIconByID(weather.getJSONObject(0).getString("icon"));
			temp = object.getJSONObject("main").getDouble("temp") - 272.15d;
		} catch (final JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getDescription() {
		return description;
	}

	public BufferedImage getIcon() {
		return icon;
	}

	public String getDate() {
		return date;
	}

	public double getTemp() {
		return temp;
	}

	public String getTime() {
		return time;
	}
}