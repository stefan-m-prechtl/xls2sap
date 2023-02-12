package de.esg.xls2sap.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;

public final class DateUtils
{
	private final static String DATE_FORMAT = "dd.MM.yyyy";
	private final static int MAX_DATE_LENGTH = 10;

	private DateUtils()
	{

	}

	public static void validateDatePickerOnFocuslost(final DatePicker dp, final LoggerExposer logger)
	{
		dp.focusedProperty().addListener((obs, oldVal, newVal) -> {

			if (!newVal)
			{
				try
				{
					if (dp.getEditor().getText().trim().isEmpty())
					{
						return;
					}

					dp.setValue(LocalDate.parse(dp.getEditor().getText(), DateTimeFormatter.ofPattern("dd.MM.yyyy")));
				}
				catch (final Exception e)
				{
					dp.setValue(null);
					logger.fatalErrorConsumer().accept(e);
					final Alert alert = new Alert(AlertType.ERROR, String.format("Falsche Datum Format: %s", dp.getEditor().getText()), ButtonType.CLOSE);

					alert.show();
				}
			}
		});
	}

	public static boolean dateValidation(final String date)
	{
		boolean status = false;
		if (checkDate(date))
		{
			final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.GERMAN);
			dateFormat.setLenient(false);
			try
			{
				dateFormat.parse(date);
				status = true;
			}
			catch (final Exception e)
			{
				status = false;
			}
		}
		return status;
	}

	private static boolean checkDate(final String date)
	{
		final String pattern = "(0?[1-9]|[12][0-9]|3[01])\\.(0?[1-9]|1[0-2])\\.([0-9]{4})";
		boolean flag = false;
		if (date.matches(pattern))
		{
			flag = true;
		}
		return flag;
	}

	private static boolean hasSpecialChar(final String input)
	{
		final List<String> specialChar = List.of("|", "!", "#", "$", "%", "&", "/", "(", ")", "=", "?", "»", "«", "@", "£", "§", "€", "{", "}", "-", ";", "'", "<", ">", "_", ",",
				"+");
		for (final String item : specialChar)
		{
			if (input.contains(item))
			{
				return true;
			}
		}

		return false;
	}

	public static UnaryOperator<Change> limitDateLengthAndRejectCharEntries()
	{
		return new UnaryOperator<TextFormatter.Change>()
		{

			@Override
			public TextFormatter.Change apply(final TextFormatter.Change t)
			{

				if ((t.isAdded() && (t.getText().matches("[A-Za-z]"))) || hasSpecialChar(t.getText()))
				{
					t.setText("");
				}
				if ((t.getControlNewText().length() > MAX_DATE_LENGTH))
				{
					t.setText(t.getControlText().substring(t.getRangeStart(), t.getRangeEnd()));
				}
				return t;
			}
		};
	}
}
