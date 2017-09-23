package wr.assets;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import wr.main.IOHandler;
import wr.main.JSONDataHandler;
import wr.main.WRUserInterface;
import wr.main.WeatherReader;

public class CityListPopup {

	private final String			country;

	private final String			capital;

	private int						index;

	private final int				capitalID;

	private String[]				cityNames;

	private int[]					cityIDs;

	private final WRUserInterface	ui;

	private JScrollPane				pane;

	private boolean					notFound;

	public CityListPopup(final WRUserInterface ui) {
		final JSONDataHandler dh = WeatherReader.getHandler();
		country = ui.getSearchField().getText();
		capital = dh.findCapitalByID(IOHandler.getCountryID(country));
		notFound = false;

		this.ui = ui;
		capitalID = dh.findCapitalID(IOHandler.getCountryID(country), capital);
	}

	private String[] cityNames() {
		final String countryID = IOHandler.getCountryID(country);
		if (countryID == null) {
			WeatherReader.getInstance().onCountryNotFounds(country);
			notFound = true;
		}

		final Set<String> cityNames = IOHandler.getCityListByID(countryID);

		final Collection<Integer> idList = IOHandler.getCityIDS(countryID);

		final String[] citynames = new String[cityNames.size()];
		final int[] ids = new int[idList.size()];
		final Iterator<String> it = cityNames.iterator();
		int index = 0;
		while (it.hasNext()) {
			final String value = it.next();
			citynames[index++] = value;
		}

		index = 0;
		final Iterator<Integer> iter = idList.iterator();
		while (iter.hasNext()) {
			final int id = iter.next();
			ids[index++] = id;
		}
		cityIDs = ids;
		this.cityNames = citynames;
		return citynames;
	}

	public void show() {
		final JList<String> list = new JList<String>(cityNames());
		if (notFound)
			return;
		final JList<String> capital = new JList<String>(new String[] {
		        this.capital
		});
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		capital.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		list.addListSelectionListener(e -> {
			index = list.getSelectedIndex();
			capital.clearSelection();
		});
		capital.addListSelectionListener(e -> list.clearSelection());
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					final JList<?> list = (JList<?>) e.getSource();
					index = list.locationToIndex(e.getPoint());
					execute();
				}
			}
		});
		capital.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					final JList<?> list = (JList<?>) e.getSource();
					index = list.locationToIndex(e.getPoint());
					executeCapital();
				}
			}
		});
		final JScrollPane scrollPane = new JScrollPane(list);
		pane = scrollPane;// local , in order to remove optionPane afterwards
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		final Object[] message = {
		        "Capital",
		        capital,
		        "Cities : " + country,
		        scrollPane,
		};

		final Object[] options = {
		        "Select",
		        "Cancel"
		};

		final int choice = JOptionPane.showOptionDialog(null, message, "Searching Cities", JOptionPane.OK_CANCEL_OPTION,
		        JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if (choice == JOptionPane.OK_OPTION) {
			if (list.isSelectionEmpty() && !capital.isSelectionEmpty())
				executeCapital();
			if (!list.isSelectionEmpty() && capital.isSelectionEmpty())
				execute();
		}
	}

	private void execute() {
		ui.getCityButton().setText("City");
		ui.getCityButton().setToolTipText("List all cities from the selected country");
		ui.setSearchingCities(true);
		ui.getSearchField().setText(null);
		ui.getSearchField().setText(cityNames[index]);

		WeatherReader.getInstance().onSearch(cityIDs[index]);
		SwingUtilities.getWindowAncestor(pane).dispose();
	}

	private void executeCapital() {
		ui.getCityButton().setText("City");
		ui.getCityButton().setToolTipText("List all cities from the selected country");
		ui.setSearchingCities(true);
		ui.getSearchField().setText(null);
		ui.getSearchField().setText(capital);

		WeatherReader.getInstance().onSearch(capitalID);
		SwingUtilities.getWindowAncestor(pane).dispose();
	}
}
