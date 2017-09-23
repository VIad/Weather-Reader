package wr.main;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IOHandler {

	public static final File			citiesFile			= IOHandler
	        .streamToFile(IOHandler.class.getResourceAsStream("stuff/citylist.json"));

	public static final File			countryCodesFile	= IOHandler
	        .streamToFile(IOHandler.class.getResourceAsStream("stuff/countryData.txt"));

	public static final File			capitalsFile		= IOHandler
	        .streamToFile(IOHandler.class.getResourceAsStream("stuff/capitals.txt"));
	public static final BufferedImage	capital_icon;

	public static final JSONArray		data;

	public static final JSONArray		countryCodes;

	public static final JSONObject		capitals;

	static {
		long start = System.currentTimeMillis();
		JSONArray toSet = null;
		JSONArray toSetCountry = null;
		JSONObject toSetCapitals = null;
		BufferedImage cap = null;
		try {
			cap = ImageIO.read(streamToFile(IOHandler.class.getResourceAsStream("stuff/capital.png")));
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
		capital_icon = cap;
		WeatherReader.getUi().getProgressBar().setValue(40);
		initFiles();
		start = System.currentTimeMillis();
		try {
			toSet = new JSONArray(read(citiesFile));
			toSetCountry = new JSONArray(read(countryCodesFile));
			toSetCapitals = new JSONObject(read(capitalsFile));
			WeatherReader.getUi().getProgressBar().setValue(60);

		} catch (final JSONException e) {
			System.err.println("An error occured while loading ");
		}
		System.out.println("Loading Arrays for : " + (System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		data = toSet;
		countryCodes = toSetCountry;
		capitals = toSetCapitals;
	}

	private IOHandler() {}

	public static String read(final File f) {
		final long start = System.currentTimeMillis();
		final StringBuilder builder = new StringBuilder();
		try {
			final BufferedReader rd = new BufferedReader(new java.io.FileReader(f));
			String cur;
			while ((cur = rd.readLine()) != null)
				builder.append(cur);
			rd.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		System.out.println("Finished loading string for : " + (System.currentTimeMillis() - start) + " ms");
		System.out.println("String size : " + builder.length());
		return builder.toString();
	}

	public static String getCountryID(final String country) {
		for (int i = 0; i < countryCodes.length(); i++)
			try {
				final JSONObject obj = countryCodes.getJSONObject(i);
				if (obj.getString("Name").equals(country))
					return obj.getString("Code");
			} catch (final JSONException e) {
				System.err.println(e.getMessage());
				return null;
			}
		return null;
	}

	public static Set<String> getCityListByID(final String id) {
		return getCityData(id).keySet();
	}

	public static Collection<Integer> getCityIDS(final String id) {
		return getCityData(id).values();
	}

	/**
	 * Thank f*ck we are on modern JVM
	 */
	private static TreeMap<String, Integer> getCityData(final String id) {
		//Colletions.freq// to remove already added items
		final TreeMap<String, Integer> map = new TreeMap<String, Integer>();
		for (int i = 0; i < data.length(); i++)
			try {
				final JSONObject object = data.getJSONObject(i);
				if (object.getString("country").equals(id))
					if (Collections.frequency(map.values(), object.getString("name")) < 1)/*Rip GC*/
						map.put(object.getString("name"), object.getInt("id"));
			} catch (final JSONException e) {
				return null;
			}
		return map;
	}

	public static BufferedImage getIconByID(final String id) {
		final String url = "http://openweathermap.org/img/w/" + id + ".png";
		try {
			return ImageIO.read(new URL(url).openStream());
		} catch (final IOException e) {
			System.err.println("Something went wrong while loading image");
			return null;
		}
	}

	public static JSONObject readJsonFromUrl(final String url) throws IOException, JSONException {
		InputStream is = null;
		try {
			is = new URL(url).openStream();
		} catch (final UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "Грешка при свързването с интернет", "Weather reader", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		try {
			final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			final String jsonText = readAll(rd);
			final JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	private static String readAll(final Reader rd) throws IOException {
		final StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1)
			sb.append((char) cp);
		return sb.toString();
	}

	public static File streamToFile(final InputStream in) {
		try {
			final File tempFile = File.createTempFile("tempFile", "tmp");
			tempFile.deleteOnExit();
			try (FileOutputStream out = new FileOutputStream(tempFile)) {
				IOUtils.copy(in, out);
			}

			return tempFile;
		} catch (final IOException ex) {
			System.err.println("File not found");
			return null;
		}
	}

	public static final File	programDir	= new File("C:\\wr\\");

	public static final File	propsFile	= new File("C:\\wr\\props.wr");

	public synchronized static void initFiles() {
		new Thread(() -> {
			if (!programDir.exists())
				programDir.mkdirs();
			if (!propsFile.exists())
				try {
					propsFile.createNewFile();
				} catch (final IOException e) {}
		}).start();
	}

	public static void init() {}

	public static void logError(final StackTraceElement[] stackTrace) {
		final File f = new File("C:\\wr\\errlog.txt");
		if (!f.exists())
			try {
				f.createNewFile();
				final FileWriter wr = new FileWriter(f);
				for (final StackTraceElement el : stackTrace) {
					wr.write(el.toString());
					wr.write("\r\n");
				}
				wr.close();
			} catch (final IOException e) {}
	}
}
