package de.esg.xls2sap.gui;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

import java.io.IOException;
import java.net.URL;

import de.esg.xls2sap.service.ServiceResult;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

@SuppressWarnings("rawtypes")
@Dependent
public abstract class BaseDialog<R, P extends BaseDialogPresenter> extends Dialog<R>
{
	@Inject
	protected ApplicationRegistry registry;
	@Inject
	protected LoggerExposer logger;

	protected DialogPane root;
	protected P presenter;

	protected BaseDialog()
	{
		super();
	}

	/**
	 * Jeder konkrete Dialog muss diese Methode als @PostConstruct implementieren
	 */
	abstract protected void initDialog();

	@SuppressWarnings("unchecked")
	protected void initBaseDialog(final P presenter)
	{
		// Initiale Standard-Methoden
		this.createGui();
		this.initBehavior();
		this.initDatabinding();
		this.initActionHandler();

		// Reihenfolge der Buttons fest auf Windows-Layout setzen
		final ButtonBar buttonBar = (ButtonBar) this.getDialogPane().lookup(".button-bar");
		buttonBar.setButtonOrder(ButtonBar.BUTTON_ORDER_WINDOWS);

		// CSS-Stylesheet laden
		this.root.getStylesheets().add("/styles/xls2sap.css");

		// Presenter/View koppeln
		this.presenter = presenter;
		this.presenter.setDialog(this);
	}

	protected abstract void initActionHandler();

	protected abstract void initBehavior();

	protected abstract void createGui();

	protected abstract void initDatabinding();

	public DialogPane getRoot()
	{
		return this.root;
	}

	private void loadFxmlAndSetController(final String fxmlResPath, final BaseDialog<R, P> dlg) throws IOException
	{
		final URL fxmlUrl = this.getClass().getResource(fxmlResPath);
		final FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
		fxmlLoader.setController(dlg);
		this.root = fxmlLoader.load();
	}

	@SuppressWarnings("unchecked")
	protected void createGuiFromFxml(final String fxmlResPath, final BaseDialog dlg, final String title, final String headerText)
	{
		try
		{
			this.loadFxmlAndSetController(fxmlResPath, dlg);
			this.setDialogPane(this.root);
			this.setTitle(title);
			this.root.setHeaderText(headerText);
		}
		catch (final Exception ex)
		{
			this.logger.fatalErrorConsumer().accept(ex);
		}
	}

	@SuppressWarnings("unchecked")
	public void setPresenter(final P presenter)
	{
		this.presenter = presenter;
		this.presenter.setDialog(this);
	}

	public void showError(final ServiceResult<?> result)
	{
		var msg = result.userMsg();
		if (msg.isBlank())
		{
			if (result.httpCode() == HTTP_UNAVAILABLE)
			{
				msg = "Service nicht erreichbar (404)";
			}
			else if (result.httpCode() == HTTP_INTERNAL_ERROR)
			{
				msg = "Fehler am Server (500)";
			}
			else if (result.httpCode() == HTTP_UNAUTHORIZED)
			{
				msg = "Fehlende Berechtigung (401)";
			}
			else if (result.httpCode() == HTTP_FORBIDDEN)
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

	public void showInfo(final String header, final String content)
	{
		final Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Info-Dialog");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

}
