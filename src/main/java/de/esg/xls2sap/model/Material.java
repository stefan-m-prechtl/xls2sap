package de.esg.xls2sap.model;

public class Material
{
	public enum Materialart
	{
		NONE, LF, NLF;

		@Override
		public String toString()
		{
			if (this == LF)
			{
				return "Luftfahrt";
			}
			if (this == NLF)
			{
				return "Nicht-Luftfahrt";
			}
			return "";
		}

	}

	private Attribute<String> idAltsystem;
	private Attribute<Materialart> materialart;
	private Attribute<String> lagerort;

	private SichtGrunddatenEins grunddaten1;
	private SichtKlassifizierung klassifizierung;
	private SichtEinkauf einkauf;

	public Material()
	{
		this.materialart = new Attribute<>("Materialart", Materialart.NONE);
		this.lagerort = new Attribute<>("Lagerort", "");

		this.grunddaten1 = new SichtGrunddatenEins();
		this.klassifizierung = new SichtKlassifizierung();
		this.einkauf = new SichtEinkauf();
	}

	public void setMaterialart(final Materialart art)
	{
		this.materialart.setValue(art);
	}

	public void setLagerort(final String lagerort)
	{
		this.lagerort.setValue(lagerort);
	}

	// ####################################################

	public Attribute<String> getIdAltsystem()
	{
		return this.idAltsystem;
	}

	public Attribute<Materialart> getMaterialart()
	{
		return this.materialart;
	}

	public Attribute<String> getLagerort()
	{
		return this.lagerort;
	}

	public SichtGrunddatenEins getGrunddaten1()
	{
		return this.grunddaten1;
	}

	public SichtKlassifizierung getKlassifizierung()
	{
		return this.klassifizierung;
	}

	public SichtEinkauf getEinkauf()
	{
		return this.einkauf;
	}

}
