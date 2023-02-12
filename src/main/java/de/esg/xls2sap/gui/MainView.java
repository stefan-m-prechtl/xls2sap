package de.esg.xls2sap.gui;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import de.esg.xls2sap.gui.help.HelpDialog;
import de.esg.xls2sap.model.Attribute;
import de.esg.xls2sap.model.Material;
import de.esg.xls2sap.model.Rohmaterial;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.validation.ConstraintViolation;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;

/**
 *
 * @author Stefan Prechtl
 */
@ApplicationScoped
@SuppressWarnings("ucd")
public class MainView extends BaseView<BorderPane, MainPresenter>
{
	// Labels für Statuszeile
	@FXML
	private Label lblCurrentUser;
	@FXML
	private Label lblStatusZeile;

	// Hauptmenü "Anwendung"
	@FXML
	private MenuItem mnuExitFile;

	// Hauptmenü "Anzeige"
	@FXML
	private MenuItem mnuXLSData;
	@FXML
	private MenuItem mnuSAPData;

	// Hauptmenü "Hilfe"
	@FXML
	private MenuItem mnuAboutHelp;
	@FXML
	private MenuItem mnuOpenHelp;

	// Toolbar
	@FXML
	private Button btnLoad, btnCheck, btnImport;

	// Linke Seite
	@FXML
	private TextField txtFile;
	@FXML
	private ListView<Rohmaterial> lvwRowdata;
	@FXML
	private TitledPane panFehler;
	@FXML
	private TableView<ConstraintViolation<Rohmaterial>> tvwFehler;
	@FXML
	private TableColumn<ConstraintViolation<Rohmaterial>, String> colFehlerAttribut;
	@FXML
	private TableColumn<ConstraintViolation<Rohmaterial>, String> colFehlerMeldung;

	// Mittlere Seite
	@FXML
	private StackPane panCenter;
	@FXML
	private AnchorPane panRowData;
	@FXML
	private AnchorPane panSAPData;

	@FXML
	private TableView<Rohmaterial> tvwRawData;

	@FXML
	private TextField txtMaterialart, txtLagerort;

	@FXML
	TabPane panSichten;
	@FXML
	private Tab tabGrunddatenEins;
	@FXML
	private Tab tabKlassifizierung;
	@FXML
	private Tab tabEinkauf;

	@FXML
	private TableView<Attribute<?>> tvwGrunddatenEins;
	@FXML
	private TableColumn<Attribute<?>, String> colGrunddatenFeld;
	@FXML
	private TableColumn<Attribute<?>, String> colGrunddatenWert;

	@FXML
	private TableView<Attribute<?>> tvwEinkauf;
	@FXML
	private TableColumn<Attribute<?>, String> colEinkaufFeld;
	@FXML
	private TableColumn<Attribute<?>, String> colEinkaufWert;

	public enum TreeType
	{
		BRUTTO, NETTO, NONE;
	}

	public MainView()
	{
		super();
	}

	@Override
	@PostConstruct
	protected void initialize()
	{
		this.initBaseView(CDI.CONTAINER.getType(MainPresenter.class));
	}

	@Override
	protected void createGui()
	{
		try
		{
			this.root = this.loadFxmlAndSetController("/fxml/main.fxml", this);

			this.panRowData.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
			this.panRowData.toFront();

		}
		catch (final Exception ex)
		{
			this.logger.fatalErrorConsumer().accept(ex);
		}
	}

	@Override
	protected void initDatabinding()
	{
	}

	@Override
	protected void initBehavior()
	{
		this.btnLoad.setDisable(false);
		this.btnCheck.setDisable(true);
		this.btnImport.setDisable(true);

		this.txtFile.setDisable(true);

		this.panFehler.setExpanded(false);
		this.panFehler.setPrefHeight(25.0);

		this.lvwRowdata.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvwRowdata.setCellFactory(lv -> new ListCell<Rohmaterial>()
		{
			@Override
			public void updateItem(final Rohmaterial rawdata, final boolean empty)
			{
				super.updateItem(rawdata, empty);
				this.setText(null);
				if (!empty)
				{
					this.setText(rawdata.getBezeichnung());
					if (rawdata.hasError())
					{
						this.setTextFill(Paint.valueOf(Color.RED.toString()));
					}
					else
					{
						this.setTextFill(Paint.valueOf(Color.BLACK.toString()));
					}
				}
			}
		});

		this.colFehlerAttribut.setCellValueFactory(v -> new ReadOnlyObjectWrapper<>(v.getValue().getInvalidValue().toString()));
		this.colFehlerMeldung.setCellValueFactory(v -> new ReadOnlyObjectWrapper<>(v.getValue().getMessage()));

		this.txtMaterialart.setDisable(true);
		this.txtLagerort.setDisable(true);

		// Spalten der Tabellen in den Sichten konfigurieren
		this.colGrunddatenFeld.setCellValueFactory(v -> v.getValue().getNameProperty());
		this.colGrunddatenWert.setCellValueFactory(v -> v.getValue().getValueProperty().asString());
		this.colEinkaufFeld.setCellValueFactory(v -> v.getValue().getNameProperty());
		this.colEinkaufWert.setCellValueFactory(v -> v.getValue().getValueProperty().asString());

		// Shortcuts für Menueinträge
		// this.mnuSearchElement.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
	}

	@Override
	protected void initActionHandler()
	{
		// Hauptmenü
		this.mnuExitFile.setOnAction(e -> this.presenter.exit());

		this.mnuXLSData.setOnAction(e -> this.showXLSView());
		this.mnuSAPData.setOnAction(e -> this.showSAPView());

		this.mnuAboutHelp.setOnAction(e -> this.showHelp());

		// Mainview
		this.btnLoad.setOnAction(e -> this.loadFile());
		this.btnCheck.setOnAction(e -> this.checkRawData());

	}

	@Override
	protected void initActionListener()
	{
		this.lvwRowdata.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {

			if (this.panSAPData.isVisible())
			{
				this.presenter.convertRawToSAP(nv);
			}
			else
			{
				this.panFehler.setExpanded(nv.hasError());
				this.tvwFehler.getItems().clear();
				if (nv.hasError())
				{
					this.tvwFehler.getItems().addAll(this.presenter.checkRawData(nv));
				}
			}
		});

		this.panFehler.expandedProperty().addListener((ob, ov, nv) -> {
			this.panFehler.setPrefHeight((nv) ? 500.0 : 25.0);
		});
	}

	private void showSAPView()
	{
		this.switchToSAPData();
	}

	private void showXLSView()
	{
		this.switchToRawData();
	}

	// ##### Action Handler ####

	private void loadFile()
	{
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Datei auswählen");
		final FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
		fileChooser.getExtensionFilters().add(extFilter);
		final File file = fileChooser.showOpenDialog(this.root.getScene().getWindow());
		if ((null != file) && file.exists())
		{
			this.switchToRawData();

			this.txtFile.setText(file.getName());
			this.presenter.loadImportFile(file);
		}
	}

	private void checkRawData()
	{
		final Map<String, Set<ConstraintViolation<Rohmaterial>>> checkRawData = this.presenter.checkAllRawData();
		final Optional<Set<ConstraintViolation<Rohmaterial>>> findAny = checkRawData.values().stream().filter(v -> v.size() > 0).findAny();

		if (findAny.isEmpty())
		{
			this.showInfo("Prüfung", "Die Prüfung der Importdaten war erfolgreich");
			this.switchToSAPData();
		}
		else
		{
			this.showError("Prüfung", "Die Prüfung der Importdaten war nicht erfolgreich");
			this.lvwRowdata.refresh();
		}
	}

	private void switchToRawData()
	{
		this.btnCheck.setDisable(true);
		this.btnImport.setDisable(true);

		this.panRowData.setVisible(true);
		this.panRowData.toFront();
		this.panSAPData.setVisible(false);
		this.panSAPData.toBack();
	}

	private void switchToSAPData()
	{
		this.btnImport.setDisable(true);

		this.panRowData.setVisible(false);
		this.panRowData.toBack();
		this.panSAPData.setVisible(true);
		this.panSAPData.toFront();
	}

	private void showHelp()
	{
		final HelpDialog dlg = CDI.CONTAINER.getType(HelpDialog.class);
		dlg.showAndWait();
	}

	/**
	 * Aktuell angemeldeten Benutzer in Statuszeile anzeigen.
	 */
	void showCurrentUser()
	{
		this.lblCurrentUser.setText(this.registry.getUsername());
		this.lblStatusZeile.setText("");
	}

	void showMessage(final String message)
	{
		this.lblStatusZeile.setText(message);
	}

	public void onThrowableEvent(@Observes final Throwable event)
	{
		Platform.runLater(() -> this.lblStatusZeile.setText("Fehler: " + event.getMessage()));
	}

	public void showRawData(final List<Rohmaterial> rohmaterialien)
	{
		final ObservableList<Rohmaterial> list = FXCollections.observableArrayList();
		list.addAll(rohmaterialien);

		this.lvwRowdata.getItems().clear();
		this.lvwRowdata.setItems(list);

		this.tvwRawData.getColumns().clear();
		final List<String> csvHeaders = Rohmaterial.getHeaders();
		csvHeaders.forEach(header -> {

			final var col = new TableColumn<Rohmaterial, String>(header);
			col.setCellValueFactory(v -> v.getValue().getProperty(header));

			this.tvwRawData.getColumns().add(col);
		});
		this.tvwRawData.getItems().clear();
		this.tvwRawData.getItems().addAll(list);

		this.btnCheck.setDisable(false);
	}

	public void showSAPMaterial(final Material sapmaterial)
	{
		this.txtMaterialart.setText(sapmaterial.getMaterialart().getValue().toString());
		this.txtLagerort.setText(sapmaterial.getLagerort().getValue());

		final var sichtGrunddatenEins = sapmaterial.getGrunddaten1();
		final List<Attribute<?>> attributesGrunddaten = sichtGrunddatenEins.getAttributes();
		this.tvwGrunddatenEins.getItems().clear();
		this.tvwGrunddatenEins.getItems().addAll(FXCollections.observableArrayList(attributesGrunddaten));

		final var sichtEinkauf = sapmaterial.getEinkauf();
		final List<Attribute<?>> attributesEinkauf = sichtEinkauf.getAttributes();
		this.tvwEinkauf.getItems().clear();
		this.tvwEinkauf.getItems().addAll(FXCollections.observableArrayList(attributesEinkauf));

	}

}
