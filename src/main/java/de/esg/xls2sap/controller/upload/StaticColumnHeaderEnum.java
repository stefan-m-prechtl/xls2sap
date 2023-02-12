package de.esg.xls2sap.controller.upload;

enum StaticColumnHeaderEnum
{
	SystemProjekt("System/Projekt/Waffensystem"), HierarchieESG("Hierarchie/Gliederung(ESG)"), HierarchieKunde("Hierarchie/Gliederung(Kunde)"), MOI("MOI"), MOV("MOV"),
	Baugruppe("Baugruppe"), VANDFP("VAN/DFP"), Bemerkung("Bemerkung"), Spezifikation("Spezifikation"), VANKINC("VANK/INC"), NSN("NSN"), SMF("SMF"), Gewicht("Gewicht"),
	GewichtEinheit("Gewicht(Einheit)"), Volumen("Volumen"), VolumenEinheit("Volumen(Einheit)"), Menge("MengeproBezugseinheit"), MengeEinheit("Bezugseinheit/UOI"),
	QNAMJOAnzahl("QNA/MJO/Anzahl"), NIL("NILgesamt"), PNR1("Artikelnummer/PNR1"), MFC1("HERSTKO/MFC1"), IDX1("Änderungsindex1"), NATO1("Nato-Code1"), BW1("BWCode1"),
	PNR2("Artikelnummer/PNR2"), MFC2("HERSTKO/MFC2"), IDX2("Änderungsindex2"), NATO2("Nato-Code2"), BW2("BWCode2"), PNR3("Artikelnummer/PNR3"), MFC3("HERSTKO/MFC3"),
	IDX3("Änderungsindex3"), NATO3("Nato-Code3"), BW3("BWCode3");

	private String value;

	StaticColumnHeaderEnum(final String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return this.value;
	}
}
