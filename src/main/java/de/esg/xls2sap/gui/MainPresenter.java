package de.esg.xls2sap.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.opencsv.bean.CsvToBeanBuilder;

import de.esg.xls2sap.model.Material;
import de.esg.xls2sap.model.Material.Materialart;
import de.esg.xls2sap.model.Rohmaterial;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolation;
import javafx.application.Platform;

/**
 *
 * @author Stefan Prechtl
 */
@ApplicationScoped
@SuppressWarnings("ucd")
public class MainPresenter extends BasePresenter<MainView>
{
	private static final char CSV_SEPARATOR = ';';

	private List<Rohmaterial> rohmaterialien;

	public MainPresenter()
	{
		super();
		this.rohmaterialien = new ArrayList<>(5000);
	}

	@Override
	protected void viewInitialized()
	{
		// Aktuellen Benutzer in Statuszeile anzeigen
		this.view.showCurrentUser();

		// Benutzer zur Auswahl einer Datei auffordern
		this.view.showMessage("Bitte wählen Sie eine Importdatei aus!");
	}

	void exit()
	{
		Platform.exit();
	}

	void loadImportFile(final File file)
	{
		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)))
		{
			final String headerLine = in.readLine();
			var header = headerLine.trim();
			header = header.replaceAll(" ", "");
			header = header.replaceAll("\u00A0", "");
			header = header.replaceAll("\ufeff", "");
			final List<String> headers = Arrays.asList(header.split(String.valueOf(CSV_SEPARATOR)));

			if (this.verifyHeader(headers))
			{
				this.rohmaterialien = this.parseDataToObjects(file);
				this.view.showMessage("Importdatei erfolgreich geladen!");
				this.view.showRawData(this.rohmaterialien);
			}
			else
			{
				this.view.showMessage("Importdatei wegen Fehler in den Spaltenüberschriften nicht geladen!");
			}

		}
		catch (final Exception e)
		{
			this.view.showError("Fehler", e.getLocalizedMessage());
			this.view.showMessage("Importdatei wegen Fehler nicht geladen!");

		}
	}

	private List<Rohmaterial> parseDataToObjects(final File file) throws FileNotFoundException
	{
		return new CsvToBeanBuilder<Rohmaterial>(new FileReader(file))//
				.withSeparator(CSV_SEPARATOR) //
				.withType(Rohmaterial.class)//
				.build()//
				.parse();
	}

	private boolean verifyHeader(final List<String> headers)
	{
		var result = true;
		final var expectedHeaders = Rohmaterial.getHeaders();

		for (final Iterator<String> it = headers.iterator(); it.hasNext();)
		{
			final String curHeader = it.next();
			final Optional<String> findFirst = expectedHeaders.stream().filter(header -> header.equals(curHeader)).findFirst();
			if (findFirst.isEmpty())
			{
				result = false;
			}
		}
		return result;
	}

	public void convertRawToSAP(final Rohmaterial rawdata)
	{
		final var sapmaterial = new Material();
		sapmaterial.setMaterialart(Materialart.valueOf(rawdata.getArt()));
		sapmaterial.setLagerort(rawdata.getLagerort());

		// Grunddaten 1
		final var kurztext = rawdata.getTkz() + " | " + rawdata.getBezeichnung();
		sapmaterial.getGrunddaten1().setMaterialkurztext(kurztext);
		sapmaterial.getGrunddaten1().setAltermaterialnummer(rawdata.getTkz());
		sapmaterial.getGrunddaten1().setMengeneinheit(rawdata.getMengeneinheit());
		sapmaterial.getGrunddaten1().setWarengruppe(rawdata.getWarengruppe());
		sapmaterial.getGrunddaten1().setGrundatentextDE(rawdata.getGrunddatentextDE());
		sapmaterial.getGrunddaten1().setGrundatentextEN(rawdata.getGrunddatentextEN());

		// Einkauf
		sapmaterial.getEinkauf().setEinkaeufer(rawdata.getEinkaeufer());

		this.view.showSAPMaterial(sapmaterial);

	}

	public Map<String, Set<ConstraintViolation<Rohmaterial>>> checkAllRawData()
	{
		final Map<String, Set<ConstraintViolation<Rohmaterial>>> mapIDErrors = new HashMap<>();

		this.rohmaterialien.forEach(rohmaterial -> {
			final Set<ConstraintViolation<Rohmaterial>> check = rohmaterial.check();
			mapIDErrors.put(rohmaterial.getId(), check);
		});
		return mapIDErrors;
	}

	public Set<ConstraintViolation<Rohmaterial>> checkRawData(final Rohmaterial rohmaterial)
	{
		final Set<ConstraintViolation<Rohmaterial>> check = rohmaterial.check();
		return check;
	}

}
