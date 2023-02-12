package de.esg.xls2sap.gui;

import jakarta.enterprise.context.Dependent;

/**
 * Basisklasse für alle konkreten Presenter.
 *
 * @author Stefan Prechtl
 *
 * @param <V> zugehöriger View (gemäß MVP-Pattern)
 */
@SuppressWarnings("rawtypes")
@Dependent
public abstract class BasePresenter<V extends BaseView>
{
	protected V view;

	protected ApplicationRegistry registry;
	protected LoggerExposer logger;

	protected BasePresenter()
	{
		super();
		this.registry = CDI.CONTAINER.getType(ApplicationRegistry.class);
		this.logger = CDI.CONTAINER.getType(LoggerExposer.class);
	}

	public void setView(final V view)
	{
		this.view = view;
	}

	public long getUserId()
	{
		final var result = this.registry.getUserId();
		return result;
	}

	/*
	 * Zeige Message in Statuszeile des Main-Views an. Für alle Presenter anwendbar.
	 */
	public void showMessage(final String message)
	{
		final MainPresenter presenterMain = CDI.CONTAINER.getType(MainPresenter.class);
		presenterMain.showMessage(message);
	}

	/*
	 * Methode wird vom zugehörigen View am Ende seiner Initialisierung in der Methode initialize() aufgerufen.
	 */
	protected abstract void viewInitialized();

	@SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
	protected void viewInitializedLazy()
	{
		// wird aufgerufen für die Presenter eines AccordionSubViews
	}
}
