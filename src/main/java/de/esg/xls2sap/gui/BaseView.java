package de.esg.xls2sap.gui;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

import java.io.IOException;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import de.esg.xls2sap.gui.i18n.BundleUtil;
import de.esg.xls2sap.service.ServiceResult;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

/**
 * Basisklasse für alle konkreten Views.
 *
 * @author Stefan Prechtl
 *
 * @param <R> "Root"-GUI Element (in der Regel ein Pane-Objekt)
 * @param <P> zugehöriger Presenter (gemäß MVP-Pattern)
 */
@SuppressWarnings("rawtypes")
public abstract class BaseView<R extends Parent, P extends BasePresenter>
{
	protected R root;
	protected P presenter;
	protected ResourceBundle bundle;

	protected ApplicationRegistry registry;
	protected LoggerExposer logger;

	protected boolean readOnly;

	protected BaseView()
	{
		this.registry = CDI.CONTAINER.getType(ApplicationRegistry.class);
		this.logger = CDI.CONTAINER.getType(LoggerExposer.class);

		this.readOnly = false;
	}

	/**
	 * Jeder konkrete View muss diese Methode als @PostConstruct implementieren
	 */
	abstract protected void initialize();

	@SuppressWarnings("unchecked")
	protected void initBaseView(final P presenter)
	{
		// Presenter/View koppeln
		this.presenter = presenter;
		this.presenter.setView(this);
		// Initiale Standard-Methoden
		this.createGui();
		this.initBehavior();
		this.initDatabinding();
		this.initActionHandler();
		this.initActionListener();
		// Initialisierung abgeschlossen --> Presenter informieren
		this.presenter.viewInitialized();
	}

	protected abstract void initActionHandler();

	protected abstract void initActionListener();

	protected abstract void initBehavior();

	protected abstract void createGui();

	protected abstract void initDatabinding();

	protected void initBundle()
	{
		this.bundle = PropertyResourceBundle.getBundle(BundleUtil.BUNDLE_BASENAME, this.registry.getLocale());
	}

	public Parent getRoot()
	{
		return this.root;
	}

	public Window getRootWindow()
	{
		return this.getRoot().getScene().getWindow();
	}

	protected boolean isDoubleClick(final MouseEvent e)
	{
		return e.getClickCount() == 2;
	}

	/**
	 * Allgemeine Methoden zum laden der FXML-Resource eines Views und der Registierung des Controllers
	 *
	 * @param fxmlResPath Resource-Pfad zur fxml-Datei
	 * @param view        konkrete View-Klassifizierungsklasse
	 *
	 * @return Rootknoten des konkreten Views (z.B BorderPane)
	 *
	 * @throws IOException
	 */
	protected R loadFxmlAndSetController(final String fxmlResPath, final BaseView<R, P> view) throws IOException
	{
		final URL fxmlUrl = this.getClass().getResource(fxmlResPath);
		final FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
		fxmlLoader.setController(view);
		final R result = fxmlLoader.load();
		return result;
	}

	public boolean isReadOnly()
	{
		return this.readOnly;
	}

	public void setReadOnly(final boolean readOnly)
	{
		this.readOnly = readOnly;
	}

	public void showError(final ServiceResult<?> result)
	{
		var msg = result.userMsg();
		if (msg.isBlank())
		{
			if (result.httpCode() == HTTP_UNAVAILABLE)
			{
				msg = "Service nicht erreichbar (503)";
			}
			if (result.httpCode() == HTTP_NOT_FOUND)
			{
				msg = "Objekt nicht gefunden (404)";
			}
			else
				if (result.httpCode() == HTTP_INTERNAL_ERROR)
				{
					msg = "Fehler am Server (500)";
				}
				else
					if (result.httpCode() == HTTP_UNAUTHORIZED)
					{
						msg = "Fehlende Berechtigung (401)";
					}
					else
						if (result.httpCode() == HTTP_FORBIDDEN)
						{
							msg = "Zugriff verboten (403)";
						}
		}
		else
		{
			msg = msg + " (" + result.httpCode() + ")";
		}

		final Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Fehler-Dialog");
		alert.setHeaderText("Unerwarteter Fehler");
		alert.setContentText(msg);

		final String exceptionText = result.detailMsg();

		final Label label = new Label("Details:");
		final TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		final GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		alert.getDialogPane().setExpandableContent(expContent);
		alert.showAndWait();
	}

	public void showError(final String header, final String content)
	{
		final Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Fehler-Dialog");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	public void showWarning(final String header, final String content)
	{
		final Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Warn-Dialog");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	public void showInfo(final String header, final String content)
	{
		final Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Info-Dialog");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

}
