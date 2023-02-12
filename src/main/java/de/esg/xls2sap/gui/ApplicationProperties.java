package de.esg.xls2sap.gui;

import java.io.IOException;
import java.util.Properties;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApplicationProperties
{
	public enum PropertyName
	{
		// TODO weitere Werte
		REST_ENDPOINT("restEndpoint"), //
		DISPLAY_WIDTH("display_Width"), //
		DISPLAY_HEIGHT("display_Height");

		final String propertyKey;

		PropertyName(final String propertyKey)
		{
			this.propertyKey = propertyKey;
		}
	}

	private static final String PATH = "/META-INF/uva-config.properties";
	private final Properties properties;

	public ApplicationProperties()
	{
		this.properties = new Properties();
	}

	public void readProps() throws IOException
	{
		final var url = this.getClass().getResourceAsStream(PATH);
		this.properties.load(url);
	}

	public String getStringProperty(final PropertyName key)
	{
		return this.properties.getProperty(key.propertyKey);
	}

	int getIntegerProperty(final PropertyName key)
	{
		final var result = Integer.valueOf(this.properties.getProperty(key.propertyKey));
		return result;
	}

}
