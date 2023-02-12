package de.esg.xls2sap.controller.upload;

import java.io.File;
import java.util.List;

public final class CsvUtils
{
	private CsvUtils()
	{
		// nothing to do!
	}

	public static boolean isDateinameZuLang(final File file, final int grenze)
	{
		return (file != null) && (file.getName().length() > grenze);
	}

	public static String validateColumnHeaders(final List<String> headers)
	{
		final StringBuilder errors = new StringBuilder();

		// validate Dynamic column header
		for (final DynamicColumnHeaderEnum dynamicColumnHeaderEnum : DynamicColumnHeaderEnum.values())
		{
			for (final String col : DynamicColumnHeaderEnum.getEnumValues(dynamicColumnHeaderEnum))
			{
				checkAvailabilityAndParticularChar(headers, errors, col);
			}
		}

		// validate static column header
		for (final StaticColumnHeaderEnum staticColumnHeaderEnum : StaticColumnHeaderEnum.values())
		{
			final String col = staticColumnHeaderEnum.getValue();
			checkAvailabilityAndParticularChar(headers, errors, col);
		}

		return errors.toString();
	}

	private static void checkAvailabilityAndParticularChar(final List<String> headers, final StringBuilder errors, final String col)
	{
		final boolean contains = headers.contains(col);
		if (!contains)
		{
			errors.append("Spalte ".concat(col).concat(" nicht vorhanden\n"));
		}
	}

}
