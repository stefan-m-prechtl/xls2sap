package de.esg.xls2sap.gui.login;

import de.esg.xls2sap.gui.BaseDialogPresenter;
import jakarta.enterprise.context.Dependent;

@Dependent
public class LoginDialogPresenter extends BaseDialogPresenter<LoginDialog>
{
	public LoginDialogPresenter()
	{
		super();
	}

	@Override
	protected void dialogInitialized()
	{
		this.registry.putUsername("");
	}

	String executeLogin(final String kennung, final String passwort)
	{
//		// Login am Server per REST-Service durchführen: Ergebnis ist ein Json-Web-Token (JWT)
//		final ServiceResult<String> serviceResult = null;
//		if (serviceResult.httpCode() != HTTP_OK)
//		{
//			this.dialog.showError(serviceResult);
//		}
//
//		final var token = serviceResult.data();
//		if (token.isEmpty())
//		{
//			this.registry.putUsername("Keine gültiger Benutzer");
//			this.logger.importWarningConsumer().accept("Keine gültige Anmeldung");
//		}
//		else
//		{
//			final Long benutzerId = 1L;
//			this.registry.putUserId(benutzerId);
//			this.registry.putUsername(kennung);
//		}
//
//		return token;

		return "ok";
	}
}
