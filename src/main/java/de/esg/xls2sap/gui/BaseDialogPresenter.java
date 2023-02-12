package de.esg.xls2sap.gui;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@SuppressWarnings("rawtypes")
@Dependent
public abstract class BaseDialogPresenter<D extends BaseDialog>
{
	protected D dialog;

	@Inject
	protected ApplicationRegistry registry;
	@Inject
	protected LoggerExposer logger;

	protected BaseDialogPresenter()
	{
		super();
	}

	public void setDialog(final D dialog)
	{
		this.dialog = dialog;
		this.dialogInitialized();
	}

	protected abstract void dialogInitialized();
}
