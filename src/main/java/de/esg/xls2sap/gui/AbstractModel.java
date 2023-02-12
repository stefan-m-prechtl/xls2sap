package de.esg.xls2sap.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class AbstractModel<T>
{
	protected ObjectProperty<T> domainObjProperty;

	public AbstractModel()
	{
		this.domainObjProperty = new SimpleObjectProperty<>();
	}

	public AbstractModel(final T domainObj)
	{
		this.domainObjProperty = new SimpleObjectProperty<>(domainObj);
	}

	public abstract void resetModel();

	public abstract T updateDomainObj();

	public ObjectProperty<T> getDomainObjProperty()
	{
		return this.domainObjProperty;
	}

	// Convenience-Methode für Zugriff auf Domainen-Objekt
	public T getDomainObject()
	{
		return this.domainObjProperty.get();
	}

	@Override
	public int hashCode()
	{
		throw new UnsupportedOperationException("Abgeleitete Klassifizierungsklasse hat keine Implementierung von 'hashCode()'");
	}

	@Override
	public boolean equals(final Object obj)
	{
		throw new UnsupportedOperationException("Abgeleitete Klassifizierungsklasse hat keine Implementierung von 'equals()'");
	}

	@Override
	// Jede Subklasse muss toString() selbst implementieren!
	public String toString()
	{
		throw new UnsupportedOperationException("Abgeleitete Klassifizierungsklasse hat keine Implementierung von 'toString()'");
	}

}
