package de.esg.xls2sap.gui.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import de.esg.xls2sap.controller.upload.CsvUtils;
import de.esg.xls2sap.gui.MainView;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.util.Pair;

@Dependent
@SuppressWarnings("PMD.ExcessiveImports")
public class DataUploadDialog extends Dialog<Object>
{
	@FXML
	private Button btnChooseImportDatei;

//	@FXML
//	private ComboBox<Project> cbProject;

	@FXML
	private BorderPane rootPane;

	@FXML
	private TextField tfTreeName;

	@FXML
	private TextField tfImportDatei;

	@FXML
	private Label lblInfo;

//	@Inject
//	ProjectService projectService;

//	@Inject
//	private ImportService importService;

	private MainView parent;

	private final static String CSV_SEPARATOR = "¦";
	private final static int MAX_LENGTH = 50;
	private static final int MIN_SIZE = 1;

	public DataUploadDialog()
	{
		super();
	}

	@PostConstruct
	@SuppressWarnings("PMD.UnusedPrivateMethod")
	private void initDialog()
	{
//		Button btnImport = null;
//		try
//		{
//			final URL fxmlUrl = this.getClass().getResource("/fxml/data_upload_dialog.fxml");
//			final FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
//			fxmlLoader.setController(this);
//			this.setDialogPane(fxmlLoader.load());
//
//			final ButtonType btnCancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
//			final ButtonType btnImportData = new ButtonType("Ok", ButtonData.OK_DONE);
//			this.getDialogPane().getButtonTypes().addAll(btnCancel, btnImportData);
//			btnImport = (Button) this.getDialogPane().lookupButton(btnImportData);
//			btnImport.disableProperty().bind(this.validationSupport.invalidProperty());
//
//			// Reihenfolge der Buttons fest auf Windows-Layout setzen
//			final ButtonBar buttonBar = (ButtonBar) this.getDialogPane().lookup(".button-bar");
//			buttonBar.setButtonOrder(ButtonBar.BUTTON_ORDER_WINDOWS);
//
//			this.setTitle("Import");
//			this.initModality(Modality.APPLICATION_MODAL);
//			final ServiceResult<List<Project>> srvResult = this.projectService.loadAll();
//			this.setProjectlist(srvResult.data());
//
//		}
//		catch (final IOException ioe)
//		{
//			throw new RuntimeException(ioe);
//		}
//
//		// CSS-Stylesheet laden
//		this.getDialogPane().getStylesheets().add("/styles/uva.css");
//
//		btnImport.setOnAction(e -> {
//			try
//			{
//				this.importService.uploadImportData(this.cbProject.getSelectionModel().getSelectedItem().getId(), this.tfTreeName.getText(), this.tfImportDatei.getText());
//				final Alert a = new Alert(AlertType.INFORMATION, "Import war erfolgreich");
//				a.initOwner(this.parent.getRootWindow());
//				a.show();
//			}
//			catch (final Exception ex)
//			{
//				throw new RuntimeException(ex);
//			}
//
//		});
//
//		this.btnChooseImportDatei.setOnAction(e -> {
//			final FileChooser fileChooser = new FileChooser();
//			fileChooser.setTitle("Datein wählen");
//			final FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
//			fileChooser.getExtensionFilters().add(extFilter);
//			final File file = fileChooser.showOpenDialog(this.rootPane.getScene().getWindow());
//			if (file.exists())
//			{
//				if (!file.canRead())
//				{
//					file.setReadable(true);
//				}
//				this.lblInfo.visibleProperty().bind(Bindings.createBooleanBinding(() -> file.getName().contains(" ")));
//				final Pair<FehlerTyp, String> fehlerPair = this.validateCSV(file);
//				if (fehlerPair.getKey() == FehlerTyp.KEIN_FEHLER)
//				{
//					this.tfImportDatei.setText(file.getPath());
//				}
//				else if (fehlerPair.getKey() == FehlerTyp.SPALTEN_HEADER)
//				{
//					this.showAlert(fehlerPair.getValue());
//					this.tfImportDatei.setText("");
//				}
//				else if (fehlerPair.getKey() == FehlerTyp.TRENNZEICHEN)
//				{
//					this.showAlert(fehlerPair.getKey().getValue());
//					this.tfImportDatei.setText("");
//				}
//
//			}
//		});
	}

	private Pair<FehlerTyp, String> validateCSV(final File file)
	{
		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)))
		{
			final String headerLine = in.readLine();
			var header = headerLine.trim();
			header = header.replaceAll(" ", "");
			header = header.replaceAll("\u00A0", "");
			header = header.replaceAll("\ufeff", "");
			final String[] headers = header.split(CSV_SEPARATOR);
			final List<String> headerList = List.of(headers);
			if (headerList.size() == MIN_SIZE)
			{
				return new Pair<>(FehlerTyp.TRENNZEICHEN, "Trennzeichen Fehler");
			}
			final String errors = CsvUtils.validateColumnHeaders(headerList);
			if ((errors != null) && !errors.isEmpty())
			{
				return new Pair<>(FehlerTyp.SPALTEN_HEADER, errors);
			}

			final boolean isDateiZuLang = CsvUtils.isDateinameZuLang(file, MAX_LENGTH);

			return isDateiZuLang ? new Pair<DataUploadDialog.FehlerTyp, String>(FehlerTyp.DATEINAME_ZU_LANG, FehlerTyp.DATEINAME_ZU_LANG.getValue())
					: new Pair<>(FehlerTyp.KEIN_FEHLER, "");
		}
		catch (final Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private void showAlert(final String errors)
	{
		final Alert a = new Alert(AlertType.ERROR);
		final TextArea textArea = new TextArea(errors);
		textArea.setStyle("-fx-margin:5px");
		textArea.setPrefRowCount(3);
		a.getDialogPane().setContent(textArea);
		a.initModality(Modality.APPLICATION_MODAL);
		if ((this.btnChooseImportDatei.getScene() != null) && (this.btnChooseImportDatei.getScene().getWindow() != null))
		{
			a.initOwner(this.btnChooseImportDatei.getScene().getWindow());
		}
		else
		{
			a.initOwner(this.parent.getRootWindow());
		}
		a.showAndWait();

	}

	public void setParentView(final MainView mainView)
	{
		this.parent = mainView;
	}

	private enum FehlerTyp
	{
		KEIN_FEHLER(""), //
		SPALTEN_HEADER("Spalte nicht vorhanden"), //
		DATEINAME_ZU_LANG("Dateiname zu lang.\nDateiname darf maximal " + MAX_LENGTH + " Zeichen lang sein."), TRENNZEICHEN(
				"Falsches Trennzeichen.\nAls Trennzeichen wird nur ".concat(CSV_SEPARATOR).concat(" zugelassen.\nBitte verwenden Sie in der CSV Datei das richtiges Trennzeichen!"));

		private String value;

		FehlerTyp(final String value)
		{
			this.value = value;
		}

		String getValue()
		{
			return this.value;
		}
	}

}
