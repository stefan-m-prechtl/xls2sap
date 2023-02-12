package de.esg.xls2sap.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public class SichtGrunddatenEins implements ISicht
{
	@NotEmpty
	private Attribute<String> materialkurztext;
	private Attribute<String> altematerialnummer;
	private Attribute<String> mengeneinheit;
	private Attribute<String> warengruppe;
	private Attribute<String> grundatentextDE;
	private Attribute<String> grundatentextEN;

	public SichtGrunddatenEins()
	{
		this.materialkurztext = new Attribute<>("Materialkurztext", "");
		this.altematerialnummer = new Attribute<>("Alte Materialnummer", "");
		this.mengeneinheit = new Attribute<>("Mengeneinheit", "");
		this.warengruppe = new Attribute<>("Warengruppe", "");
		this.grundatentextDE = new Attribute<>("Grundatentext DE", "");
		this.grundatentextEN = new Attribute<>("Grundatentext EN", "");
	}

	public void setMaterialkurztext(final String value)
	{
		this.materialkurztext.setValue(value);
	}

	public void setAltermaterialnummer(final String value)
	{
		this.altematerialnummer.setValue(value);
	}

	public void setMengeneinheit(final String value)
	{
		this.mengeneinheit.setValue(value);
	}

	public void setWarengruppe(final String value)
	{
		this.warengruppe.setValue(value);
	}

	public void setGrundatentextDE(final String value)
	{
		this.grundatentextDE.setValue(value);
	}

	public void setGrundatentextEN(final String value)
	{
		this.grundatentextEN.setValue(value);
	}

	@Override
	public List<Attribute<?>> getAttributes()
	{
		final var result = new ArrayList<Attribute<?>>();
		result.add(this.materialkurztext);
		result.add(this.altematerialnummer);
		result.add(this.mengeneinheit);
		result.add(this.warengruppe);
		result.add(this.grundatentextDE);
		result.add(this.grundatentextEN);

		return result;

	}
}
