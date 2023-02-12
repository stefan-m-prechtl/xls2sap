package de.esg.xls2sap.gui.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class BundleUtil
{

	public static final String BUNDLE_BASENAME = "bundles.GuiBundle";

	private static final String INDICATOR_MISSING_RESOURCE = "?";
	private static final String INDICATOR_MISSING_KEY = "??";

	private BundleUtil()
	{
		// nur statische Methoden
	}

	public static String getString(final ResourceBundle resourceBundle, final BundleKeys key)
	{
		if (resourceBundle != null)
		{
			try
			{
				return resourceBundle.getString(key.name());
			}
			catch (final MissingResourceException e)
			{
				return INDICATOR_MISSING_KEY + key;
			}
		}
		return INDICATOR_MISSING_RESOURCE + key;
	}
}
