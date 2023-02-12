package de.esg.xls2sap.gui;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApplicationRegistry
{
	private enum Infotyp
	{
		CURRENT_PROJECT, //
		CURRENT_TREE, //
		CURRENT_ELEMENT, //
		CURRENT_NODE, //
		USER_NAME, USER_ID, //
		JWT, PROPS, LOCALE, CURRENT_MSG, HTTPCODE
	}

	private final Map<Infotyp, Object> objMap = new HashMap<>();

	public ApplicationProperties getApplicationProperties()
	{
		return (ApplicationProperties) this.objMap.get(Infotyp.PROPS);
	}

	public void putApplicationProperties(final ApplicationProperties props)
	{
		this.objMap.put(Infotyp.PROPS, props);
	}

	public void putLocale(final Locale locale)
	{
		this.objMap.put(Infotyp.LOCALE, locale);
	}

	public Locale getLocale()
	{
		return (Locale) this.objMap.get(Infotyp.LOCALE);
	}

	public void putUsername(final String username)
	{
		this.objMap.put(Infotyp.USER_NAME, username);
	}

	public void putUserId(final Long benutzerId)
	{
		this.objMap.put(Infotyp.USER_ID, benutzerId);
	}

	public String getUsername()
	{
		return (String) this.objMap.get(Infotyp.USER_NAME);
	}

	public Long getUserId()
	{
		return (Long) this.objMap.get(Infotyp.USER_ID);
	}

	public void putCurrentHttpCode(final int httpCode)
	{
		this.objMap.put(Infotyp.HTTPCODE, httpCode);
	}

	public int getCurrentHttpCode()
	{
		if (this.objMap.containsKey(Infotyp.HTTPCODE))
		{
			return (Integer) this.objMap.get(Infotyp.HTTPCODE);
		}
		return -1;
	}

}
