package wr.assets;

import java.awt.image.BufferedImage;

public class DrawableObject {

	public static enum Type {
		NOT_FOUND, STANDART, FORECAST
	}

	private Type			type;

	private boolean			capital;

	private BufferedImage	icon;

	private String			humidity;

	private String			sunrise, sunset;

	private String			temp;

	private String			city_name, country;

	private String			pressure;

	private final String	description;

	private int				id;

	private Builder			builder;

	public static class Builder {

		private BufferedImage	icon;
		private String			humidity;
		private String			sunrise;
		private String			city_name;
		private String			country_id;
		private String			sunset;
		private String			temp;
		private String			pressure;
		private String			description;
		private Type			type;
		private boolean			capital;
		private int				id;

		public Builder icon(final BufferedImage icon) {
			this.icon = icon;
			return this;
		}

		public Builder type(final Type type) {
			Builder.this.type = type;
			return Builder.this;
		}

		public Builder id(final int id) {
			Builder.this.id = id;
			return Builder.this;
		}

		public Builder city_name(final String cityN) {
			city_name = cityN;
			return this;
		}

		public Builder humidity(final String humidity) {
			this.humidity = humidity;
			return this;
		}

		public Builder capital(final boolean capital) {
			this.capital = capital;
			return this;
		}

		public Builder country(final String id) {
			country_id = id;
			return this;
		}

		public Builder sunrise(final String sunrise) {
			this.sunrise = sunrise;
			return this;
		}

		public Builder sunset(final String sunset) {
			this.sunset = sunset;
			return this;
		}

		public Builder temp(final String temp) {
			this.temp = temp;
			return this;
		}

		public Builder pressure(final String pressure) {
			this.pressure = pressure;
			return this;
		}

		public Builder description(final String description) {
			this.description = description;
			return this;
		}

		public DrawableObject build() {
			return new DrawableObject(this);
		}
	}

	private DrawableObject(final Builder builder) {
		this.builder = builder;
		icon = builder.icon;
		city_name = builder.city_name;
		country = builder.country_id;
		humidity = builder.humidity;
		sunrise = builder.sunrise;
		sunset = builder.sunset;
		temp = builder.temp;
		pressure = builder.pressure;
		description = builder.description;
		type = builder.type;
		capital = builder.capital;
		id = builder.id;
	}

	public DrawableObject(final String message) {
		type = Type.NOT_FOUND;
		description = message;
	}

	private DateObject[]	dateObjectArray;

	private int				len;

	public DrawableObject(final DateObject[] dateObjectArray, final Builder current) {
		this(current);
		this.dateObjectArray = dateObjectArray;
		len = dateObjectArray.length;
		type = Type.FORECAST;
	}

	public String getForecastDescription(final int index) {
		if (index > (len - 1))
			throw new IllegalArgumentException("Index > len");
		return dateObjectArray[index].getDescription();
	}

	public String getForecastTime(final int index) {
		if (index > (len - 1))
			throw new IllegalArgumentException("Index > len");
		return dateObjectArray[index].getTime();
	}

	public String getForecastTemp(final int index) {
		if (index > (len - 1))
			throw new IllegalArgumentException("Index > len");
		return String.format("%.02f °C", dateObjectArray[index].getTemp());
	}

	public String getDate(final int index) {
		return dateObjectArray[index].getDate();
	}

	public BufferedImage getForecastIcon(final int index) {
		if (index > (len - 1))
			throw new IllegalArgumentException("Index > len");
		return dateObjectArray[index].getIcon();
	}

	public int getLen() {
		return len;
	}

	public String getDescription() {
		return description;
	}

	public String getHumidity() {
		return humidity;
	}

	public String getCityName() {
		return city_name;
	}

	public String getCountry() {
		return country;
	}

	public boolean isCapital() {
		return capital;
	}

	public BufferedImage getIcon() {
		return icon;
	}

	public String getPressure() {
		return pressure;
	}

	public int getId() {
		return id;
	}

	public String getSunrise() {
		return sunrise;
	}

	public String getSunset() {
		return sunset;
	}

	public String getTemp() {
		return temp;
	}

	public Type getType() {
		return type;
	}

	public Builder getBuilder() {
		return builder;
	}
}
