package de.esg.xls2sap.model;

import java.util.ArrayList;
import java.util.List;

public class SichtEinkauf implements ISicht
{
	private Attribute<String> einkaeufer;

	public SichtEinkauf()
	{
		this.einkaeufer = new Attribute<>("Einkäufer", "");
	}

	public void setEinkaeufer(final String value)
	{
		this.einkaeufer.setValue(value);
	}

	@Override
	public List<Attribute<?>> getAttributes()
	{
		final var result = new ArrayList<Attribute<?>>();
		result.add(this.einkaeufer);

		return result;

	}
}
