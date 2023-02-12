package de.esg.xls2sap.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Attribute<T>
{
	private StringProperty name;
	private ObjectProperty<T> value;
	private boolean mandatory;

	public Attribute(final String name, final T value)
	{
		super();

		this.name = new SimpleStringProperty(name);
		this.value = new SimpleObjectProperty<>(value);
		this.mandatory = false;
	}

	public StringProperty getNameProperty()
	{
		return this.name;
	}

	public ObjectProperty<T> getValueProperty()
	{
		return this.value;
	}

	public String getName()
	{
		return this.name.get();
	}

	public void setName(final String name)
	{
		this.name.set(name);
	}

	public T getValue()
	{
		return this.value.get();
	}

	public void setValue(final T value)
	{
		this.value.set(value);
	}

	public boolean isMandatory()
	{
		return this.mandatory;
	}

	public void setMandatory(final boolean mandatory)
	{
		this.mandatory = mandatory;
	}

}
