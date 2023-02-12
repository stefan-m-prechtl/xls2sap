package de.esg.xls2sap.gui.shared;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;

public class DeutschLocalDateStringConverter extends StringConverter<LocalDate>
{

	private final StringConverter<LocalDate> converter;

	public DeutschLocalDateStringConverter()
	{
		super();
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		this.converter = new LocalDateStringConverter(formatter, formatter);
	}

	@Override
	public String toString(final LocalDate object)
	{
		return this.converter.toString(object);
	}

	@Override
	public LocalDate fromString(final String string)
	{
		return this.converter.fromString(string);
	}

}