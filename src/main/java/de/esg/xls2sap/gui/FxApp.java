package de.esg.xls2sap.gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Optional;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.tinylog.Logger;

import de.esg.xls2sap.gui.ApplicationProperties.PropertyName;
import de.esg.xls2sap.gui.i18n.BundleKeys;
import de.esg.xls2sap.gui.i18n.BundleUtil;
import de.esg.xls2sap.gui.login.LoginDialog;
import jakarta.inject.Inject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Pair;

public class FxApp extends Application
{
	@Inject
	private ApplicationRegistry registry;
	@Inject
	protected LoggerExposer logger;

	public static void main(final String[] args)
	{
		launch();
	}

	@Override
	public void init() throws Exception
	{
		CDI.CONTAINER.isRunning();

		// Client-seitige Registry initialiseren
		this.registry = CDI.CONTAINER.getType(ApplicationRegistry.class);

		// Aktuell nur Deutsch als GUI-Sprache
		Locale.setDefault(Locale.GERMAN);
		this.registry.putLocale(Locale.GERMAN);

		// Anwendungs-Properties aus Datei lesen und intern speichern
		final var appProperties = new ApplicationProperties();
		appProperties.readProps();
		this.registry.putApplicationProperties(appProperties);

	}

	@Override
	public void start(final Stage stage) throws Exception
	{
		Thread.setDefaultUncaughtExceptionHandler(FxApp::showError);

		try
		{
			// Login-Dialog zu erst anzeigen
			final LoginDialog dlg = CDI.CONTAINER.getType(LoginDialog.class);
			final Optional<Pair<Boolean, String>> result = dlg.showAndWait();

			// Bei fehlerhaften Login die Anwendung beenden
			if (Boolean.FALSE.equals(result.get().getKey()))
			{
				Platform.exit();
				return;
			}

			// Main-View vorbereiten und anzeigen
			final MainView view = CDI.CONTAINER.getType(MainView.class);
			final Parent parent = view.getRoot();

			final double width = this.registry.getApplicationProperties().getIntegerProperty(PropertyName.DISPLAY_WIDTH);
			final var height = this.registry.getApplicationProperties().getIntegerProperty(PropertyName.DISPLAY_HEIGHT);

			final Scene scene = new Scene(parent, width, height);
			scene.getStylesheets().add("/styles/xls2sap.css");

			final ResourceBundle resourceBundle = PropertyResourceBundle.getBundle(BundleUtil.BUNDLE_BASENAME, this.registry.getLocale());
			stage.setTitle(BundleUtil.getString(resourceBundle, BundleKeys.app_title));
			stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/AppIcon.png")));
			stage.setResizable(true);
			stage.setScene(scene);
			stage.centerOnScreen();
			stage.show();
		}
		catch (final Exception e)
		{
			Logger.error(e);
		}
	}

	@Override
	public void stop() throws Exception
	{
		CDI.CONTAINER.shutdown();
	}

	@SuppressWarnings({ "PMD.SystemPrintln", "PMD.UnusedFormalParameter" })
	private static void showError(final Thread t, final Throwable e)
	{
		if (Platform.isFxApplicationThread())
		{
			Logger.error(e);

			final Alert dlg = new Alert(AlertType.ERROR);
			dlg.setTitle("Fehler");
			dlg.setHeaderText("Es trat ein unerwarteter Fehler auf");
			dlg.setContentText("Fehlermeldung: " + e.getMessage());

			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			final String exceptionText = sw.toString();

			final Label label = new Label("Stacktrace:");
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

			dlg.getDialogPane().setExpandableContent(expContent);
			dlg.showAndWait();

		}
		else
		{
			System.err.println("Unerwarteter Fehler: " + e.getMessage());
		}
	}

}
