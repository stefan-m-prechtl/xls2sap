package de.esg.xls2sap;

import de.esg.xls2sap.gui.FxApp;

/**
 * Zentrale Klassifizierungsklasse mit der GUI-unabhängigen main() Methode für den Start der GUI-abhängigen main() Methode. </br>
 * Der Client wird mit Hilfe dieser Klassifizierungsklasse gestartet.
 * 
 * @author Stefan Prechtl
 *
 */
@SuppressWarnings("ucd")
public final class App
{
	private App()
	{
		// nothing to do!
	}

	public static void main(final String[] args)
	{
		FxApp.main(args);
	}
}
