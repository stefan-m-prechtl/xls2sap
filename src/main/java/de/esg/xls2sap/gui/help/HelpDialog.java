package de.esg.xls2sap.gui.help;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

//#############################################################
//Klassifizierungsklasse Dialog ist wegen final-Attributen nicht ApplicationScoped
//#############################################################
//@ApplicationScoped

@Dependent
public class HelpDialog extends Dialog<String>
{

	public HelpDialog()
	{
		super();
	}

	@PostConstruct
	@SuppressWarnings("PMD.UnusedPrivateMethod")
	private void initDialog()
	{
		this.setTitle("Help for Demo");
		this.setResizable(false);
		this.setHeaderText("Header information...");

		final ButtonType closeButtonType = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
		this.getDialogPane().getButtonTypes().add(closeButtonType);
		this.getDialogPane().setContentText("The real help is still missing... Ask the developer...");
	}
}
