package de.esg.xls2sap.gui.login;

import de.esg.xls2sap.gui.BaseDialog;
import de.esg.xls2sap.gui.CDI;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.util.Pair;

@Dependent
@SuppressWarnings("PMD.OneDeclarationPerLine")
public class LoginDialog extends BaseDialog<Pair<Boolean, String>, LoginDialogPresenter>
{
	@FXML
	private ButtonType btnLogin, btnAbbrechen;
	@FXML
	private TextField txtKennung;
	@FXML
	private PasswordField txtPasswort;

	private String loginResult;

	public LoginDialog()
	{
		super();
	}

	@Override
	@PostConstruct
	protected void initDialog()
	{
		this.initBaseDialog(CDI.CONTAINER.getType(LoginDialogPresenter.class));

		// TODO nur während Entwicklung, später entfernen!
		this.txtKennung.setText("admin");
		this.txtPasswort.setText("geheim123");
	}

	@Override
	protected void createGui()
	{
		this.createGuiFromFxml("/fxml/login.fxml", this, "Login SAP-Importer", "Login mit SAP-Kennung");
		this.root.lookupButton(this.btnLogin).setDisable(true);
	}

	@Override
	protected void initBehavior()
	{
		// Bindings für aktiv/deaktiv
		final BooleanBinding validKennung = this.txtKennung.textProperty() //
				.isNotEmpty() //
				.and(this.txtKennung.textProperty().length().greaterThan(2));
		final BooleanBinding validPasswort = this.txtKennung.textProperty() //
				.isNotEmpty() //
				.and(this.txtPasswort.textProperty().length().greaterThan(6));

		this.txtPasswort.disableProperty().bind(validKennung.not());

		final var loginButton = (Button) this.root.lookupButton(this.btnLogin);
		loginButton.disableProperty().bind(validKennung.and(validPasswort).not());

		this.setResultConverter(dialogButton -> {
			if (dialogButton == this.btnLogin)
			{
				return new Pair<Boolean, String>(Boolean.TRUE, this.loginResult);
			}
			return new Pair<Boolean, String>(Boolean.FALSE, "");
		});

		// CSS-Gestaltung
		this.getDialogPane().getStyleClass().add("loginDialog");
		final Button abbrechenButton = (Button) this.root.lookupButton(this.btnAbbrechen);
		// Effekte
		final Effect effect = new DropShadow(2, 3, 3, Color.BLACK);
		loginButton.setEffect(effect);
		abbrechenButton.setEffect(effect);

		// set focus
		Platform.runLater(() -> this.txtKennung.requestFocus());
	}

	@Override
	protected void initActionHandler()
	{
		final var loginButton = (Button) this.root.lookupButton(this.btnLogin);
		loginButton.addEventFilter(ActionEvent.ACTION, event -> this.handleLogin(event));
	}

	@Override
	protected void initDatabinding()
	{

	}

	// ##### Action Handler ####
	private void handleLogin(final ActionEvent event)
	{
		// Login am Server per REST-Service durchführen: Ergebnis ist ein Json-Web-Token (JWT)
		final var token = this.presenter.executeLogin(this.txtKennung.getText(), this.txtPasswort.getText());

		// Ergebnis - Dialog nicht schliessen
		if (token.isEmpty())
		{
			event.consume();
		}
		else // Ergebnis speichern und Dialog schliessen
		{
			this.loginResult = token;
		}
	}
}