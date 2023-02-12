package de.esg.xls2sap.gui;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * CDI-Container als Singleton gekapselt.
 *
 * @author Stefan Prechtl
 */
public enum CDI
{
	CONTAINER;

	private WeldContainer container;

	CDI()
	{
		final Weld weld = new Weld();
		this.container = weld.initialize();
	}

	public boolean isRunning()
	{
		return this.container.isRunning();
	}

	void shutdown()
	{
		if (this.container.isRunning())
		{
			this.container.close();
		}
	}

	public <T extends Object> T getType(final Class<T> clazz)
	{
		final T result = this.container.select(clazz).get();
		return result;
	}

}
