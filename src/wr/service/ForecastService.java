package wr.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ForecastService {

	private final JSONObject	object;

	private final JSONObject[]	hourlyData;

	private final JSONArray		data;

	public ForecastService(final int city_id) {
		object = new OWMAPIConnectionTask(city_id, OWMAPIConnectionTask.SERVICE_FORECAST).readFromService();
		hourlyData = new JSONObject[40];
		JSONArray array = null;
		try {
			array = object.getJSONArray("list");
		} catch (final JSONException e) {
			e.printStackTrace();
		}
		data = array;
		fillData();
	}

	private void fillData() {
		for (int i = 0; i < hourlyData.length; i++)
			try {
				final JSONObject object = data.getJSONObject(i);
				hourlyData[i] = object;
			} catch (final JSONException e) {
				e.printStackTrace();
			}
	}

	/**
	 * 
	 * @param index - from 0 to 4 where 0 is today and 4 is after 4 days
	 * @return
	 */
	public String getDate(int index) {
		if (index > 4)
			throw new IllegalArgumentException("Index > 4");
		index *= 8;
		try {
			return hourlyData[index].getString("dt_txt").substring(6, 10);
		} catch (final JSONException e) {}
		return null;
	}

	public JSONObject[] getDailyData(final int day) {
		JSONObject[] arr = null;
		final int freq = dayFreq(getDate(day));
		arr = new JSONObject[freq];
		final int from = from(getDate(day));
		for (int i = 0; i < arr.length; i++)
			arr[i] = hourlyData[from + i];
		return arr;
	}

	private int dayFreq(final String day) {
		int freq = 0;
		for (final JSONObject obj : hourlyData) {
			try {
				if (obj.getString("dt_txt").substring(6, 10).equals(day))
					freq++;
			} catch (final JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return freq;
	}

	private int from(final String day) {
		for (int i = 0; i < hourlyData.length; i++) {
			final JSONObject obj = hourlyData[i];
			try {
				if (obj.getString("dt_txt").substring(6, 10).equals(day))
					return i;
			} catch (final JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return -0xff;
	}

}
