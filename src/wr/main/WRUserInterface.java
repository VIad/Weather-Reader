package wr.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.json.JSONException;
import org.json.JSONObject;

import wr.assets.CityListPopup;
import wr.assets.DateObject;
import wr.assets.DrawableObject;
import wr.service.ForecastService;
import wr.service.LocationModule;

public class WRUserInterface {

	private JFrame			frame;

	private WRUIPanel		panel;

	private JSONDataHandler	dataHandler;

	private DrawableObject	current;

	private EventHandler	handler;

	private Dimension		frameSize, panelBounds;

	private JTextField		searchField;
	private JButton			searchButton, findMeButton, additionalInfoButton;
	private JLabel			loadLabel, date;
	private JProgressBar	progressBar;
	private JButton			cityButton;

	private JButton[]		dateButtons;

	private boolean			searchingCities;

	private boolean			pressed;

	/**
	 * @wbp.parser.entryPoint
	 */
	void initialize() {
		searchingCities = true;
		pressed = false;
		setUIManager();
		handler = new EventHandler();
		dateButtons = new JButton[5];
		panel = new WRUIPanel();
		panel.setBounds(10, 84, 460, 186);
		//		panel.setBackground(color);
		frame = new JFrame("Weather Reader 0.5 Pre Build");
		frameSize = new Dimension(500, 320);
		panelBounds = new Dimension(580, 186);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(WRUserInterface.class.getResource("stuff/icon.png")));
		frame.setSize(frameSize);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocation(300, 200);
		frame.setResizable(false);
		frame.addKeyListener(handler);
		date = new JLabel("Date");
		date.setVisible(true);
		date.setBounds(534, 2, 60, 30);
		date.setFont(new Font("Tahoma", Font.PLAIN, 14));
		frame.add(date);
		for (int i = 0; i < dateButtons.length; i++) {
			dateButtons[i] = new JButton();
			dateButtons[i].setBounds(520, (i * 50) + 30, 60, 30);
			dateButtons[i].addActionListener(handler);
			dateButtons[i].setFocusable(false);
			frame.add(dateButtons[i]);
		}
		//		frame.setResizable(false);

		progressBar = new JProgressBar();
		progressBar.setBounds(68, 137, 347, 22);
		frame.getContentPane().add(progressBar);

		loadLabel = new JLabel("Loading");
		loadLabel.setFont(new Font("Tahoma", Font.PLAIN, 27));
		loadLabel.setBounds(195, 81, 149, 33);
		frame.getContentPane().add(loadLabel);
		frame.setVisible(true);
		frame.addKeyListener(handler);

		searchField = new JTextField();
		searchField.setFont(new Font("Tahoma", Font.PLAIN, 17));
		searchField.setBounds(108, 21, 139, 39);

		searchField.setColumns(10);
		searchField.setEnabled(false);

		searchButton = new JButton("Search");
		searchButton.setBounds(261, 21, 89, 39);
		searchButton.setEnabled(false);

		findMeButton = new JButton("Find Me");
		findMeButton.setBounds(355, 21, 100, 39);
		findMeButton.setEnabled(false);

		frame.repaint();

		cityButton = new JButton("City");
		cityButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		cityButton.setBounds(15, 21, 80, 40);
		cityButton.setToolTipText("Search Cities");

		additionalInfoButton = new JButton("More Info");
		additionalInfoButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		additionalInfoButton.setBounds(15, 69, 80, 40);
		additionalInfoButton.setToolTipText("Additional weather information about the city");
		additionalInfoButton.setVisible(false);

		frame.getContentPane().add(additionalInfoButton);

		additionalInfoButton.addActionListener(handler);
		searchButton.addActionListener(handler);
		findMeButton.addActionListener(handler);
		searchField.addActionListener(handler);
		cityButton.addActionListener(handler);

		additionalInfoButton.setFocusable(false);
		searchButton.setFocusable(false);
		findMeButton.setFocusable(false);
		cityButton.setFocusable(false);
		
		new Timer(20, handler).start();
	}

	private void setUIManager() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//Will never fail, catch block useless
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {}
	}

	public void setCurrent(final DrawableObject current) {
		this.current = current;
	}

	/**
	 * All graphics usage done here
	 */
	@SuppressWarnings("serial")
	class WRUIPanel extends JPanel {

		final Font	font		= new Font("Courier new", Font.BOLD, 20);
		final Font	timeFont	= new Font("Courier New", Font.BOLD, 16);
		final Font	sthFont		= new Font("Courier New", Font.BOLD, 12);

		@Override
		protected void paintComponent(final Graphics g2) {
			super.paintComponent(g2);
			//			WRUIPanel.this.setBackground(Color.green);//debug, remove @ final

			final Graphics2D g = (Graphics2D) g2;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			final DrawableObject o = current;
			if (o == null)
				return;

			switch (o.getType())
			{
				default:
					break;
				case FORECAST: {
					standartWeatherServiceRender(g, o);
					g.fillRect(0, 200, 150, 5);
					g.fillRect(400, 200, 200, 5);
					g.drawString("Data for : " + o.getDate(0), 180, 210);
					final int len = o.getLen();
					if (len <= 4)
						//row 1
						for (int i = 0; i < len; i++) {
							g.setFont(timeFont);
							g.drawString(o.getForecastTime(i), i * 120, 240);
							g.drawImage(o.getForecastIcon(i), (i * 120) + 17, 240, null);
							g.drawString(o.getForecastTemp(i), (i * 120) + 12, 300);
							g.setFont(sthFont);
							g.drawString(o.getForecastDescription(i), i * 120, 320);
						}

					if (len > 4) {
						for (int i = 0; i < 4; i++) {
							g.setFont(timeFont);
							g.drawString(o.getForecastTime(i), i * 150, 240);
							g.drawImage(o.getForecastIcon(i), (i * 150) + 17, 240, null);
							g.drawString(o.getForecastTemp(i), (i * 150) + 12, 300);
							g.setFont(sthFont);
							g.drawString(o.getForecastDescription(i), (i * 150) + 15, 320);
						}
						int mul = 0;
						for (int j = 4; j < len; j++) {
							g.setFont(timeFont);
							g.drawString(o.getForecastTime(j), mul * 150, 350);
							g.drawImage(o.getForecastIcon(j), (mul * 150) + 17, 360, null);
							g.drawString(o.getForecastTemp(j), (mul * 150) + 12, 420);
							g.setFont(sthFont);
							g.drawString(o.getForecastDescription(j), (mul * 150) + 10, 440);
							++mul;
						}
					}

					return;
				}
				case NOT_FOUND: {
					g.setFont(font);
					g.setColor(Color.gray.darker().darker());
					g.setFont(font);
					g.drawString("" + o.getDescription(), 0, 20);
					return;
				}
				case STANDART: {
					standartWeatherServiceRender(g, o);
				}
			}

		}

		private void standartWeatherServiceRender(final Graphics2D g, final DrawableObject o) {
			g.setColor(Color.gray.darker().darker());
			g.setFont(font);
			g.drawImage(o.getIcon(), 100, 0, null);
			if (o.isCapital())
				g.drawImage(IOHandler.capital_icon, 210, 5, 30, 30, null);
			g.drawString("City : " + o.getCityName() + ", " + o.getCountry(), 0, 60);
			g.drawString("Temperature : " + o.getTemp(), 0, 90);
			g.drawString(o.getHumidity() + " %" + " | " + o.getDescription(), 0, 120);
			g.drawString(o.getPressure(), 0, 150);
			g.drawString(o.getSunrise() + " | " + o.getSunset(), 0, 180);
		}
	}

	private class EventHandler extends KeyAdapter implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent event) {
			if ((event.getSource() == searchButton) || (event.getSource() == searchField)) {
				setToDefault();
				if (searchField.getText().equals(""))
					return;
				if (searchingCities)
					WeatherReader.getInstance().onSearch(searchField.getText().trim());
				else
					new CityListPopup(WRUserInterface.this).show();
			}
			if (event.getSource() == findMeButton) {
				if (searchingCities)
					searchField.setText(LocationModule.getLocationCity());
				else
					searchField.setText(LocationModule.getLocationCountry());
				setToDefault();
			}
			if (event.getSource() == cityButton) {
				if (searchingCities) {
					searchingCities = false;
					cityButton.setText("Country");
					cityButton.setToolTipText("List all cities from the selected country");
				} else {
					searchingCities = true;
					cityButton.setText("City");
					cityButton.setToolTipText("Display weather data for the selected city");
				}
				setToDefault();
			}

			if (event.getSource() == additionalInfoButton) {
				pressed = !pressed;
				additionalInfoButton.setText(pressed ? "Reset" : "More Info");
				if (!pressed) {
					setToDefault();
					searchField.setText(null);
					return;
				}
				final ForecastService service = new ForecastService(727011);
				final String[] arr = new String[5];
				for (int i = 0; i < arr.length; i++)
					arr[i] = service.getDate(i);
				setDateButtons(arr);

				frameSize.width = 600;
				frame.setSize(frameSize);
			}

			for (int i = 0; i < dateButtons.length; i++)
				if (event.getSource() == dateButtons[i]) {
					final int i_mask = i; //Outplayed you Java XDDDD Lmao
					synchronized (event.getSource()) {
						new Thread(() -> {
							try {
								final ForecastService service = new ForecastService(current.getId());
								final JSONObject[] arr = service.getDailyData(i_mask);
								onDateButtonPressed(arr);
							} catch (final Exception ex) {
								IOHandler.logError(ex.getStackTrace());
								JOptionPane.showMessageDialog(null,
								        "Fatal Error in lambda thread @ 313, see log @ C:\\wr\\log.txt for more info", "Error",
								        JOptionPane.ERROR_MESSAGE);
								System.exit(1);
							}
						}).start();
					}
					return;
				}

			panel.repaint();
			panel.setBounds(10, 84, panelBounds.width, panelBounds.height);
			frame.repaint();
			//			frame.getContentPane().setBackground(color);
			frame.revalidate();
			frame.validate();
		}

		private void onDateButtonPressed(final JSONObject[] arr) {

			frameSize.height = 580;
			panelBounds.height = 456;
			frame.setSize(frameSize);

			final int len = arr.length;
			final DateObject[] dObjectArray = new DateObject[len];
			for (int i = 0; i < dObjectArray.length; i++)
				dObjectArray[i] = new DateObject(arr[i]);
			final DrawableObject.Builder curBuilder = current.getBuilder();
			current = new DrawableObject(dObjectArray, curBuilder);
		}

		@Override
		public void keyPressed(final KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_TAB)
				frame.setState(Frame.ICONIFIED);
		}

	}

	public void onLoadingFinished(final boolean toggleGUI) {
		searchButton.setEnabled(toggleGUI);
		searchField.setEnabled(toggleGUI);
		findMeButton.setEnabled(toggleGUI);

		frame.getContentPane().remove(loadLabel);
		frame.getContentPane().remove(progressBar);

		frame.getContentPane().add(panel);
		frame.getContentPane().add(cityButton);
		frame.getContentPane().add(searchField);
		frame.getContentPane().add(searchButton);
		frame.getContentPane().add(findMeButton);
		frame.repaint();
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void setDateButtons(final String[] dates) {
		final boolean cond = new ForecastService(current.getId()).getDailyData(0).length == 1; /** Inneficient as f(ck*/

		for (int i = 0; i < dates.length; i++) {
			if (cond && (i == 0))
				continue;
			final JButton button = dateButtons[i];
			button.setText(dates[i]);
			button.setToolTipText("Hourly weather info for " + button.getText());
			button.setVisible(true);
		}
	}

	public void setToDefault() {
		frameSize.setSize(500, 320);
		frame.setSize(frameSize);
		panelBounds.height = 186;
		additionalInfoButton.setVisible(false);
		additionalInfoButton.setText("More Info");
		pressed = false;

		for (final JButton button : dateButtons)
			button.setVisible(false);
	}

	public void onDataReceived(final JSONObject received, final String cityData, final boolean cond) {
		if (dataHandler == null)
			dataHandler = WeatherReader.getHandler();
		try {
			additionalInfoButton.setVisible(true);
			final long sunrise = received.getJSONObject("sys").getLong("sunrise");
			final long sunset = received.getJSONObject("sys").getLong("sunset");
			final double temp = received.getJSONObject("main").getDouble("temp") - 273.15d;//Convert to celsius
			final Date sunriseDate = new Date(1000 * sunrise);
			final Date sunsetDate = new Date(1000 * sunset);
			final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

			final String city_name = cityData;
			final String country = LocationModule.findCountryByID(received.getJSONObject("sys").getString("country"));
			final String temperature = String.format("%.02f °C", temp);
			final String description = dataHandler.getArrayFromObject(received, "weather").getJSONObject(0)
			        .getString("description");
			final String humidity = "Humidity : " + received.getJSONObject("main").getInt("humidity");
			final String pressure = "Pressure : " + received.getJSONObject("main").getInt("pressure") + " hPa";
			final String icon_id = dataHandler.getArrayFromObject(received, "weather").getJSONObject(0).get("icon").toString();
			final String sunriseString = "Sunrise : " + sdf.format(sunriseDate);
			final String sunsetString = "Sunset : " + sdf.format(sunsetDate);

			final DrawableObject.Builder builder = new DrawableObject.Builder();

			current = builder
					.id(WeatherReader.getHandler().getCityID(cityData))
					.icon(IOHandler.getIconByID(icon_id))
					.type(DrawableObject.Type.STANDART)
			        .temp(temperature).country(country)
			        .description(description)
			        .sunrise(sunriseString)
			        .sunset(sunsetString)
			        .city_name(city_name)
			        .humidity(humidity)
			        .pressure(pressure)
			        .capital(cond)
			        .build();

		} catch (final JSONException e) {
			e.printStackTrace();
		}
	}

	public JTextField getSearchField() {
		return searchField;
	}

	public JButton getCityButton() {
		return cityButton;
	}

	public JButton getAdditionalInfoButton() {
		return additionalInfoButton;
	}

	public void setSearchingCities(final boolean searchingCities) {
		this.searchingCities = searchingCities;
	}
}
