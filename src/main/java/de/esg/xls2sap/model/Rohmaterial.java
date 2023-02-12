package de.esg.xls2sap.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.opencsv.bean.CsvBindByName;

import de.esg.xls2sap.model.constraints.ValidLagerort;
import de.esg.xls2sap.model.constraints.ValidMaterialart;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotEmpty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Rohmaterial
{
	// Einstieg MM01
	private static final String ID = "id";
	private static final String ART = "art";
	private static final String LAGERORT = "lagerort";
	// Sicht Grunddaten 1
	private static final String TKZ = "tkz";
	private static final String BEZEICHNUNG = "bezeichnung";
	private static final String MENGENEINHEIT = "mengeneinheit";
	private static final String WARENGRUPPE = "warengruppe";
	private static final String GRUNDDATENTEXT_EN = "grunddatentext(en)";
	private static final String GRUNDDATENTEXT_DE = "grunddatentext(de)";
	// Sicht Grunddaten Klassifizierung
	// Sicht Grunddaten Einkauf
	private static final String EINKAUEFER = "einkaeufer";
	// Sicht Grunddaten Disposition 1
	// Sicht Grunddaten Disposition 2
	// Sicht Grunddaten Disposition 3
	// Sicht Grunddaten Disposition 4
	// Sicht Grunddaten Arbeitsvorbereitung
	// Sicht Grunddaten Werksdaten/Lagerung 1
	// Sicht Grunddaten Werksdaten/Lagerung 2

	@CsvBindByName(column = ID)
	public String id;
	public StringProperty idProperty;
	@CsvBindByName(column = ART)
	private String art;
	@ValidMaterialart
	private StringProperty artProperty;
	@CsvBindByName(column = LAGERORT)
	private String lagerort;
	@ValidLagerort
	private StringProperty lagerortProperty;
	@CsvBindByName(column = TKZ)
	private String tkz;
	private StringProperty tkzProperty;
	@CsvBindByName(column = BEZEICHNUNG)
	private String bezeichnung;
	private StringProperty bezeichnungProperty;
	@CsvBindByName(column = MENGENEINHEIT)
	private String mengeneinheit;
	private StringProperty mengeneinheitProperty;
	@CsvBindByName(column = WARENGRUPPE)
	private String warengruppe;
	private StringProperty warengruppeProperty;
	@CsvBindByName(column = GRUNDDATENTEXT_DE)
	private String grunddatentextDE;
	private StringProperty grunddatentextDEProperty;
	@CsvBindByName(column = GRUNDDATENTEXT_EN)
	private String grunddatentextEN;
	private StringProperty grunddatentextENProperty;

	@CsvBindByName(column = EINKAUEFER)
	private String einkaeufer;
	private StringProperty einkaeuferProperty;

	private StringProperty unknown;

	Validator validator;
	private boolean hasError;

	public static List<String> getHeaders()
	{
		final var result = new ArrayList<String>();
		result.add(ID);
		result.add(ART);
		result.add(LAGERORT);
		result.add(TKZ);
		result.add(BEZEICHNUNG);
		result.add(MENGENEINHEIT);
		result.add(WARENGRUPPE);
		result.add(GRUNDDATENTEXT_DE);
		result.add(GRUNDDATENTEXT_EN);
		result.add(EINKAUEFER);

		return result;
	}

	public Rohmaterial()
	{
		this.hasError = false;
		this.unknown = new SimpleStringProperty("ohne Wert");

		final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		this.validator = factory.getValidator();

		// Einstieg MM01
		this.idProperty = new SimpleStringProperty("");
		this.artProperty = new SimpleStringProperty("");
		this.lagerortProperty = new SimpleStringProperty("");
		// Grundaten 1
		this.tkzProperty = new SimpleStringProperty("");
		this.bezeichnungProperty = new SimpleStringProperty("");
		this.mengeneinheitProperty = new SimpleStringProperty("");
		this.warengruppeProperty = new SimpleStringProperty("");
		this.grunddatentextDEProperty = new SimpleStringProperty("");
		this.grunddatentextENProperty = new SimpleStringProperty("");
		// Einkauf
		this.einkaeuferProperty = new SimpleStringProperty("");
	}

	public StringProperty getProperty(final String header)
	{
		switch (header)
		{
		case ID:
			return this.idProperty;
		case ART:
			return this.artProperty;
		case LAGERORT:
			return this.lagerortProperty;
		case TKZ:
			return this.tkzProperty;
		case BEZEICHNUNG:
			return this.bezeichnungProperty;
		case MENGENEINHEIT:
			return this.mengeneinheitProperty;
		case WARENGRUPPE:
			return this.warengruppeProperty;
		case GRUNDDATENTEXT_DE:
			return this.grunddatentextDEProperty;
		case GRUNDDATENTEXT_EN:
			return this.grunddatentextENProperty;
		case EINKAUEFER:
			return this.einkaeuferProperty;
		default:
			return this.unknown;
		}
	}

	public String getId()
	{
		return this.idProperty.get();
	}

	public void setId(final String value)
	{
		this.idProperty.set(value);
	}

	public StringProperty getIdProperty()
	{
		return this.idProperty;
	}

	public String getArt()
	{
		return this.artProperty.get();
	}

	public void setArt(final String value)
	{
		this.artProperty.set(value);
	}

	public StringProperty getArtProperty()
	{
		return this.artProperty;
	}

	public String getLagerort()
	{
		return this.lagerortProperty.get();
	}

	public void setLagerort(final String value)
	{
		this.lagerortProperty.set(value);
	}

	public StringProperty getLagerortProperty()
	{
		return this.lagerortProperty;
	}

	public String getTkz()
	{
		return this.tkzProperty.get();
	}

	public void setTkz(final String tkz)
	{
		this.tkzProperty.set(tkz);
	}

	public StringProperty getTkzProperty()
	{
		return this.tkzProperty;
	}

	@NotEmpty
	public String getBezeichnung()
	{
		return this.bezeichnungProperty.get();
	}

	public void setBezeichnung(final String value)
	{
		this.bezeichnungProperty.set(value);
	}

	public StringProperty getBezeichnungProperty()
	{
		return this.bezeichnungProperty;
	}

	public String getMengeneinheit()
	{
		return this.mengeneinheitProperty.get();
	}

	public void setMengeneinheit(final String value)
	{
		this.mengeneinheitProperty.set(value);
	}

	public StringProperty getMengeneinheitProperty()
	{
		return this.mengeneinheitProperty;
	}

	public String getWarengruppe()
	{
		return this.warengruppeProperty.get();
	}

	public void setWarengruppe(final String value)
	{
		this.warengruppeProperty.set(value);
	}

	public StringProperty getWarengruppeProperty()
	{
		return this.warengruppeProperty;
	}

	public String getGrunddatentextDE()
	{
		return this.grunddatentextDEProperty.get();
	}

	public void setGrunddatentextDE(final String value)
	{
		this.grunddatentextDEProperty.set(value);
	}

	public StringProperty getGrunddatentextDEProperty()
	{
		return this.grunddatentextDEProperty;
	}

	public String getGrunddatentextEN()
	{
		return this.grunddatentextENProperty.get();
	}

	public void setGrunddatentextEN(final String value)
	{
		this.grunddatentextENProperty.set(value);
	}

	public StringProperty getGrunddatentextENProperty()
	{
		return this.grunddatentextENProperty;
	}

	public String getEinkaeufer()
	{
		return this.einkaeuferProperty.get();
	}

	public void setEinkaeufer(final String value)
	{
		this.einkaeuferProperty.set(value);
	}

	public StringProperty getEinkaeuferProperty()
	{
		return this.einkaeuferProperty;
	}

	public Set<ConstraintViolation<Rohmaterial>> check()
	{
		final Set<ConstraintViolation<Rohmaterial>> result = this.validator.validate(this);
		this.hasError = (!result.isEmpty());

		return result;
	}

	public boolean hasError()
	{
		return this.hasError;
	}

}
