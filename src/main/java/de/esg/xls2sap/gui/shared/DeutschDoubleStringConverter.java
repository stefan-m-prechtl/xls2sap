package de.esg.xls2sap.gui.shared;

import java.util.Locale;

import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

public class DeutschDoubleStringConverter extends StringConverter<Double>
{

	private final NumberStringConverter numberStringConverter;

	public DeutschDoubleStringConverter()
	{
		super();
		this.numberStringConverter = new NumberStringConverter(Locale.GERMAN);
	}

	@Override
	public String toString(final Double object)
	{
		return this.numberStringConverter.toString(object);
	}

	@Override
	public Double fromString(final String string)
	{

		return this.numberStringConverter.fromString(string).doubleValue();
	}

}