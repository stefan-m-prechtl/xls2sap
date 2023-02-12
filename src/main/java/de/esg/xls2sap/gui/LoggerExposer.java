/*******************************************************
 * File         : LoggerExposer.java
 * Author       : dev
 * Date         : Feb 15, 2022
 * Company      : ESG
 * Project      : UVA-Tool
 * Copyright (C) 2022 ESG, Fürstenfeldbruck, Germany.
 * -----------------------------------------------------
 * Comments     :
 *
 *
 *******************************************************/
package de.esg.xls2sap.gui;

import java.util.function.Consumer;

import org.tinylog.Logger;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

/**
 * @author Stefan Prechtl
 *
 */
@Dependent
public class LoggerExposer
{
	@Produces
	public Consumer<Throwable> fatalErrorConsumer()
	{
		return LoggerExposer::printThrowable;
	}

	@Produces
	public Consumer<String> importWarningConsumer()
	{
		return LoggerExposer::printWarning;
	}

	@SuppressWarnings({ "PMD.SystemPrintln", "PMD.GuardLogStatement" })
	private static void printThrowable(final Throwable t)
	{
		Logger.error(t, "Fataler Fehler: {}", t.getMessage());
	}

	@SuppressWarnings("PMD.SystemPrintln")
	private static void printWarning(final String warning)
	{
		Logger.warn(warning);
	}
}
