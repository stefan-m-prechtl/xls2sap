package de.esg.xls2sap.model;

import java.util.List;

/**
 * Interface für eine SAP-Sicht Matstamm
 * 
 * @author Stefan M. Prechtl
 *
 */
public interface ISicht
{
	// Liste der SAP-Attribute für eine Sicht (z.B. Grunddaten 1, Einkauf)
	List<Attribute<?>> getAttributes();

}